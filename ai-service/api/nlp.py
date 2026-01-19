from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
import uuid
import time

# Import NLP service
from services.nlp_service import NLPService

router = APIRouter()
nlp_service = NLPService()

# Pydantic models for request and response
class SegmentRequest(BaseModel):
    text: str
    language: str = "zh"

class SegmentResponse(BaseModel):
    tokens: List[str]
    request_id: str
    timestamp: int

class SentimentAnalysisRequest(BaseModel):
    text: str
    language: str = "zh"

class SentimentAnalysisResponse(BaseModel):
    sentiment: str  # positive, negative, neutral
    score: float    # 0-1之间的情感得分
    confidence: float
    request_id: str
    timestamp: int

class IntentRecognitionRequest(BaseModel):
    text: str
    language: str = "zh"

class IntentRecognitionResponse(BaseModel):
    intent: str
    confidence: float
    entities: Dict[str, Any]
    request_id: str
    timestamp: int

class EntityRecognitionRequest(BaseModel):
    text: str
    language: str = "zh"

class EntityRecognitionResponse(BaseModel):
    entities: List[Dict[str, Any]]
    request_id: str
    timestamp: int

class ChatRequest(BaseModel):
    user_id: int
    message: str
    context: Optional[Dict[str, Any]] = None
    language: str = "zh"

class ChatResponse(BaseModel):
    response: str
    intent: str
    confidence: float
    context: Optional[Dict[str, Any]] = None
    request_id: str
    timestamp: int

class CommentAnalysisRequest(BaseModel):
    comments: List[str]
    language: str = "zh"

class CommentAnalysisResponse(BaseModel):
    overall_sentiment: str
    sentiment_score: float
    comment_analyses: List[Dict[str, Any]]
    request_id: str
    timestamp: int

# API endpoints
@router.post("/segment")
async def segment_text(request: SegmentRequest):
    """
    Segment text into tokens
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get segmentation from service
        tokens = nlp_service.segment_text(
            text=request.text,
            language=request.language
        )
        
        return SegmentResponse(
            tokens=tokens,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/sentiment")
async def analyze_sentiment(request: SentimentAnalysisRequest):
    """
    Analyze sentiment of text
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get sentiment analysis from service
        result = nlp_service.analyze_sentiment(
            text=request.text,
            language=request.language
        )
        
        return SentimentAnalysisResponse(
            sentiment=result["sentiment"],
            score=result["score"],
            confidence=result["confidence"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/intent")
async def recognize_intent(request: IntentRecognitionRequest):
    """
    Recognize intent from text
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get intent recognition from service
        result = nlp_service.recognize_intent(
            text=request.text,
            language=request.language
        )
        
        return IntentRecognitionResponse(
            intent=result["intent"],
            confidence=result["confidence"],
            entities=result["entities"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/entity")
async def recognize_entities(request: EntityRecognitionRequest):
    """
    Recognize entities from text
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get entity recognition from service
        entities = nlp_service.recognize_entities(
            text=request.text,
            language=request.language
        )
        
        return EntityRecognitionResponse(
            entities=entities,
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/chat")
async def chat_with_bot(request: ChatRequest):
    """
    Chat with AI bot
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get chat response from service
        result = nlp_service.chat(
            user_id=request.user_id,
            message=request.message,
            context=request.context,
            language=request.language
        )
        
        return ChatResponse(
            response=result["response"],
            intent=result["intent"],
            confidence=result["confidence"],
            context=result["context"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/comment_analysis")
async def analyze_comments(request: CommentAnalysisRequest):
    """
    Analyze multiple comments
    """
    try:
        request_id = str(uuid.uuid4())
        timestamp = int(time.time() * 1000)
        
        # Get comment analysis from service
        result = nlp_service.analyze_comments(
            comments=request.comments,
            language=request.language
        )
        
        return CommentAnalysisResponse(
            overall_sentiment=result["overall_sentiment"],
            sentiment_score=result["sentiment_score"],
            comment_analyses=result["comment_analyses"],
            request_id=request_id,
            timestamp=timestamp
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))