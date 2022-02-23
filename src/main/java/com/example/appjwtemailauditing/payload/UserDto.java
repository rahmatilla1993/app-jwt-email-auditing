package com.example.appjwtemailauditing.payload;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserDto {

    @NotNull
    @Size(min = 5, max = 50)
    private String firstName;

    @NotNull
    @Length(min = 5, max = 50)
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

}
