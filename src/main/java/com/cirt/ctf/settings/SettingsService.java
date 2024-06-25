package com.cirt.ctf.settings;

import com.cirt.ctf.util.Utils;
import lombok.Data;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@Data
@RequiredArgsConstructor
public class SettingsService { 
    private final SettingsRepository settingsRepository;

    public SettingsEntity findById(Long id){
        return settingsRepository.findById(id).orElseThrow();
    }

    public void update(SettingsDTO settingsDTO) {
        SettingsEntity entity= findById(1L);
        entity.setEventName(settingsDTO.getEventName());
        entity.setStartTime(LocalDateTime.parse(settingsDTO.getStartTime()));
        entity.setEndTime(LocalDateTime.parse(settingsDTO.getEndTime()));
        entity.setScoreboardVisibility(settingsDTO.getScoreboardVisibility());
        settingsRepository.save(entity);
    }
}
