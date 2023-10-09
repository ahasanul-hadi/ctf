package com.cirt.ctf.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Member2 {
    private String name2;
    private String email2;
    private String mobile2;
    @JsonIgnore
    private String password;
    private String designation2;
    private MultipartFile file2;
}
