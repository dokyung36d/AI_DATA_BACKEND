package com.example.AI_DATA.dto.user;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class PasswordChangeDTO {
    String oldPassword;
    String newPassword;


}
