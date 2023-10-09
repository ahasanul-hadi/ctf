package com.cirt.ctf.user;

import com.cirt.ctf.enums.Role;
import com.cirt.ctf.team.TeamEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;

    @NotEmpty(message = "Please enter valid name.")
    private String name;

    @NotEmpty(message = "Please enter valid email.")
    @Email(message = "Email is not valid")
    private String email;

    @NotEmpty(message = "Please enter valid mobile.")
    private String mobile;

    @NotEmpty(message = "Please enter valid password.")
    private String password;
    private String rePassword;

    private Role role;

    private TeamEntity team;

    private String avatar;
    private String education;
    private String address;
    private String personalInfo;
    private String designation;

}
