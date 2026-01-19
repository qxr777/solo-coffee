import { describe, it, expect, beforeEach } from 'vitest'
import { useCartStore } from '../store/cartStore'
import { createPinia, setActivePinia } from 'pinia'

// Mock localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString()
    },
    clear: () => {
      store = {}
    },
    removeItem: (key: string) => {
      delete store[key]
    }
  }
})()

global.localStorage = localStorageMock as any

describe('CartStore', () => {
  let cartStore: ReturnType<typeof useCartStore>

  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear()
    // Create and set active Pinia instance
    const pinia = createPinia()
    setActivePinia(pinia)
    // Create a new store instance
    cartStore = useCartStore()
  })

  describe('addToCart', () => {
    it('should add new item to cart', () => {
      // Add item to cart
      cartStore.addToCart({
        productId: 1,
        productNo: 'PROD123456',
        name: 'Americano',
        price: 3.50,
        quantity: 2,
        imageUrl: ''
      })

      // Verify the item was added
      expect(cartStore.cartItems).toHaveLength(1)
      const addedItem = cartStore.cartItems[0]
      expect(addedItem).toBeDefined()
      if (addedItem) {
        expect(addedItem.productId).toBe(1)
        expect(addedItem.quantity).toBe(2)
        expect(addedItem.total).toBe(7.00)
      }
      expect(cartStore.cartCount).toBe(2)
      expect(cartStore.cartTotal).toBe(7.00)
    })

    it('should update quantity for existing item', () => {
      // Add item to cart
      cartStore.addToCart({
        productId: 1,
        productNo: 'PROD123456',
        name: 'Americano',
        price: 3.50,
        quantity: 2,
        imageUrl: ''
      })

      // Add the same item again
      cartStore.addToCart({
        productId: 1,
        productNo: 'PROD123456',
        name: 'Americano',
        price: 3.50,
        quantity: 1,
        imageUrl: ''
      })

      // Verify the quantity was updated
      expect(cartStore.cartItems).toHaveLength(1)
      const updatedItem = cartStore.cartItems[0]
      expect(updatedItem).toBeDefined()
      if (updatedItem) {
        expect(updatedItem.quantity).toBe(3)
        expect(updatedItem.total).toBe(10.50)
      }
      expect(cartStore.cartCount).toBe(3)
      expect(cartStore.cartTotal).toBe(10.50)
    })
  })

  describe('removeFromCart', () => {
    it('should remove item from cart', () => {
      // Add item to cart
      cartStore.addToCart({
        productId: 1,
        productNo: 'PROD123456',
        name: 'Americano',
        price: 3.50,
        quantity: 2,
        imageUrl: ''
      })

      // Remove item from cart using index 0 (not productId)
      cartStore.removeFromCart(0)

      // Verify the item was removed
      expect(cartStore.cartItems).toHaveLength(0)
      expect(cartStore.cartCount).toBe(0)
      expect(cartStore.cartTotal).toBe(0)
      expect(cartStore.isEmpty).toBe(true)
    })
  })

  describe('updateQuantity', () => {
    it('should update item quantity', () => {
      // Add item to cart
      cartStore.addToCart({
        productId: 1,
        productNo: 'PROD123456',
        name: 'Americano',
        price: 3.50,
        quantity: 2,
        imageUrl: ''
      })

      // Update quantity using index 0 (not productId)
      cartStore.updateQuantity(0, 5)

      // Verify the quantity was updated
      const updatedItem = cartStore.cartItems[0]
      expect(updatedItem).toBeDefined()
      if (updatedItem) {
        expect(updatedItem.quantity).toBe(5)
        expect(updatedItem.total).toBe(17.50)
      }
      expect(cartStore.cartCount).toBe(5)
      expect(cartStore.cartTotal).toBe(17.50)
    })

    it('should not allow quantity less than 1', () => {
      // Add item to cart
      cartStore.addToCart({
        productId: 1,
        productNo: 'PROD123456',
        name: 'Americano',
        price: 3.50,
        quantity: 2,
        imageUrl: ''
      })

      // Try to set quantity to 0 using index 0 (not productId)
      cartStore.updateQuantity(0, 0)

      // Verify quantity was not set below 1
      const updatedItem = cartStore.cartItems[0]
      expect(updatedItem).toBeDefined()
      if (updatedItem) {
        expect(updatedItem.quantity).toBe(1)
        expect(updatedItem.total).toBe(3.50)
      }
      expect(cartStore.cartCount).toBe(1)
    })
  })

  describe('clearCart', () => {
    it('should clear all items from cart', () => {
      // Add items to cart
      cartStore.addToCart({
        productId: 1,
        productNo: 'PROD123456',
        name: 'Americano',
        price: 3.50,
        quantity: 2,
        imageUrl: ''
      })
      cartStore.addToCart({
        productId: 2,
        productNo: 'PROD123457',
        name: 'Latte',
        price: 4.75,
        quantity: 1,
        imageUrl: ''
      })

      // Clear cart
      cartStore.clearCart()

      // Verify cart is empty
      expect(cartStore.cartItems).toHaveLength(0)
      expect(cartStore.cartCount).toBe(0)
      expect(cartStore.cartTotal).toBe(0)
      expect(cartStore.isEmpty).toBe(true)
    })
  })

  describe('localStorage integration', () => {
    it('should save cart to localStorage', () => {
      // Add item to cart
      cartStore.addToCart({
        productId: 1,
        productNo: 'PROD123456',
        name: 'Americano',
        price: 3.50,
        quantity: 2,
        imageUrl: ''
      })

      // Verify cart was saved to localStorage
      const savedCart = localStorage.getItem('cart')
      expect(savedCart).toBeTruthy()
      const parsedCart = JSON.parse(savedCart!)
      expect(parsedCart).toHaveLength(1)
      expect(parsedCart[0].productId).toBe(1)
    })

    it('should load cart from localStorage', () => {
      // Save cart to localStorage
      const cartData = [
        {
          productId: 1,
          productNo: 'PROD123456',
          name: 'Americano',
          price: 3.50,
          quantity: 2,
          imageUrl: '',
          total: 7.00
        }
      ]
      localStorage.setItem('cart', JSON.stringify(cartData))

      // Create new Pinia instance and store instance
      const newPinia = createPinia()
      setActivePinia(newPinia)
      const newCartStore = useCartStore()

      // Verify cart was loaded from localStorage
      expect(newCartStore.cartItems).toHaveLength(1)
      const loadedItem = newCartStore.cartItems[0]
      expect(loadedItem).toBeDefined()
      if (loadedItem) {
        expect(loadedItem.productId).toBe(1)
      }
      expect(newCartStore.cartCount).toBe(2)
    })
  })
})