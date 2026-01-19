import logging
from typing import List, Optional, Dict, Any
import random

# Try to import jieba, if it fails, use a simple alternative
try:
    import jieba
    JIEBA_AVAILABLE = True
except ImportError:
    JIEBA_AVAILABLE = False

logger = logging.getLogger(__name__)

class NLPService:
    def __init__(self):
        """
        Initialize the NLP service
        """
        logger.info("Initializing NLPService")
        # In a real implementation, we would load models and resources here
        self.intent_patterns = self._load_intent_patterns()
        self.entity_patterns = self._load_entity_patterns()
        self.chat_responses = self._load_chat_responses()
        
    def _load_intent_patterns(self):
        """
        Load intent patterns for intent recognition
        """
        return {
            "order": ["点", "订", "买", "要", "order", "buy", "purchase"],
            "inquiry": ["多少钱", "价格", "优惠", "折扣", "how much", "price", "cost"],
            "recommendation": ["推荐", "建议", "什么好", "what's good", "recommend", "suggest"],
            "complaint": ["不好", "差", "投诉", "抱怨", "bad", "poor", "complain"],
            "thanks": ["谢谢", "感谢", "thank", "thanks"],
            "greeting": ["你好", "您好", "hi", "hello", "hey"],
            "farewell": ["再见", "拜拜", "bye", "goodbye"],
            "store_info": ["地址", "营业时间", "在哪里", "location", "address", "hours"]
        }
    
    def _load_entity_patterns(self):
        """
        Load entity patterns for entity recognition
        """
        return {
            "product": ["咖啡", "茶", "蛋糕", "面包", "coffee", "tea", "cake", "bread"],
            "size": ["大", "中", "小", "large", "medium", "small"],
            "quantity": ["一", "二", "三", "四", "五", "1", "2", "3", "4", "5"],
            "location": ["店", "门店", "地址", "location", "store"],
            "time": ["今天", "明天", "现在", "today", "tomorrow", "now"]
        }
    
    def _load_chat_responses(self):
        """
        Load chat responses for intelligent customer service
        """
        return {
            "greeting": ["您好！欢迎光临 Solo Coffee，请问有什么可以帮助您的？", "你好！很高兴为您服务，请问您需要点什么？", "嗨！欢迎来到 Solo Coffee，有什么我可以帮忙的吗？"],
            "farewell": ["再见！期待您的下次光临！", "拜拜！祝您有愉快的一天！", "再见！感谢您的光临！"],
            "thanks": ["不客气！很高兴为您服务！", "不用谢！有任何问题随时告诉我！", "乐意效劳！"],
            "order": ["好的，请问您想点什么呢？", "没问题，您想点什么咖啡或茶？", "好的，请问您需要点什么饮品？"],
            "inquiry": ["我们的价格非常合理，您可以查看我们的菜单了解详情。", "不同产品价格不同，请问您想了解哪款产品的价格？", "我们经常有优惠活动，您可以关注我们的公众号获取最新信息。"],
            "recommendation": ["我们的招牌咖啡是 Latte，非常受欢迎！", "如果您喜欢浓郁的咖啡，推荐您尝试 Espresso。", "如果您喜欢喝茶，我们的绿茶也很不错！"],
            "complaint": ["非常抱歉给您带来不便，请问具体是什么问题呢？", "对不起，我们会努力改进的，请问您遇到了什么问题？", "非常抱歉，我们会认真处理您的反馈，请问具体情况是？"],
            "store_info": ["我们的地址是：北京市朝阳区建国路 88 号，营业时间是每天 8:00-22:00。", "我们在全国各地都有门店，请问您想了解哪个城市的门店信息？", "您可以在我们的官网或APP上查看所有门店的地址和营业时间。"],
            "default": ["抱歉，我不太理解您的意思，请问您能再说一遍吗？", "对不起，我没有听明白，请问您需要什么帮助？", "抱歉，我不太清楚，请您详细说明一下好吗？"]
        }
    
    def segment_text(self, text: str, language: str = "zh") -> List[str]:
        """
        Segment text into tokens
        """
        logger.info(f"Segmenting text: {text}")
        
        if language == "zh":
            # Use jieba for Chinese segmentation if available
            if JIEBA_AVAILABLE:
                return list(jieba.cut(text))
            else:
                # Simple alternative: split by characters
                return list(text)
        else:
            # For English, simple whitespace tokenization
            return text.split()
    
    def analyze_sentiment(self, text: str, language: str = "zh") -> Dict[str, Any]:
        """
        Analyze sentiment of text
        """
        logger.info(f"Analyzing sentiment of text: {text}")
        
        # In a real implementation, we would use a trained sentiment analysis model
        # For this sample, we'll use a simple keyword-based approach
        
        # Positive and negative keywords
        positive_keywords = ["好", "棒", "喜欢", "不错", "good", "great", "like", "love"]
        negative_keywords = ["不好", "差", "讨厌", "失望", "bad", "poor", "dislike", "hate"]
        
        # Count positive and negative keywords
        positive_count = sum(1 for keyword in positive_keywords if keyword in text)
        negative_count = sum(1 for keyword in negative_keywords if keyword in text)
        
        # Calculate sentiment score
        if positive_count > negative_count:
            sentiment = "positive"
            score = min(1.0, positive_count / (positive_count + negative_count))
        elif negative_count > positive_count:
            sentiment = "negative"
            score = min(1.0, negative_count / (positive_count + negative_count))
        else:
            sentiment = "neutral"
            score = 0.5
        
        # Calculate confidence (simplified)
        confidence = min(1.0, (positive_count + negative_count) / len(text.split()))
        
        return {
            "sentiment": sentiment,
            "score": score,
            "confidence": confidence
        }
    
    def recognize_intent(self, text: str, language: str = "zh") -> Dict[str, Any]:
        """
        Recognize intent from text
        """
        logger.info(f"Recognizing intent from text: {text}")
        
        # In a real implementation, we would use a trained intent recognition model
        # For this sample, we'll use a simple keyword-based approach
        
        # Calculate intent scores
        intent_scores = {}
        for intent, patterns in self.intent_patterns.items():
            score = sum(1 for pattern in patterns if pattern in text.lower())
            if score > 0:
                intent_scores[intent] = score
        
        # Find the intent with the highest score
        if intent_scores:
            intent = max(intent_scores, key=intent_scores.get)
            confidence = min(1.0, intent_scores[intent] / len(text.split()))
        else:
            intent = "unknown"
            confidence = 0.5
        
        # Recognize entities
        entities = self.recognize_entities(text, language)
        
        return {
            "intent": intent,
            "confidence": confidence,
            "entities": entities
        }
    
    def recognize_entities(self, text: str, language: str = "zh") -> List[Dict[str, Any]]:
        """
        Recognize entities from text
        """
        logger.info(f"Recognizing entities from text: {text}")
        
        # In a real implementation, we would use a trained entity recognition model
        # For this sample, we'll use a simple keyword-based approach
        
        entities = []
        for entity_type, patterns in self.entity_patterns.items():
            for pattern in patterns:
                if pattern in text.lower():
                    entities.append({
                        "type": entity_type,
                        "value": pattern,
                        "confidence": 0.8  # Simplified confidence score
                    })
        
        return entities
    
    def chat(self, user_id: int, message: str, context: Optional[Dict[str, Any]] = None, language: str = "zh") -> Dict[str, Any]:
        """
        Chat with AI bot for intelligent customer service
        """
        logger.info(f"Chatting with user {user_id}: {message}")
        
        # Recognize intent from message
        intent_result = self.recognize_intent(message, language)
        intent = intent_result["intent"]
        confidence = intent_result["confidence"]
        
        # Get response based on intent
        if intent in self.chat_responses:
            response = random.choice(self.chat_responses[intent])
        else:
            response = random.choice(self.chat_responses["default"])
        
        # Update context
        if not context:
            context = {}
        
        context["last_intent"] = intent
        context["last_message"] = message
        context["last_response"] = response
        
        return {
            "response": response,
            "intent": intent,
            "confidence": confidence,
            "context": context
        }
    
    def analyze_comments(self, comments: List[str], language: str = "zh") -> Dict[str, Any]:
        """
        Analyze multiple comments for sentiment and trends
        """
        logger.info(f"Analyzing {len(comments)} comments")
        
        # Analyze each comment
        comment_analyses = []
        total_score = 0
        
        for comment in comments:
            sentiment_result = self.analyze_sentiment(comment, language)
            total_score += sentiment_result["score"]
            comment_analyses.append({
                "comment": comment,
                "sentiment": sentiment_result["sentiment"],
                "score": sentiment_result["score"],
                "confidence": sentiment_result["confidence"]
            })
        
        # Calculate overall sentiment
        average_score = total_score / len(comments) if comments else 0
        
        if average_score > 0.6:
            overall_sentiment = "positive"
        elif average_score < 0.4:
            overall_sentiment = "negative"
        else:
            overall_sentiment = "neutral"
        
        return {
            "overall_sentiment": overall_sentiment,
            "sentiment_score": average_score,
            "comment_analyses": comment_analyses
        }