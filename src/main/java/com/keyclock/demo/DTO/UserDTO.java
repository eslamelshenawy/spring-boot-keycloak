package com.keyclock.demo.DTO;

import com.sun.istack.NotNull;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String email;
    private String username;
    private String lastName;
    private String firstName;
}
