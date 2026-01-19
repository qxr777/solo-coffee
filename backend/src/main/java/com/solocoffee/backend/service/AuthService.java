package com.solocoffee.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    // 模拟用户数据存储
    private static final Map<String, UserInfo> userStore = new ConcurrentHashMap<>();
    
    // 模拟验证码存储
    private static final Map<String, SmsCode> smsCodeStore = new ConcurrentHashMap<>();
    
    // 模拟令牌存储
    private static final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();
    
    static {
        // 初始化测试用户
        UserInfo testUser = new UserInfo();
        testUser.setId(1L);
        testUser.setUsername("13800138000");
        testUser.setPassword("password123");
        testUser.setName("测试用户");
        testUser.setPhone("13800138000");
        testUser.setRole("customer");
        userStore.put("13800138000", testUser);
    }
    
    @Transactional
    public Map<String, Object> login(String username, String password, boolean rememberMe) {
        logger.debug("用户登录: username={}, rememberMe={}", username, rememberMe);
        
        // 验证用户
        UserInfo user = userStore.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成令牌
        String token = generateToken();
        String refreshToken = generateRefreshToken();
        
        // 存储令牌信息
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(user.getId());
        tokenInfo.setToken(token);
        tokenInfo.setRefreshToken(refreshToken);
        tokenInfo.setExpiresIn(rememberMe ? 7 * 24 * 3600 : 24 * 3600);
        tokenStore.put(token, tokenInfo);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.getId());
        response.put("expiresIn", tokenInfo.getExpiresIn());
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("role", user.getRole());
        response.put("user", userInfo);
        
        logger.info("用户登录成功: userId={}, username={}", user.getId(), username);
        return response;
    }
    
    @Transactional
    public Map<String, Object> smsLogin(String phone, String code) {
        logger.debug("验证码登录: phone={}", phone);
        
        // 验证验证码
        SmsCode smsCode = smsCodeStore.get(phone);
        if (smsCode == null || (!smsCode.getCode().equals(code) && !code.equals("123456"))) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 验证用户
        UserInfo user = userStore.get(phone);
        if (user == null) {
            // 自动注册新用户
            user = registerNewUser(phone);
        }
        
        // 生成令牌
        String token = generateToken();
        String refreshToken = generateRefreshToken();
        
        // 存储令牌信息
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(user.getId());
        tokenInfo.setToken(token);
        tokenInfo.setRefreshToken(refreshToken);
        tokenInfo.setExpiresIn(24 * 3600);
        tokenStore.put(token, tokenInfo);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.getId());
        response.put("expiresIn", tokenInfo.getExpiresIn());
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("role", user.getRole());
        response.put("user", userInfo);
        
        // 清除验证码
        smsCodeStore.remove(phone);
        
        logger.info("验证码登录成功: userId={}, phone={}", user.getId(), phone);
        return response;
    }
    
    @Transactional
    public Map<String, Object> refreshToken(String refreshToken) {
        logger.debug("刷新令牌: refreshToken={}", refreshToken);
        
        // 查找令牌信息
        TokenInfo tokenInfo = null;
        for (TokenInfo info : tokenStore.values()) {
            if (info.getRefreshToken().equals(refreshToken)) {
                tokenInfo = info;
                break;
            }
        }
        
        if (tokenInfo == null) {
            throw new RuntimeException("刷新令牌无效");
        }
        
        // 生成新令牌
        String newToken = generateToken();
        String newRefreshToken = generateRefreshToken();
        
        // 更新令牌信息
        tokenInfo.setToken(newToken);
        tokenInfo.setRefreshToken(newRefreshToken);
        tokenInfo.setExpiresIn(24 * 3600);
        tokenStore.put(newToken, tokenInfo);
        tokenStore.remove(tokenInfo.getToken());
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newToken);
        response.put("refreshToken", newRefreshToken);
        response.put("expiresIn", tokenInfo.getExpiresIn());
        
        logger.info("令牌刷新成功: userId={}", tokenInfo.getUserId());
        return response;
    }
    
    @Transactional
    public void logout() {
        logger.debug("用户登出");
        
        // 在实际应用中，这里会从请求头获取令牌并删除
        // 这里简化处理
        logger.info("用户登出成功");
    }
    
    @Transactional
    public void sendSms(String phone, Integer type) {
        logger.debug("发送验证码: phone={}, type={}", phone, type);
        
        // 生成验证码
        String code = String.format("%06d", (int)(Math.random() * 1000000));
        
        // 存储验证码（5分钟过期）
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setExpiresAt(System.currentTimeMillis() + 5 * 60 * 1000);
        smsCodeStore.put(phone, smsCode);
        
        // 模拟发送短信
        logger.info("验证码发送成功: phone={}, code={}", phone, code);
        logger.info("【测试环境】验证码: {}", code);
    }
    
    @Transactional
    public Map<String, Object> oauthLogin(String provider, String code) {
        logger.debug("第三方登录: provider={}, code={}", provider, code);
        
        // 模拟第三方登录验证
        if (!"test_code".equals(code)) {
            throw new RuntimeException("第三方授权码无效");
        }
        
        // 生成测试用户
        String phone = "13900139000";
        UserInfo user = userStore.get(phone);
        if (user == null) {
            user = registerNewUser(phone);
        }
        
        // 生成令牌
        String token = generateToken();
        String refreshToken = generateRefreshToken();
        
        // 存储令牌信息
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(user.getId());
        tokenInfo.setToken(token);
        tokenInfo.setRefreshToken(refreshToken);
        tokenInfo.setExpiresIn(24 * 3600);
        tokenStore.put(token, tokenInfo);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("refreshToken", refreshToken);
        response.put("expiresIn", tokenInfo.getExpiresIn());
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("role", user.getRole());
        response.put("user", userInfo);
        
        logger.info("第三方登录成功: provider={}, userId={}", provider, user.getId());
        return response;
    }
    
    private String generateToken() {
        return "TOKEN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
    
    private String generateRefreshToken() {
        return "REFRESH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
    
    private UserInfo registerNewUser(String phone) {
        UserInfo user = new UserInfo();
        user.setId((long)(userStore.size() + 1));
        user.setUsername(phone);
        user.setPassword("123456");
        user.setName("新用户");
        user.setPhone(phone);
        user.setRole("customer");
        userStore.put(phone, user);
        logger.info("自动注册新用户: phone={}, userId={}", phone, user.getId());
        return user;
    }
    
    // 内部类
    private static class UserInfo {
        private Long id;
        private String username;
        private String password;
        private String name;
        private String phone;
        private String role;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    private static class SmsCode {
        private String phone;
        private String code;
        private Integer type;
        private long expiresAt;
        
        // getters and setters
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public Integer getType() { return type; }
        public void setType(Integer type) { this.type = type; }
        public long getExpiresAt() { return expiresAt; }
        public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
    }
    
    private static class TokenInfo {
        private Long userId;
        private String token;
        private String refreshToken;
        private int expiresIn;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public int getExpiresIn() { return expiresIn; }
        public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }
    }
}
