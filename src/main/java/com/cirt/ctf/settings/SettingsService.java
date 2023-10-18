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
}
