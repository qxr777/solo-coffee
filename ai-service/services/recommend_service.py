import logging
from typing import List, Optional, Dict, Any
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import random

logger = logging.getLogger(__name__)

class RecommendationService:
    def __init__(self):
        """
        Initialize the recommendation service
        """
        logger.info("Initializing RecommendationService")
        # In a real implementation, we would load models and data here
        self.products = self._load_sample_products()
        self.users = self._load_sample_users()
        self.user_ratings = self._load_sample_ratings()
        
    def _load_sample_products(self):
        """
        Load sample product data
        """
        return [
            {"product_id": 1, "product_name": "Americano", "category": "coffee", "price": 3.5, "popularity": 0.8},
            {"product_id": 2, "product_name": "Latte", "category": "coffee", "price": 4.2, "popularity": 0.9},
            {"product_id": 3, "product_name": "Cappuccino", "category": "coffee", "price": 4.0, "popularity": 0.7},
            {"product_id": 4, "product_name": "Espresso", "category": "coffee", "price": 2.8, "popularity": 0.6},
            {"product_id": 5, "product_name": "Mocha", "category": "coffee", "price": 4.5, "popularity": 0.85},
            {"product_id": 6, "product_name": "Green Tea", "category": "tea", "price": 3.0, "popularity": 0.5},
            {"product_id": 7, "product_name": "Black Tea", "category": "tea", "price": 2.8, "popularity": 0.4},
            {"product_id": 8, "product_name": "Chai Latte", "category": "tea", "price": 3.8, "popularity": 0.65},
            {"product_id": 9, "product_name": "Croissant", "category": "pastry", "price": 2.5, "popularity": 0.7},
            {"product_id": 10, "product_name": "Muffin", "category": "pastry", "price": 2.2, "popularity": 0.6},
        ]
    
    def _load_sample_users(self):
        """
        Load sample user data
        """
        return [
            {"user_id": 1, "preferences": {"coffee": 0.9, "tea": 0.3, "pastry": 0.6}},
            {"user_id": 2, "preferences": {"coffee": 0.5, "tea": 0.8, "pastry": 0.4}},
            {"user_id": 3, "preferences": {"coffee": 0.7, "tea": 0.5, "pastry": 0.9}},
            {"user_id": 4, "preferences": {"coffee": 0.8, "tea": 0.2, "pastry": 0.3}},
            {"user_id": 5, "preferences": {"coffee": 0.4, "tea": 0.7, "pastry": 0.8}},
        ]
    
    def _load_sample_ratings(self):
        """
        Load sample user ratings
        """
        return {
            1: {1: 5, 2: 4, 3: 5, 9: 4},
            2: {6: 5, 7: 4, 8: 5, 10: 3},
            3: {1: 4, 3: 3, 5: 4, 9: 5, 10: 5},
            4: {1: 5, 2: 5, 4: 4, 5: 5},
            5: {6: 4, 8: 5, 9: 4, 10: 5},
        }
    
    def get_product_recommendations(self, user_id: int, store_id: int, count: int = 10, context: Optional[Dict[str, Any]] = None) -> List[Dict[str, Any]]:
        """
        Get personalized product recommendations for a user
        """
        logger.info(f"Getting product recommendations for user {user_id} at store {store_id}")
        
        # In a real implementation, we would use a collaborative filtering or content-based approach
        # For this sample, we'll use a simple preference-based approach
        
        # Find user preferences
        user_preferences = next((user["preferences"] for user in self.users if user["user_id"] == user_id), None)
        
        if not user_preferences:
            # If user not found, return popular products
            return self._get_popular_products(count)
        
        # Calculate scores for each product
        product_scores = []
        for product in self.products:
            # Base score on user preferences for category
            category_score = user_preferences.get(product["category"], 0.5)
            # Add popularity score
            popularity_score = product["popularity"]
            # Calculate final score
            score = (category_score * 0.7) + (popularity_score * 0.3)
            
            product_scores.append({
                "product_id": product["product_id"],
                "product_name": product["product_name"],
                "score": score,
                "reason": f"Based on your preference for {product['category']}"
            })
        
        # Sort by score and return top N
        product_scores.sort(key=lambda x: x["score"], reverse=True)
        return product_scores[:count]
    
    def get_promotion_recommendations(self, user_id: int, store_id: int, count: int = 10, context: Optional[Dict[str, Any]] = None) -> List[Dict[str, Any]]:
        """
        Get personalized promotion recommendations for a user
        """
        logger.info(f"Getting promotion recommendations for user {user_id} at store {store_id}")
        
        # Sample promotions
        promotions = [
            {"promotion_id": 1, "promotion_name": "Buy 2 Get 1 Free Coffee", "category": "coffee", "discount": 0.33},
            {"promotion_id": 2, "promotion_name": "Tea of the Day - 20% Off", "category": "tea", "discount": 0.2},
            {"promotion_id": 3, "promotion_name": "Breakfast Combo - $5.99", "category": "combo", "discount": 0.25},
            {"promotion_id": 4, "promotion_name": "Happy Hour - 15% Off All Drinks", "category": "all", "discount": 0.15},
            {"promotion_id": 5, "promotion_name": "Loyalty Member Discount - 10% Off", "category": "all", "discount": 0.1},
        ]
        
        # Find user preferences
        user_preferences = next((user["preferences"] for user in self.users if user["user_id"] == user_id), None)
        
        if not user_preferences:
            # If user not found, return random promotions
            random.shuffle(promotions)
            return [
                {"promotion_id": p["promotion_id"], "promotion_name": p["promotion_name"], "score": random.random(), "reason": "Limited time offer"}
                for p in promotions[:count]
            ]
        
        # Calculate scores for each promotion
        promotion_scores = []
        for promotion in promotions:
            # Base score on user preferences for category
            if promotion["category"] == "all":
                category_score = max(user_preferences.values())
            else:
                category_score = user_preferences.get(promotion["category"], 0.5)
            
            # Add discount score
            discount_score = promotion["discount"]
            
            # Calculate final score
            score = (category_score * 0.6) + (discount_score * 0.4)
            
            promotion_scores.append({
                "promotion_id": promotion["promotion_id"],
                "promotion_name": promotion["promotion_name"],
                "score": score,
                "reason": f"Based on your preference for {promotion['category']} items"
            })
        
        # Sort by score and return top N
        promotion_scores.sort(key=lambda x: x["score"], reverse=True)
        return promotion_scores[:count]
    
    def get_combination_recommendations(self, user_id: int, store_id: int, count: int = 10, context: Optional[Dict[str, Any]] = None) -> List[Dict[str, Any]]:
        """
        Get personalized product combination recommendations for a user
        """
        logger.info(f"Getting combination recommendations for user {user_id} at store {store_id}")
        
        # Sample combinations
        combinations = [
            {
                "combination_id": 1,
                "products": [
                    {"product_id": 1, "product_name": "Americano", "quantity": 1},
                    {"product_id": 9, "product_name": "Croissant", "quantity": 1}
                ],
                "category": "breakfast",
                "discount": 0.1
            },
            {
                "combination_id": 2,
                "products": [
                    {"product_id": 2, "product_name": "Latte", "quantity": 1},
                    {"product_id": 10, "product_name": "Muffin", "quantity": 1}
                ],
                "category": "breakfast",
                "discount": 0.1
            },
            {
                "combination_id": 3,
                "products": [
                    {"product_id": 5, "product_name": "Mocha", "quantity": 1},
                    {"product_id": 9, "product_name": "Croissant", "quantity": 1}
                ],
                "category": "dessert",
                "discount": 0.12
            },
            {
                "combination_id": 4,
                "products": [
                    {"product_id": 6, "product_name": "Green Tea", "quantity": 1},
                    {"product_id": 10, "product_name": "Muffin", "quantity": 1}
                ],
                "category": "healthy",
                "discount": 0.08
            },
        ]
        
        # Find user preferences
        user_preferences = next((user["preferences"] for user in self.users if user["user_id"] == user_id), None)
        
        if not user_preferences:
            # If user not found, return random combinations
            random.shuffle(combinations)
            return [
                {"combination_id": c["combination_id"], "products": c["products"], "score": random.random(), "reason": "Popular combination"}
                for c in combinations[:count]
            ]
        
        # Calculate scores for each combination
        combination_scores = []
        for combination in combinations:
            # Calculate average category score for products in combination
            total_score = 0
            for product in combination["products"]:
                product_info = next((p for p in self.products if p["product_id"] == product["product_id"]), None)
                if product_info:
                    total_score += user_preferences.get(product_info["category"], 0.5)
            
            avg_score = total_score / len(combination["products"])
            # Add discount score
            discount_score = combination["discount"] * 10  # Scale discount for better weighting
            
            # Calculate final score
            score = (avg_score * 0.7) + (discount_score * 0.3)
            
            combination_scores.append({
                "combination_id": combination["combination_id"],
                "products": combination["products"],
                "score": score,
                "reason": f"Based on your preferences and {int(combination['discount'] * 100)}% discount"
            })
        
        # Sort by score and return top N
        combination_scores.sort(key=lambda x: x["score"], reverse=True)
        return combination_scores[:count]
    
    def _get_popular_products(self, count: int = 10) -> List[Dict[str, Any]]:
        """
        Get popular products
        """
        # Sort products by popularity
        popular_products = sorted(self.products, key=lambda x: x["popularity"], reverse=True)
        
        # Convert to recommendation format
        return [
            {
                "product_id": product["product_id"],
                "product_name": product["product_name"],
                "score": product["popularity"],
                "reason": "Popular item"
            }
            for product in popular_products[:count]
        ]
    
    def train_model(self, model_type: str, training_params: Optional[Dict[str, Any]] = None) -> str:
        """
        Train a recommendation model
        """
        logger.info(f"Training {model_type} model with params {training_params}")
        
        # In a real implementation, we would start a training job
        # For this sample, we'll just return a mock task ID
        
        import uuid
        return str(uuid.uuid4())
    
    def submit_feedback(self, user_id: int, product_id: Optional[int] = None, promotion_id: Optional[int] = None, 
                       feedback_type: str = "positive", feedback_value: Optional[float] = None) -> None:
        """
        Submit feedback on recommendations
        """
        logger.info(f"Submitting feedback from user {user_id}: {feedback_type}")
        
        # In a real implementation, we would store this feedback and use it to improve recommendations
        # For this sample, we'll just log the feedback
        
        if product_id:
            logger.info(f"Feedback for product {product_id}: {feedback_type}")
        elif promotion_id:
            logger.info(f"Feedback for promotion {promotion_id}: {feedback_type}")