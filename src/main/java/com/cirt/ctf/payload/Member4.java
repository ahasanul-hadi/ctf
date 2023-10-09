package com.cirt.ctf.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Member4 {
    private String name4;
    private String email4;
    private String mobile4;
    @JsonIgnore
    private String password;
    private String designation4;
    private MultipartFile file4;
}
