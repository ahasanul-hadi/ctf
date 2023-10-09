package com.cirt.ctf.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Member1 {
    private String name1;
    private String email1;
    private String mobile1;
    private String designation1;
    @JsonIgnore
    private String password;
    private MultipartFile file1;
}
