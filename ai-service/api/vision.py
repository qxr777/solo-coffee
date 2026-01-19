from fastapi import APIRouter, HTTPException, UploadFile, File
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
import uuid
import time
import base64

# Import vision service
from services.vision_service import VisionService

router = APIRouter()
vision_service = VisionService()

# Pydantic models for request and response
class ImageClassifyRequest(BaseModel):
    image_url: str
    image_data: Optional[str] = None  # Base64 encoded image

class Classification(BaseModel):
    category: str
    score: float

class ImageClassifyResponse(BaseModel):
    dominant_category: str
    confidence: float
    predictions: List[Classification]
    request_id: str
    timestamp: int

class ObjectDetectRequest(BaseModel):
    image_url: str
    image_data: Optional[str] = None  # Base64 encoded image
    threshold: float = 0.5

class ObjectDetection(BaseModel):
    label: str
    score: float
    bbox: Dict[str, float]

class ObjectDetectResponse(BaseModel):
    detections: List[ObjectDetection]
    count: int
    request_id: str
    timestamp: int

class StoreLayoutRequest(BaseModel):
    image_url: str
    image_data: Optional[str] = None  # Base64 encoded image

class StoreLayoutResponse(BaseModel):
    detected_sections: List[str]
    estimated_capacity: int
    layout_quality: float
    suggestions: List[str]
    request_id: str
    timestamp: int

class ProductDisplayRequest(BaseModel):
    image_url: str
    image_data: Optional[str] = None  # Base64 encoded image

class ProductDisplayResponse(BaseModel):
    estimated_product_count: int
    display_quality: float
    variety_score: float
    suggestions: List[str]
    request_id: str
    timestamp: int

# API endpoints
@router.post("/classify", response_model=ImageClassifyResponse)
async def classify_image(request: ImageClassifyRequest):
    """
    Classify objects in an image
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get classification from service
        result = vision_service.classify_image(
            image_url=request.image_url,
            image_data=request.image_data
        )
        
        return ImageClassifyResponse(
            dominant_category=result["dominant_category"],
            confidence=result["confidence"],
            predictions=result["predictions"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/detect", response_model=ObjectDetectResponse)
async def detect_objects(request: ObjectDetectRequest):
    """
    Detect objects in an image
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get detections from service
        result = vision_service.detect_objects(
            image_url=request.image_url,
            image_data=request.image_data,
            threshold=request.threshold
        )
        
        return ObjectDetectResponse(
            detections=result["detections"],
            count=result["count"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/analyze_layout", response_model=StoreLayoutResponse)
async def analyze_store_layout(request: StoreLayoutRequest):
    """
    Analyze store layout from image
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get layout analysis from service
        result = vision_service.analyze_store_layout(
            image_url=request.image_url,
            image_data=request.image_data
        )
        
        return StoreLayoutResponse(
            detected_sections=result["detected_sections"],
            estimated_capacity=result["estimated_capacity"],
            layout_quality=result["layout_quality"],
            suggestions=result["suggestions"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/analyze_display", response_model=ProductDisplayResponse)
async def analyze_product_display(request: ProductDisplayRequest):
    """
    Analyze product display from image
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get display analysis from service
        result = vision_service.analyze_product_display(
            image_url=request.image_url,
            image_data=request.image_data
        )
        
        return ProductDisplayResponse(
            estimated_product_count=result["estimated_product_count"],
            display_quality=result["display_quality"],
            variety_score=result["variety_score"],
            suggestions=result["suggestions"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# File upload endpoint for testing
@router.post("/upload")
async def upload_image(file: UploadFile = File(...)):
    """
    Upload an image file for testing
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Read image file
        image_bytes = await file.read()
        
        # Convert to base64 for processing
        encoded_image = base64.b64encode(image_bytes).decode('utf-8')
        
        # Classify image
        result = vision_service.classify_image(
            image_url="uploaded_image",
            image_data=encoded_image
        )
        
        return {
            "result": result,
            "request_id": request_id,
            "timestamp": timestamp
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))