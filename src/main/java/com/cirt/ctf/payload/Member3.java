package com.cirt.ctf.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Member3 {
    private String name3;
    private String email3;
    private String mobile3;
    @JsonIgnore
    private String password;
    private String designation3;
    private MultipartFile file3;
}
