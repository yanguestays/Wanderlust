package com.wanderlust.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String role;
    private String avatar;
    // 这里不要放 password！也不要放 List<Order> 这种复杂对象！
}