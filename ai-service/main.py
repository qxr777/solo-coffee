from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import uvicorn
import uuid
import time
import logging
from contextlib import asynccontextmanager

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Import API routers
from api.recommend import router as recommend_router
from api.predict import router as predict_router
from api.nlp import router as nlp_router
from api.vision import router as vision_router
from api.decision import router as decision_router

# Startup and shutdown events
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("Starting up AI services...")
    # Initialize services here
    yield
    # Shutdown
    logger.info("Shutting down AI services...")
    # Cleanup resources here

# Create FastAPI application
app = FastAPI(
    title="Solo Coffee AI Service",
    description="AI-powered services for Solo Coffee enterprise application",
    version="1.0.0",
    lifespan=lifespan
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, replace with specific origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Custom exception handler
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    request_id = str(uuid.uuid4())
    logger.error(f"Request ID: {request_id} - Error: {str(exc)}")
    
    return JSONResponse(
        status_code=500,
        content={
            "code": 500,
            "message": "Internal server error",
            "data": None,
            "timestamp": int(time.time() * 1000),
            "requestId": request_id
        }
    )

# Health check endpoint
@app.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "service": "Solo Coffee AI Service",
        "timestamp": int(time.time() * 1000)
    }

# Register API routers
app.include_router(recommend_router, prefix="/api/v1/recommend", tags=["Recommendation"])
app.include_router(predict_router, prefix="/api/v1/predict", tags=["Prediction"])
app.include_router(nlp_router, prefix="/api/v1/nlp", tags=["NLP"])
app.include_router(vision_router, prefix="/api/v1/vision", tags=["Vision"])
app.include_router(decision_router, prefix="/api/v1/decision", tags=["Decision Support"])

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)