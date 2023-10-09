package com.cirt.ctf.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class TeamRegistration {
    private Long id;

    @NotEmpty(message = "Please enter valid Team Name")
    private String teamName;
    @NotEmpty(message = "Please enter valid Organization Name")
    private String teamOrganization;
    @NotEmpty(message = "Please enter valid OrderID")
    private String orderID;
    @NotEmpty(message = "Please enter valid Payment Email")
    private String paymentEmail;

    //Team Leader
    @NotEmpty(message = "Please enter valid Team Leader Name")
    private String name;
    @NotEmpty(message = "Please enter valid Team Leader Email")
    private String email;
    @NotEmpty(message = "Please enter valid Team Leader Mobile")
    private String mobile;
    @JsonIgnore
    private String password;
    private String designation;
    private MultipartFile file;

    //member 1
    @NotEmpty(message = "Please enter valid Player1 Name")
    private String name1;
    @NotEmpty(message = "Please enter valid Player1 Email")
    private String email1;
    @NotEmpty(message = "Please enter valid Player1 Mobile")
    private String mobile1;
    private String designation1;
    @JsonIgnore
    private String password1;
    private MultipartFile file1;

    //member2
    @NotEmpty(message = "Please enter valid Player2 Name")
    private String name2;
    @NotEmpty(message = "Please enter valid Player2 Email")
    private String email2;
    @NotEmpty(message = "Please enter valid Player2 Mobile")
    private String mobile2;
    @JsonIgnore
    private String password2;
    private String designation2;
    private MultipartFile file2;

    //member3
    @NotEmpty(message = "Please enter valid Player3 Name")
    private String name3;
    @NotEmpty(message = "Please enter valid Player3 Email")
    private String email3;
    @NotEmpty(message = "Please enter valid Player3 Mobile")
    private String mobile3;
    @JsonIgnore
    private String password3;
    private String designation3;
    private MultipartFile file3;


    //member4
    private String name4;
    private String email4;
    private String mobile4;
    @JsonIgnore
    private String password4;
    private String designation4;
    private MultipartFile file4;

    //member5
    private String name5;
    private String email5;
    private String mobile5;
    @JsonIgnore
    private String password5;
    private String designation5;
    private MultipartFile file5;

}
