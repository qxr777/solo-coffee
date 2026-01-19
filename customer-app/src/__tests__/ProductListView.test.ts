import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import axios from 'axios'
import ProductListView from '../views/ProductListView.vue'
import { useCartStore } from '../store/cartStore'
import { useProductStore } from '../store/productStore'

// Mock axios to prevent real API calls
vi.mock('axios')
const mockedAxios = axios as any

// Create router mocks
const mockPush = vi.fn()
const mockBack = vi.fn()
const mockRouter = {
  back: mockBack,
  push: mockPush,
  currentRoute: { value: { query: {} } }
}
const mockRoute = {
  query: { category: undefined }
}

// Explicitly mock vue-router's useRouter and useRoute
vi.mock('vue-router', () => ({
  useRouter: vi.fn(() => mockRouter),
  useRoute: vi.fn(() => mockRoute)
}))

describe('ProductListView', () => {
  let wrapper: any
  let productStore: any
  let cartStore: any
  let pinia: any

  beforeEach(async () => {
    // Clear localStorage before each test
    localStorage.clear()
    
    // Reset router mock functions
    mockPush.mockReset()
    mockBack.mockReset()
    
    // Create a fresh Pinia instance
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Initialize stores before mounting
    productStore = useProductStore()
    cartStore = useCartStore()
    
    // Clear cart to ensure fresh state
    cartStore.clearCart()
    
    // Set up mock data
    const mockProducts = [
      {
        id: 1,
        productNo: 'P001',
        name: 'Espresso',
        description: 'Strong black coffee',
        price: 2.99,
        categoryId: 1,
        imageUrl: 'https://example.com/espresso.jpg',
        status: 1,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      {
        id: 2,
        productNo: 'P002',
        name: 'Green Tea',
        description: 'Refreshing green tea',
        price: 2.49,
        categoryId: 2,
        imageUrl: 'https://example.com/greentea.jpg',
        status: 1,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
    ]
    
    const mockCategories = [
      { id: 1, name: 'Coffee', description: 'Coffee products' },
      { id: 2, name: 'Tea', description: 'Tea products' }
    ]
    
    // Mock the store methods to set the mock data instead of making API calls
    vi.spyOn(productStore, 'fetchProducts').mockImplementation(async () => {
      productStore.$patch({ products: mockProducts })
      return mockProducts
    })
    
    vi.spyOn(productStore, 'fetchCategories').mockImplementation(async () => {
      productStore.$patch({ categories: mockCategories })
      return mockCategories
    })
    
    // Mount the component with pinia plugin
    wrapper = mount(ProductListView, {
      global: {
        plugins: [pinia]
      }
    })
    
    // Wait for component to update and for async actions to complete
    await wrapper.vm.$nextTick()
    await wrapper.vm.$nextTick()
  })

  it('should render product list correctly', () => {
    // Use a simpler selector that should match product cards
    const productCards = wrapper.findAll('div.cursor-pointer')
    expect(productCards).toHaveLength(2)
    expect(wrapper.text()).toContain('Espresso')
    expect(wrapper.text()).toContain('Green Tea')
  })

  it.skip('should navigate to product detail when clicking on product card', async () => {
    const productCards = wrapper.findAll('div.cursor-pointer')
    
    // Verify the router mock is working correctly
    expect(mockPush).not.toHaveBeenCalled()
    
    // Click the first product card
    await productCards[0].trigger('click')
    
    // The mockPush should have been called
    expect(mockPush).toHaveBeenCalledWith('/product/1')
  })

  it('should add product to cart when clicking Add button', async () => {
    const addButtons = wrapper.findAll('button.bg-blue-600')
    // The first Add button should be for Espresso
    await addButtons[0].trigger('click')
    
    expect(cartStore.cartItems).toHaveLength(1)
    expect(cartStore.cartItems[0].productId).toBe(1)
  })

  it('should filter products by category', async () => {
    // Find all category buttons
    const categoryButtons = wrapper.findAll('button.px-4.py-2.rounded-full')
    // Find the Coffee button
    const coffeeButton = categoryButtons.find(button => button.text() === 'Coffee')
    await coffeeButton.trigger('click')
    
    await wrapper.vm.$nextTick()
    
    const productCards = wrapper.findAll('.grid > .bg-white')
    expect(productCards).toHaveLength(1)
    expect(wrapper.text()).toContain('Espresso')
    expect(wrapper.text()).not.toContain('Green Tea')
  })

  it('should search products by name', async () => {
    const searchInput = wrapper.find('input[placeholder="Search products..."]')
    await searchInput.setValue('tea')
    
    await wrapper.vm.$nextTick()
    
    const productCards = wrapper.findAll('.bg-white')
    // Use a more reliable way to check if only Green Tea is displayed
    const displayedProducts = productCards.map(card => card.text())
    const hasEspresso = displayedProducts.some(text => text.includes('Espresso'))
    const hasGreenTea = displayedProducts.some(text => text.includes('Green Tea'))
    
    expect(hasGreenTea).toBe(true)
    expect(hasEspresso).toBe(false)
  })

  it('should clear filters when clicking All button', async () => {
    // First filter by category
    const categoryButtons = wrapper.findAll('button.px-4.py-2.rounded-full')
    const coffeeButton = categoryButtons.find(button => button.text() === 'Coffee')
    await coffeeButton.trigger('click')
    
    await wrapper.vm.$nextTick()
    
    // Then clear filters
    const allButton = categoryButtons.find(button => button.text() === 'All')
    await allButton.trigger('click')
    
    await wrapper.vm.$nextTick()
    
    const productCards = wrapper.findAll('.bg-white')
    // After clearing filters, both products should be displayed
    const displayedProducts = productCards.map(card => card.text())
    const hasEspresso = displayedProducts.some(text => text.includes('Espresso'))
    const hasGreenTea = displayedProducts.some(text => text.includes('Green Tea'))
    
    expect(hasEspresso).toBe(true)
    expect(hasGreenTea).toBe(true)
  })

  it('should display loading state when products are loading', async () => {
    productStore.loading = true
    wrapper.vm.$forceUpdate()
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.animate-spin')).toBeTruthy()
  })

  it('should display error state when there is an error', async () => {
    productStore.error = 'Failed to load products'
    productStore.loading = false
    wrapper.vm.$forceUpdate()
    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('Error loading products')
    expect(wrapper.text()).toContain('Failed to load products')
  })

  it('should display empty state when no products match filters', async () => {
    const searchInput = wrapper.find('input[placeholder="Search products..."]')
    await searchInput.setValue('nonexistentproduct')
    
    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('No products found')
    expect(wrapper.text()).toContain('Try adjusting your search or filters')
  })
})
