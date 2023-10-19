package com.cirt.ctf.settings;

import lombok.Data;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Data
@RequiredArgsConstructor
public class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsEntity findById(Long id){
        return settingsRepository.findById(id).orElseThrow();
    }

    public void update(SettingsEntity settingsEntity) {
        SettingsEntity entity= findById(1L);
        entity.setEndTime(settingsEntity.getEndTime());
        entity.setStartTime(settingsEntity.getStartTime());
        settingsRepository.save(entity);
    }
}
