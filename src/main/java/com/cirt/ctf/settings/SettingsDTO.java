package com.cirt.ctf.settings;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SettingsDTO {
    private Long id;
    private String eventName;
    private Boolean isScoreboardFrozen;
    private String startTime;
    private String endTime;
}
