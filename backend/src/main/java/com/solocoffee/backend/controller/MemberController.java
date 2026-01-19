package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    
    @Autowired
    private MemberService memberService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> registerMember(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String phone = (String) request.get("phone");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            
            Map<String, Object> response = memberService.registerMember(name, phone, email, password);
            return ResponseEntity.ok(ApiResponse.success("会员注册成功", response));
        } catch (RuntimeException e) {
            logger.error("会员注册失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("注册失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("会员注册系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<?>> getMemberDetails(@PathVariable Long memberId) {
        try {
            Map<String, Object> memberDetails = memberService.getMemberDetails(memberId);
            if (memberDetails != null) {
                return ResponseEntity.ok(ApiResponse.success(memberDetails));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("会员不存在"));
            }
        } catch (Exception e) {
            logger.error("获取会员详情失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/{memberId}/points")
    public ResponseEntity<ApiResponse<?>> getMemberPoints(@PathVariable Long memberId,
                                                       @RequestParam(required = false, defaultValue = "1") int page,
                                                       @RequestParam(required = false, defaultValue = "10") int size) {
        try {
            Map<String, Object> pointsInfo = memberService.getMemberPoints(memberId, page, size);
            if (pointsInfo != null) {
                return ResponseEntity.ok(ApiResponse.success(pointsInfo));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("会员不存在"));
            }
        } catch (Exception e) {
            logger.error("获取会员积分失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PostMapping("/{memberId}/points")
    public ResponseEntity<ApiResponse<?>> addMemberPoints(@PathVariable Long memberId, @RequestBody Map<String, Object> request) {
        try {
            Integer points = ((Number) request.get("points")).intValue();
            String description = (String) request.get("description");
            
            Map<String, Object> response = memberService.addMemberPoints(memberId, points, description);
            return ResponseEntity.ok(ApiResponse.success("积分添加成功", response));
        } catch (RuntimeException e) {
            logger.error("添加会员积分失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("操作失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("添加会员积分系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<?>> updateMember(@PathVariable Long memberId, @RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> response = memberService.updateMember(memberId, request);
            if (response != null) {
                return ResponseEntity.ok(ApiResponse.success("会员信息更新成功", response));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("会员不存在"));
            }
        } catch (RuntimeException e) {
            logger.error("更新会员信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("更新失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("更新会员信息系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
}
