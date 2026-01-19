package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody Map<String, Object> request) {
        try {
            String phone = (String) request.get("phone");
            String password = (String) request.get("password");
            
            Map<String, Object> response = authService.login(phone, password, false);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (RuntimeException e) {
            logger.error("登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("登录失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("登录系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PostMapping("/sms-login")
    public ResponseEntity<ApiResponse<?>> smsLogin(@RequestBody Map<String, Object> request) {
        try {
            String phone = (String) request.get("phone");
            String code = (String) request.get("code");
            
            Map<String, Object> response = authService.smsLogin(phone, code);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (RuntimeException e) {
            logger.error("验证码登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("登录失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("验证码登录系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody Map<String, Object> request) {
        try {
            String refreshToken = (String) request.get("refreshToken");
            
            Map<String, Object> response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("令牌刷新成功", response));
        } catch (RuntimeException e) {
            logger.error("令牌刷新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("令牌刷新失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("令牌刷新系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout() {
        try {
            authService.logout();
            return ResponseEntity.ok(ApiResponse.success("登出成功", null));
        } catch (Exception e) {
            logger.error("登出系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PostMapping("/send-sms")
    public ResponseEntity<ApiResponse<?>> sendSms(@RequestBody Map<String, Object> request) {
        try {
            String phone = (String) request.get("phone");
            
            authService.sendSms(phone, 1);
            return ResponseEntity.ok(ApiResponse.success("验证码发送成功", null));
        } catch (RuntimeException e) {
            logger.error("验证码发送失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("验证码发送失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("验证码发送系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PostMapping("/oauth/{provider}")
    public ResponseEntity<ApiResponse<?>> oauthLogin(@PathVariable String provider, @RequestBody Map<String, Object> request) {
        try {
            String code = (String) request.get("code");
            
            Map<String, Object> response = authService.oauthLogin(provider, code);
            return ResponseEntity.ok(ApiResponse.success("第三方登录成功", response));
        } catch (RuntimeException e) {
            logger.error("第三方登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("第三方登录失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("第三方登录系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
}
