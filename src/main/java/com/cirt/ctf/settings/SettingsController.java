package com.cirt.ctf.settings;

import com.cirt.ctf.challenge.ChallengeDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor

public class SettingsController {
    private final SettingsService settingsService;
    private final ModelMapper modelMapper;
    
    @GetMapping
    public String getSettingsPage(Model model) {
        SettingsEntity settingsEntity= settingsService.findById(1L);
        SettingsDTO settingsDTO= modelMapper.map(settingsEntity, SettingsDTO.class);

        String[] startTokens = settingsEntity.getStartTime().toString().split(":");
        String startTime = String.join(":", startTokens[0], startTokens[1]);
        settingsDTO.setStartTime(startTime);

        String[] endTokens = settingsEntity.getEndTime().toString().split(":");
        String endTime = String.join(":", endTokens[0], endTokens[1]);
        settingsDTO.setEndTime(endTime);
        model.addAttribute("settings", settingsDTO);
        return "settings";
    }

    @PostMapping
    public String save(Model model, @ModelAttribute("settings")  SettingsDTO settingsDTO) {
        settingsService.update(settingsDTO);
        return "redirect:/settings";
    }
}
