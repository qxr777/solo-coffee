package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.service.VoiceRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/voice")
public class VoiceController {
    
    private static final Logger logger = LoggerFactory.getLogger(VoiceController.class);
    
    @Autowired
    private VoiceRecognitionService voiceRecognitionService;
    
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<?>> voiceToOrder(
            @RequestBody Map<String, Object> voiceOrderRequest) {
        try {
            String voiceInput = (String) voiceOrderRequest.get("voiceInput");
            Long customerId = voiceOrderRequest.containsKey("customerId") ? Long.valueOf(voiceOrderRequest.get("customerId").toString()) : null;
            
            Map<String, Object> result = voiceRecognitionService.voiceToOrder(voiceInput, customerId);
            
            if (result.containsKey("success") && (Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success("语音点单成功", result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.badRequest(result.get("message").toString()));
            }
        } catch (Exception e) {
            logger.error("语音点单失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("语音点单失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/commands")
    public ResponseEntity<ApiResponse<?>> getSupportedCommands() {
        try {
            List<Map<String, Object>> commands = voiceRecognitionService.getSupportedCommands();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("commands", commands);
            return ResponseEntity.ok(ApiResponse.success("支持的语音命令获取成功", responseData));
        } catch (Exception e) {
            logger.error("获取支持的语音命令失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
}
