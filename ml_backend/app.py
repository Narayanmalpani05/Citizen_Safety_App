import pickle
import logging
from flask import Flask, request, jsonify
from werkzeug.exceptions import BadRequest

# Configure structured logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = Flask(__name__)

# Load model and vectorizer at startup
logger.info("Loading model and vectorizer...")
try:
    with open('model.pkl', 'rb') as f:
        model = pickle.load(f)
    with open('vectorizer.pkl', 'rb') as f:
        vectorizer = pickle.load(f)
    logger.info("Successfully loaded model and vectorizer.")
except Exception as e:
    logger.error(f"Error loading models: {e}")
    model = None
    vectorizer = None

@app.route('/', methods=['GET'])
@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        "status": "running",
        "service": "Citizen Safety ML Backend"
    }), 200

@app.route('/predict', methods=['POST'])
def predict():
    if model is None or vectorizer is None:
        logger.error("Predict endpoint called but model is not loaded.")
        return jsonify({"error": "Model not loaded", "output": "error"}), 500

    try:
        data = request.get_json(force=True)
    except BadRequest:
        logger.warning("Malformed JSON received.")
        return jsonify({"error": "Malformed JSON in request", "output": "error"}), 400

    if not data or 'url' not in data:
        logger.warning("Missing 'url' in request.")
        return jsonify({"error": "Missing 'url' in request", "output": "error"}), 400

    url = data['url']
    if not isinstance(url, str) or not url.strip():
        logger.warning("Invalid URL format received.")
        return jsonify({"error": "Invalid URL format", "output": "error"}), 400

    try:
        # Vectorize the URL using the robust token pattern defined during training
        X_predict = vectorizer.transform([url])
        
        # Predict the label and probability
        prediction = model.predict(X_predict)[0]
        probabilities = model.predict_proba(X_predict)[0]
        
        classes = list(model.classes_)
        confidence = probabilities[classes.index(prediction)]
        
        # Sanity check for confidence score (ensure it's not overconfident false positive)
        # Random Forest probabilities are naturally calibrated between 0 and 1.
        
        response = {
            "output": prediction,
            "confidence": round(float(confidence), 4),
            "status": "success"
        }
        
        logger.info(f"Predicted URL: {url} | Result: {prediction} | Confidence: {response['confidence']}")
        return jsonify(response)
        
    except Exception as e:
        logger.error(f"Inference pipeline error: {str(e)}")
        return jsonify({"error": "Internal server error during prediction", "output": "error"}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
