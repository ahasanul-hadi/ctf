package com.cirt.ctf.settings;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsEntity findById(Long id){
        return settingsRepository.findById(id).orElseThrow();
    }
}
