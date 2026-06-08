package com.wanderlust.dto;

import lombok.Data;


@Data
public class RegisterRequest {
    // 这里明确定义前端必须传这两个字段
    private String username;
    private String password;

    // 如果以后要加“确认密码”或“邮箱”，直接在这里加字段即可，不用改 Controller 逻辑
}