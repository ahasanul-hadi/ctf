package com.cirt.ctf.settings;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class SettingsDTO {
    private Long id;
    private String eventName;
    private Boolean isScoreboardFrozen;
    private String startTime;
    private String endTime;
    private String scoreboardVisibility;
}
