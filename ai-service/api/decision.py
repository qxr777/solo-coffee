from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
import uuid
import time

# Import decision support service
from services.decision_service import DecisionService

router = APIRouter()
decision_service = DecisionService()

# Pydantic models for request and response
class PurchaseRequest(BaseModel):
    store_id: int
    current_inventory: Dict[str, int]
    sales_data: List[Dict[str, Any]]
    seasonality: Optional[str] = None

class PurchaseRecommendation(BaseModel):
    product_id: int
    product_name: str
    current_stock: int
    estimated_daily_demand: float
    recommended_quantity: int
    urgency: str  # high, medium, low
    estimated_cost: float

class PurchaseResponse(BaseModel):
    store_id: int
    recommendations: List[PurchaseRecommendation]
    total_estimated_cost: float
    request_id: str
    timestamp: int

class SchedulingRequest(BaseModel):
    store_id: int
    sales_forecast: List[Dict[str, Any]]
    employee_availability: List[Dict[str, Any]]
    store_hours: Dict[str, str]

class ShiftRecommendation(BaseModel):
    shift_start: str
    shift_end: str
    expected_sales: float
    required_staff: int
    assigned_employees: List[int]
    staff_shortage: int

class SchedulingResponse(BaseModel):
    store_id: int
    store_hours: Dict[str, str]
    shifts: List[ShiftRecommendation]
    summary: Dict[str, int]
    request_id: str
    timestamp: int

class PromotionRequest(BaseModel):
    store_id: int
    sales_data: List[Dict[str, Any]]
    inventory_data: Dict[str, int]
    seasonality: Optional[str] = None

class ProductPromotion(BaseModel):
    product_id: int
    product_name: str
    promotion_type: str
    promotion_value: str
    estimated_increase: int
    start_date: str
    end_date: str

class GeneralPromotion(BaseModel):
    promotion_type: str
    promotion_value: str
    target_audience: str
    start_date: str
    end_date: str

class PromotionResponse(BaseModel):
    store_id: int
    product_promotions: List[ProductPromotion]
    general_promotions: List[GeneralPromotion]
    request_id: str
    timestamp: int

# API endpoints
@router.post("/purchase", response_model=PurchaseResponse)
async def get_purchase_recommendations(request: PurchaseRequest):
    """
    Get intelligent purchase recommendations
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get purchase recommendations from service
        result = decision_service.purchase_decision(
            store_id=request.store_id,
            current_inventory=request.current_inventory,
            sales_data=request.sales_data,
            seasonality=request.seasonality
        )
        
        return PurchaseResponse(
            store_id=result["store_id"],
            recommendations=result["recommendations"],
            total_estimated_cost=result["total_estimated_cost"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/scheduling", response_model=SchedulingResponse)
async def get_scheduling_recommendations(request: SchedulingRequest):
    """
    Get intelligent scheduling recommendations
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get scheduling recommendations from service
        result = decision_service.scheduling_decision(
            store_id=request.store_id,
            sales_forecast=request.sales_forecast,
            employee_availability=request.employee_availability,
            store_hours=request.store_hours
        )
        
        return SchedulingResponse(
            store_id=result["store_id"],
            store_hours=result["store_hours"],
            shifts=result["shifts"],
            summary=result["summary"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/promotion", response_model=PromotionResponse)
async def get_promotion_recommendations(request: PromotionRequest):
    """
    Get intelligent promotion recommendations
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get promotion recommendations from service
        result = decision_service.promotion_decision(
            store_id=request.store_id,
            sales_data=request.sales_data,
            inventory_data=request.inventory_data,
            seasonality=request.seasonality
        )
        
        return PromotionResponse(
            store_id=result["store_id"],
            product_promotions=result["product_promotions"],
            general_promotions=result["general_promotions"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))