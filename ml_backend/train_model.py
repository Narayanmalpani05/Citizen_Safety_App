import pandas as pd
import pickle
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.calibration import CalibratedClassifierCV
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, confusion_matrix

def train_model():
    print("Loading dataset...")
    try:
        urls_data = pd.read_csv('../ML assets/dataset.csv')
    except Exception as e:
        try:
            urls_data = pd.read_csv('dataset.csv')
        except:
            print("Dataset not found.")
            return
    
    print(f"Original dataset size: {len(urls_data)}")
    
    # Clean dataset
    urls_data = urls_data.dropna(subset=['URL', 'Label'])
    urls_data = urls_data.drop_duplicates(subset=['URL'])
    
    print(f"Cleaned dataset size: {len(urls_data)}")
    
    y = urls_data["Label"]
    url_list = urls_data["URL"]

    # Using robust regex tokenizer for URLs (extracting alphanumeric tokens)
    # Ignore http, https, and www as they cause false positives due to dataset bias
    vectorizer = TfidfVectorizer(token_pattern=r'[A-Za-z0-9]+', lowercase=True, max_features=10000, stop_words=['http', 'https', 'www'])
    print("Vectorizing data...")
    X = vectorizer.fit_transform(url_list)
    
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    print("Training Calibrated LinearSVC model...")
    # CalibratedLinearSVC automatically provides well-calibrated probabilities via predict_proba
    base_clf = LinearSVC(random_state=42, class_weight='balanced', dual=False)
    clf = CalibratedClassifierCV(base_clf, cv=3)
    clf.fit(X_train, y_train)
    
    # Validation
    y_pred = clf.predict(X_test)
    
    acc = accuracy_score(y_test, y_pred)
    prec = precision_score(y_test, y_pred, pos_label='bad')
    rec = recall_score(y_test, y_pred, pos_label='bad')
    f1 = f1_score(y_test, y_pred, pos_label='bad')
    cm = confusion_matrix(y_test, y_pred, labels=['bad', 'good'])
    
    print("\n--- Model Validation Metrics ---")
    print(f"Accuracy:  {acc:.4f}")
    print(f"Precision: {prec:.4f}")
    print(f"Recall:    {rec:.4f}")
    print(f"F1-Score:  {f1:.4f}")
    print(f"Confusion Matrix (Bad/Good):\n{cm}")
    print("--------------------------------\n")
    
    print("Saving model and vectorizer...")
    with open('model.pkl', 'wb') as f:
        pickle.dump(clf, f)
        
    with open('vectorizer.pkl', 'wb') as f:
        pickle.dump(vectorizer, f)
        
    print("Training complete! Saved model.pkl and vectorizer.pkl.")

if __name__ == '__main__':
    train_model()
