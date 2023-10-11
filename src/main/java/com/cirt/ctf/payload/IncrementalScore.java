package com.cirt.ctf.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IncrementalScore {
    //@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String time;
    private int score;
}
