package com.solocoffee.backend.init;

import com.solocoffee.backend.entity.*;
import com.solocoffee.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    public DataInitializer(StoreRepository storeRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            EmployeeRepository employeeRepository,
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            InventoryRepository inventoryRepository) {
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Store store;
        Product p1, p2, s1;

        if (storeRepository.count() > 0) {
            System.out.println(">>> Base data already exists, checking additional data...");
            store = storeRepository.findAll().get(0);
            // 假设产品按顺序添加，简单处理 (实际项目应该按 productNo 查询)
            // 为了稳健，我们这里尽量查一下
            p1 = productRepository.findByProductNo("PC001").orElse(null);
            p2 = productRepository.findByProductNo("PC002").orElse(null);
            s1 = productRepository.findByProductNo("PS001").orElse(null);
        } else {
            System.out.println(">>> Antigravity Data Initializer: Starting base data seeding...");

            // 1. Stores
            store = new Store();
            store.setName("Solo Coffee 旗舰店");
            store.setAddress("北京市朝阳区建国路 88 号");
            store.setPhone("010-12345678");
            store.setBusinessHours("08:00 - 22:00");
            store.setStatus(1);
            store = storeRepository.save(store);

            // 2. Categories
            Category coffee = new Category();
            coffee.setCategoryName("经典咖啡");
            coffee.setDescription("严选豆源，大师烘焙");
            coffee.setSortOrder(1);
            coffee = categoryRepository.save(coffee);

            Category snack = new Category();
            snack.setCategoryName("精选甜点");
            snack.setDescription("每日新鲜现制");
            snack.setSortOrder(2);
            snack = categoryRepository.save(snack);

            // 3. Products
            p1 = new Product();
            p1.setProductNo("PC001");
            p1.setName("美式咖啡");
            p1.setPrice(new BigDecimal("28.00"));
            p1.setCategoryId(coffee.getId());
            p1.setStatus(1);
            p1.setDescription("醇厚回甘，提神醒脑");
            p1 = productRepository.save(p1);

            p2 = new Product();
            p2.setProductNo("PC002");
            p2.setName("拿铁咖啡");
            p2.setPrice(new BigDecimal("32.00"));
            p2.setCategoryId(coffee.getId());
            p2.setStatus(1);
            p2.setDescription("细腻奶泡，丝滑口感");
            p2 = productRepository.save(p2);

            s1 = new Product();
            s1.setProductNo("PS001");
            s1.setName("法式牛角包");
            s1.setPrice(new BigDecimal("18.00"));
            s1.setCategoryId(snack.getId());
            s1.setStatus(1);
            s1.setDescription("层层酥脆，奶香浓郁");
            s1 = productRepository.save(s1);

            // 4. Employees
            Employee e1 = new Employee();
            e1.setName("陈晓文");
            e1.setEmployeeId("EMP001");
            e1.setPhone("13811112222");
            e1.setPosition("店经理");
            e1.setStoreId(store.getId());
            e1.setHireDate(LocalDateTime.now().minusYears(1));
            e1.setStatus(1);
            employeeRepository.save(e1);

            Employee e2 = new Employee();
            e2.setName("李明");
            e2.setEmployeeId("EMP002");
            e2.setPhone("13822223333");
            e2.setPosition("咖啡师长");
            e2.setStoreId(store.getId());
            e2.setHireDate(LocalDateTime.now().minusMonths(6));
            e2.setStatus(1);
            employeeRepository.save(e2);

            Employee e3 = new Employee();
            e3.setName("张琦");
            e3.setEmployeeId("EMP003");
            e3.setPhone("13833334444");
            e3.setPosition("咖啡师");
            e3.setStoreId(store.getId());
            e3.setHireDate(LocalDateTime.now().minusMonths(3));
            e3.setStatus(1);
            employeeRepository.save(e3);

            // 5. Customers
            Customer c1 = new Customer();
            c1.setName("张三");
            c1.setPhone("13900001111");
            c1.setPassword("password123");
            c1.setPoints(500);
            c1 = customerRepository.save(c1);

            Customer c2 = new Customer();
            c2.setName("李四");
            c2.setPhone("13922223333");
            c2.setPassword("password123");
            c2.setPoints(120);
            c2 = customerRepository.save(c2);

            Customer c3 = new Customer();
            c3.setName("测试用户");
            c3.setPhone("13800138000");
            c3.setPassword("password123");
            c3.setPoints(1500);
            c3.setMemberLevelId(2L);
            c3 = customerRepository.save(c3);

            // 6. Orders
            createOrder(store.getId(), c1.getId(), "SO001", p1, 2, 1); // 待支付
            createOrder(store.getId(), c2.getId(), "SO002", p2, 1, 2); // 制作中
            createOrder(store.getId(), null, "SO003", p1, 1, 3); // 已完成
        }

        // 7. Inventory (Check and seed)
        if (inventoryRepository.count() == 0 && store != null) {
            System.out.println(">>> Seeding Inventory data...");
            if (p1 != null)
                createInventory(store.getId(), p1.getId(), 100);
            if (p2 != null)
                createInventory(store.getId(), p2.getId(), 100);
            if (s1 != null)
                createInventory(store.getId(), s1.getId(), 50);
        }

        System.out.println(">>> Antigravity Data Initializer: Data seeding completed!");
    }

    private void createInventory(Long storeId, Long productId, int quantity) {
        Inventory inventory = new Inventory();
        inventory.setStoreId(storeId);
        inventory.setProductId(productId);
        inventory.setQuantity(new BigDecimal(quantity));
        inventory.setUnit("个");
        inventory.setWarningThreshold(new BigDecimal(10));
        inventoryRepository.save(inventory);
    }

    private void createOrder(Long storeId, Long customerId, String orderNo, Product product, int qty, int status) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setStoreId(storeId);
        order.setCustomerId(customerId);
        order.setOrderStatus(status);
        order.setPaymentMethod(1);

        BigDecimal total = product.getPrice().multiply(new BigDecimal(qty));
        order.setTotalAmount(total);
        order.setActualAmount(total);

        order.setOrderItems(new ArrayList<>());

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setQuantity(qty);
        item.setPrice(product.getPrice());
        item.setSubtotal(total);

        order.getOrderItems().add(item);
        orderRepository.save(order);
    }
}
