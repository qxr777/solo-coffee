# Solo Coffee AI Service

This is the AI service layer for the Solo Coffee enterprise application system. It provides various AI-powered services including recommendation, prediction, NLP, image recognition, and decision support.

## Project Structure

```
ai-service/
├── api/             # API routes and endpoints
├── models/          # AI models and model-related code
├── services/        # Core service implementations
├── data/            # Data processing and storage
├── config/          # Configuration files
├── utils/           # Utility functions
├── main.py          # Main FastAPI application
├── requirements.txt # Python dependencies
└── README.md        # Project documentation
```

## Getting Started

1. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

2. Start the development server:
   ```bash
   uvicorn main:app --reload
   ```

3. Access the API documentation:
   ```
   http://localhost:8000/docs
   ```

## Services

- **Recommendation Service**: Provides personalized coffee and promotion recommendations
- **Prediction Service**: Offers sales, inventory, and customer flow predictions
- **NLP Service**: Handles natural language processing tasks like sentiment analysis and chat
- **Image Recognition Service**: Processes images for product recognition and quality checking
- **Decision Support Service**: Provides intelligent business recommendations

## API Endpoints

All API endpoints are prefixed with `/api/v1/` and follow RESTful design principles.