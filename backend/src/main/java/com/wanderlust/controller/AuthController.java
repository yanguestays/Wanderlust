package com.wanderlust.controller;

import com.wanderlust.dto.LoginResponse;
import com.wanderlust.dto.RegisterRequest;
import com.wanderlust.dto.UserDTO;
import com.wanderlust.entity.User;
import com.wanderlust.service.AuthService;
import com.wanderlust.utils.JwtUtil;
import com.wanderlust.utils.Result;
import com.wanderlust.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        try {
            User user = authService.login(username, password);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            LoginResponse response = new LoginResponse(
                    user.getId(), user.getUsername(), user.getRole(),
                    token, user.getAvatar()
            );
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        try {
            User user = authService.register(request.getUsername(), request.getPassword());
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            LoginResponse response = new LoginResponse(
                    user.getId(), user.getUsername(), user.getRole(),
                    token, user.getAvatar()
            );
            return Result.success("注册成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public Result<UserDTO> getUserInfo(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserDTO dto = new UserDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getRole(),
                            user.getAvatar()
                    );
                    return Result.success("获取成功", dto);
                })
                .orElse(Result.error("用户不存在"));
    }
}
