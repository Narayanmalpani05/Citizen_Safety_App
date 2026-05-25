#!/usr/bin/env bash
set -o errexit

echo "Installing Python dependencies..."
pip install -r requirements.txt

echo "Downloading model.pkl..."
curl -L "$MODEL_URL" -o model.pkl

echo "Checking model file..."
ls -lh model.pkl

echo "Build completed successfully."
