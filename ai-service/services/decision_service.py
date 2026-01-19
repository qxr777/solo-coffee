import logging
from typing import List, Dict, Any, Optional
import random
import datetime

logger = logging.getLogger(__name__)

class DecisionService:
    def __init__(self):
        """
        Initialize the decision support service
        """
        logger.info("Initializing DecisionService")
        # In a real implementation, we would load models and resources here
        self.products = self._load_products()
        self.promotion_types = self._load_promotion_types()
        
    def _load_products(self):
        """
        Load product data for decision making
        """
        return [
            {"id": 1, "name": "Espresso", "category": "coffee", "cost": 2.5, "price": 4.5},
            {"id": 2, "name": "Latte", "category": "coffee", "cost": 3.0, "price": 5.5},
            {"id": 3, "name": "Cappuccino", "category": "coffee", "cost": 3.0, "price": 5.5},
            {"id": 4, "name": "Americano", "category": "coffee", "cost": 2.0, "price": 4.0},
            {"id": 5, "name": "Green Tea", "category": "tea", "cost": 1.5, "price": 3.5},
            {"id": 6, "name": "Black Tea", "category": "tea", "cost": 1.5, "price": 3.5},
            {"id": 7, "name": "Cheesecake", "category": "cake", "cost": 3.0, "price": 6.5},
            {"id": 8, "name": "Chocolate Cake", "category": "cake", "cost": 3.0, "price": 6.5},
            {"id": 9, "name": "Croissant", "category": "bread", "cost": 1.5, "price": 3.0},
            {"id": 10, "name": "Bagel", "category": "bread", "cost": 1.5, "price": 3.0}
        ]
    
    def _load_promotion_types(self):
        """
        Load promotion types for decision making
        """
        return [
            "discount",
            "buy_one_get_one",
            "bundle",
            "free_addon",
            "loyalty_points"
        ]
    
    def purchase_decision(self, store_id: int, current_inventory: Dict[str, int], sales_data: List[Dict[str, Any]], seasonality: Optional[str] = None) -> Dict[str, Any]:
        """
        Make purchase decision based on inventory and sales data
        """
        logger.info(f"Making purchase decision for store {store_id}")
        
        # In a real implementation, we would use a predictive model to determine optimal purchase quantities
        # For this sample, we'll generate purchase recommendations based on simple rules
        
        recommendations = []
        
        for product in self.products:
            # Get current inventory level
            current_stock = current_inventory.get(str(product["id"]), 0)
            
            # Estimate demand based on sales data
            if sales_data:
                product_sales = [sale for sale in sales_data if sale.get("product_id") == product["id"]]
                avg_daily_sales = sum(sale.get("quantity", 0) for sale in product_sales) / len(product_sales) if product_sales else 5
            else:
                avg_daily_sales = 5  # Default average daily sales
            
            # Calculate recommended order quantity (14 days of supply)
            recommended_quantity = int(avg_daily_sales * 14 - current_stock)
            
            # Only recommend if quantity is positive
            if recommended_quantity > 0:
                # Calculate urgency based on current stock
                days_of_stock = current_stock / avg_daily_sales if avg_daily_sales > 0 else 0
                if days_of_stock < 3:
                    urgency = "high"
                elif days_of_stock < 7:
                    urgency = "medium"
                else:
                    urgency = "low"
                
                recommendations.append({
                    "product_id": product["id"],
                    "product_name": product["name"],
                    "current_stock": current_stock,
                    "estimated_daily_demand": round(avg_daily_sales, 2),
                    "recommended_quantity": recommended_quantity,
                    "urgency": urgency,
                    "estimated_cost": round(recommended_quantity * product["cost"], 2)
                })
        
        # Sort recommendations by urgency and then by recommended quantity
        urgency_order = {"high": 0, "medium": 1, "low": 2}
        recommendations.sort(key=lambda x: (urgency_order[x["urgency"]], -x["recommended_quantity"]))
        
        # Calculate total estimated cost
        total_cost = sum(rec["estimated_cost"] for rec in recommendations)
        
        return {
            "store_id": store_id,
            "recommendations": recommendations,
            "total_estimated_cost": round(total_cost, 2),
            "timestamp": datetime.datetime.now().isoformat()
        }
    
    def scheduling_decision(self, store_id: int, sales_forecast: List[Dict[str, Any]], employee_availability: List[Dict[str, Any]], store_hours: Dict[str, str]) -> Dict[str, Any]:
        """
        Make scheduling decision based on sales forecast and employee availability
        """
        logger.info(f"Making scheduling decision for store {store_id}")
        
        # In a real implementation, we would use an optimization algorithm to create optimal schedules
        # For this sample, we'll generate simple scheduling recommendations
        
        # Parse store hours
        opening_time = store_hours.get("opening", "08:00")
        closing_time = store_hours.get("closing", "20:00")
        
        # Generate shifts based on sales forecast
        shifts = []
        
        # For simplicity, we'll create 4-hour shifts
        shift_start_hours = ["08:00", "12:00", "16:00"]
        
        for shift_start in shift_start_hours:
            # Find sales forecast for this shift
            shift_sales = next((forecast for forecast in sales_forecast if forecast.get("time_slot") == shift_start), None)
            expected_sales = shift_sales.get("expected_sales", 500) if shift_sales else 500
            
            # Determine required staff based on expected sales
            if expected_sales > 1000:
                required_staff = 4
            elif expected_sales > 700:
                required_staff = 3
            elif expected_sales > 400:
                required_staff = 2
            else:
                required_staff = 1
            
            # Find available employees for this shift
            available_employees = [emp for emp in employee_availability if shift_start in emp.get("available_shifts", [])]
            
            # Assign employees to shift
            assigned_employees = available_employees[:required_staff]
            
            shifts.append({
                "shift_start": shift_start,
                "shift_end": self._get_shift_end(shift_start),
                "expected_sales": expected_sales,
                "required_staff": required_staff,
                "assigned_employees": [emp.get("employee_id") for emp in assigned_employees],
                "staff_shortage": max(0, required_staff - len(assigned_employees))
            })
        
        # Calculate total staff needed and assigned
        total_required = sum(shift["required_staff"] for shift in shifts)
        total_assigned = sum(len(shift["assigned_employees"]) for shift in shifts)
        total_shortage = sum(shift["staff_shortage"] for shift in shifts)
        
        return {
            "store_id": store_id,
            "store_hours": store_hours,
            "shifts": shifts,
            "summary": {
                "total_required_staff": total_required,
                "total_assigned_staff": total_assigned,
                "total_staff_shortage": total_shortage
            },
            "timestamp": datetime.datetime.now().isoformat()
        }
    
    def _get_shift_end(self, shift_start: str) -> str:
        """
        Calculate shift end time based on start time
        """
        hour = int(shift_start.split(":")[0])
        end_hour = (hour + 4) % 24
        return f"{end_hour:02d}:00"
    
    def promotion_decision(self, store_id: int, sales_data: List[Dict[str, Any]], inventory_data: Dict[str, int], seasonality: Optional[str] = None) -> Dict[str, Any]:
        """
        Make promotion decision based on sales and inventory data
        """
        logger.info(f"Making promotion decision for store {store_id}")
        
        # In a real implementation, we would use a model to determine optimal promotions
        # For this sample, we'll generate promotion recommendations based on simple rules
        
        promotions = []
        
        # Identify slow-moving products (low sales, high inventory)
        slow_moving_products = []
        
        for product in self.products:
            # Get current inventory level
            current_stock = inventory_data.get(str(product["id"]), 0)
            
            # Estimate sales rate based on sales data
            if sales_data:
                product_sales = [sale for sale in sales_data if sale.get("product_id") == product["id"]]
                avg_daily_sales = sum(sale.get("quantity", 0) for sale in product_sales) / len(product_sales) if product_sales else 1
            else:
                avg_daily_sales = 1  # Default average daily sales
            
            # Calculate days of stock
            days_of_stock = current_stock / avg_daily_sales if avg_daily_sales > 0 else 0
            
            # Consider product as slow-moving if days of stock > 21
            if days_of_stock > 21:
                slow_moving_products.append({
                    "product": product,
                    "days_of_stock": days_of_stock,
                    "avg_daily_sales": avg_daily_sales
                })
        
        # Create promotions for slow-moving products
        for item in slow_moving_products:
            product = item["product"]
            promotion_type = random.choice(self.promotion_types)
            
            if promotion_type == "discount":
                discount_percentage = random.randint(15, 30)
                promotion_value = f"{discount_percentage}% off"
            elif promotion_type == "buy_one_get_one":
                promotion_value = "Buy 1 Get 1 Free"
            elif promotion_type == "bundle":
                promotion_value = "Bundle with another product"
            elif promotion_type == "free_addon":
                promotion_value = "Free addon"
            else:  # loyalty_points
                extra_points = random.randint(5, 15)
                promotion_value = f"Extra {extra_points} loyalty points"
            
            promotions.append({
                "product_id": product["id"],
                "product_name": product["name"],
                "promotion_type": promotion_type,
                "promotion_value": promotion_value,
                "estimated_increase": random.randint(20, 50),  # Estimated sales increase in percentage
                "start_date": datetime.datetime.now().isoformat(),
                "end_date": (datetime.datetime.now() + datetime.timedelta(days=7)).isoformat()
            })
        
        # Also create some general promotions
        general_promotions = []
        
        # Add a weekend promotion
        if random.random() > 0.5:
            general_promotions.append({
                "promotion_type": "discount",
                "promotion_value": "10% off all drinks on weekends",
                "target_audience": "all_customers",
                "start_date": datetime.datetime.now().isoformat(),
                "end_date": (datetime.datetime.now() + datetime.timedelta(days=14)).isoformat()
            })
        
        # Add a loyalty promotion
        if random.random() > 0.5:
            general_promotions.append({
                "promotion_type": "loyalty_points",
                "promotion_value": "Double loyalty points on all purchases",
                "target_audience": "loyalty_members",
                "start_date": datetime.datetime.now().isoformat(),
                "end_date": (datetime.datetime.now() + datetime.timedelta(days=30)).isoformat()
            })
        
        return {
            "store_id": store_id,
            "product_promotions": promotions,
            "general_promotions": general_promotions,
            "timestamp": datetime.datetime.now().isoformat()
        }