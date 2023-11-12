package com.samuel.loja.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserInsertDTO extends UserDto {

    private String password;

    public UserInsertDTO(String password) {
        super();
        this.password = password;
    }
}
