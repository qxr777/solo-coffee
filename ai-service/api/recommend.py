from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
import uuid
import time

# Import recommendation service
from services.recommend_service import RecommendationService

router = APIRouter()
recommend_service = RecommendationService()

# Pydantic models for request and response
class RecommendRequest(BaseModel):
    user_id: int
    store_id: int
    count: int = 10
    context: Optional[Dict[str, Any]] = None

class ProductRecommend(BaseModel):
    product_id: int
    product_name: str
    score: float
    reason: Optional[str] = None

class RecommendResponse(BaseModel):
    products: List[ProductRecommend]
    request_id: str
    timestamp: int

class PromotionRecommend(BaseModel):
    promotion_id: int
    promotion_name: str
    score: float
    reason: Optional[str] = None

class PromotionResponse(BaseModel):
    promotions: List[PromotionRecommend]
    request_id: str
    timestamp: int

class CombinationRecommend(BaseModel):
    combination_id: int
    products: List[Dict[str, Any]]
    score: float
    reason: Optional[str] = None

class CombinationResponse(BaseModel):
    combinations: List[CombinationRecommend]
    request_id: str
    timestamp: int

class TrainRequest(BaseModel):
    model_type: str
    training_params: Optional[Dict[str, Any]] = None

class TrainResponse(BaseModel):
    task_id: str
    status: str
    request_id: str
    timestamp: int

class FeedbackRequest(BaseModel):
    user_id: int
    product_id: Optional[int] = None
    promotion_id: Optional[int] = None
    feedback_type: str  # positive, negative, click, purchase
    feedback_value: Optional[float] = None

class FeedbackResponse(BaseModel):
    status: str
    request_id: str
    timestamp: int

# API endpoints
@router.post("/products", response_model=RecommendResponse)
async def get_product_recommendations(request: RecommendRequest):
    """
    Get personalized product recommendations for a user
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get recommendations from service
        recommendations = recommend_service.get_product_recommendations(
            user_id=request.user_id,
            store_id=request.store_id,
            count=request.count,
            context=request.context
        )
        
        return RecommendResponse(
            products=recommendations,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/promotions", response_model=PromotionResponse)
async def get_promotion_recommendations(request: RecommendRequest):
    """
    Get personalized promotion recommendations for a user
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get recommendations from service
        recommendations = recommend_service.get_promotion_recommendations(
            user_id=request.user_id,
            store_id=request.store_id,
            count=request.count,
            context=request.context
        )
        
        return PromotionResponse(
            promotions=recommendations,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/combinations", response_model=CombinationResponse)
async def get_combination_recommendations(request: RecommendRequest):
    """
    Get personalized product combination recommendations for a user
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get recommendations from service
        recommendations = recommend_service.get_combination_recommendations(
            user_id=request.user_id,
            store_id=request.store_id,
            count=request.count,
            context=request.context
        )
        
        return CombinationResponse(
            combinations=recommendations,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/train", response_model=TrainResponse)
async def train_recommendation_model(request: TrainRequest):
    """
    Trigger model training for recommendation system
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Start training task
        task_id = recommend_service.train_model(
            model_type=request.model_type,
            training_params=request.training_params
        )
        
        return TrainResponse(
            task_id=task_id,
            status="started",
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/feedback", response_model=FeedbackResponse)
async def submit_recommendation_feedback(request: FeedbackRequest):
    """
    Submit feedback on recommendations
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Submit feedback
        recommend_service.submit_feedback(
            user_id=request.user_id,
            product_id=request.product_id,
            promotion_id=request.promotion_id,
            feedback_type=request.feedback_type,
            feedback_value=request.feedback_value
        )
        
        return FeedbackResponse(
            status="success",
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))