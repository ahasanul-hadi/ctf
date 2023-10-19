package com.cirt.ctf.settings;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService settingsService;
    
    @GetMapping
    public String getSettingsPage(Model model) {
        SettingsEntity settingsEntity= settingsService.findById(1L);
        model.addAttribute("settingsEntity", settingsEntity);
        return "settings";
    }

    @PostMapping
    public String save(Model model, SettingsEntity settingsEntity) {
        settingsService.update(settingsEntity);
        return "redirect://settings";
    }
}
