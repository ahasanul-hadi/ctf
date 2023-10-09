package com.cirt.ctf.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TeamLeader {
    private String name;
    private String email;
    private String mobile;
    @JsonIgnore
    private String password;
    private String designation;
    private MultipartFile file;
}
