package com.cirt.ctf.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Member5 {
    private String name5;
    private String email5;
    private String mobile5;
    @JsonIgnore
    private String password;
    private String designation5;
    private MultipartFile file5;
}
