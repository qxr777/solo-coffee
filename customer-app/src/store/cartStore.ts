import { defineStore } from 'pinia'

interface CartItem {
  productId: number
  productNo: string
  name: string
  price: number
  quantity: number
  imageUrl: string
  total: number
  size?: string
  temperature?: string
  addons?: number[]
}

interface CartState {
  items: CartItem[]
  loading: boolean
  error: string | null
}

export const useCartStore = defineStore('cart', {
  state: (): CartState => ({
    items: JSON.parse(localStorage.getItem('cart') || '[]'),
    loading: false,
    error: null
  }),

  getters: {
    cartItems: (state) => state.items,
    cartCount: (state) => state.items.reduce((total, item) => total + item.quantity, 0),
    cartTotal: (state) => state.items.reduce((total, item) => total + item.total, 0),
    isEmpty: (state) => state.items.length === 0
  },

  actions: {
    addToCart(item: Omit<CartItem, 'total'>) {
      // Find existing item with same customization
      const existingItem = this.items.find(i => 
        i.productId === item.productId &&
        i.size === item.size &&
        i.temperature === item.temperature &&
        JSON.stringify(i.addons) === JSON.stringify(item.addons)
      )
      
      if (existingItem) {
        existingItem.quantity += item.quantity
        existingItem.total = existingItem.price * existingItem.quantity
      } else {
        this.items.push({
          ...item,
          total: item.price * item.quantity
        })
      }
      this.saveToLocalStorage()
    },

    removeFromCart(index: number) {
      this.items.splice(index, 1)
      this.saveToLocalStorage()
    },

    updateQuantity(index: number, quantity: number) {
      const item = this.items[index]
      if (item) {
        item.quantity = Math.max(1, quantity)
        item.total = item.price * item.quantity
        this.saveToLocalStorage()
      }
    },

    clearCart() {
      this.items = []
      this.saveToLocalStorage()
    },

    saveToLocalStorage() {
      localStorage.setItem('cart', JSON.stringify(this.items))
    },

    loadFromLocalStorage() {
      const savedCart = localStorage.getItem('cart')
      if (savedCart) {
        this.items = JSON.parse(savedCart)
      }
    }
  }
})