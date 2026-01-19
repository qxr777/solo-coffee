from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
import uuid
import time

# Import prediction service
from services.predict_service import PredictionService

router = APIRouter()
predict_service = PredictionService()

# Pydantic models for request and response
class SalesPredictRequest(BaseModel):
    store_id: int
    product_id: Optional[int] = None
    start_date: str
    end_date: str
    include_external_factors: bool = True

class SalesPrediction(BaseModel):
    date: str
    predicted_sales: float
    confidence_interval: List[float]
    external_factors: Optional[Dict[str, float]] = None

class SalesPredictResponse(BaseModel):
    predictions: List[SalesPrediction]
    request_id: str
    timestamp: int

class InventoryPredictRequest(BaseModel):
    store_id: int
    product_id: int
    start_date: str
    end_date: str
    current_inventory: float

class InventoryPrediction(BaseModel):
    date: str
    predicted_inventory: float
    recommended_reorder: float
    confidence_interval: List[float]

class InventoryPredictResponse(BaseModel):
    predictions: List[InventoryPrediction]
    request_id: str
    timestamp: int

class CustomerFlowPredictRequest(BaseModel):
    store_id: int
    start_date: str
    end_date: str
    include_external_factors: bool = True

class CustomerFlowPrediction(BaseModel):
    date: str
    predicted_flow: float
    confidence_interval: List[float]
    external_factors: Optional[Dict[str, float]] = None

class CustomerFlowPredictResponse(BaseModel):
    predictions: List[CustomerFlowPrediction]
    request_id: str
    timestamp: int

class DemandPredictRequest(BaseModel):
    store_id: int
    product_id: int
    start_date: str
    end_date: str
    include_external_factors: bool = True

class DemandPrediction(BaseModel):
    date: str
    predicted_demand: float
    confidence_interval: List[float]
    external_factors: Optional[Dict[str, float]] = None

class DemandPredictResponse(BaseModel):
    predictions: List[DemandPrediction]
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

# API endpoints
@router.post("/sales", response_model=SalesPredictResponse)
async def predict_sales(request: SalesPredictRequest):
    """
    Predict sales for a store or product
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get sales predictions from service
        predictions = predict_service.predict_sales(
            store_id=request.store_id,
            product_id=request.product_id,
            start_date=request.start_date,
            end_date=request.end_date,
            include_external_factors=request.include_external_factors
        )
        
        return SalesPredictResponse(
            predictions=predictions,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/inventory", response_model=InventoryPredictResponse)
async def predict_inventory(request: InventoryPredictRequest):
    """
    Predict inventory levels and recommend reorder quantities
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get inventory predictions from service
        predictions = predict_service.predict_inventory(
            store_id=request.store_id,
            product_id=request.product_id,
            start_date=request.start_date,
            end_date=request.end_date,
            current_inventory=request.current_inventory
        )
        
        return InventoryPredictResponse(
            predictions=predictions,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/customer_flow", response_model=CustomerFlowPredictResponse)
async def predict_customer_flow(request: CustomerFlowPredictRequest):
    """
    Predict customer flow for a store
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get customer flow predictions from service
        predictions = predict_service.predict_customer_flow(
            store_id=request.store_id,
            start_date=request.start_date,
            end_date=request.end_date,
            include_external_factors=request.include_external_factors
        )
        
        return CustomerFlowPredictResponse(
            predictions=predictions,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/demand", response_model=DemandPredictResponse)
async def predict_demand(request: DemandPredictRequest):
    """
    Predict demand for a product
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get demand predictions from service
        predictions = predict_service.predict_demand(
            store_id=request.store_id,
            product_id=request.product_id,
            start_date=request.start_date,
            end_date=request.end_date,
            include_external_factors=request.include_external_factors
        )
        
        return DemandPredictResponse(
            predictions=predictions,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/train", response_model=TrainResponse)
async def train_prediction_model(request: TrainRequest):
    """
    Trigger model training for prediction system
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Start training task
        task_id = predict_service.train_model(
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