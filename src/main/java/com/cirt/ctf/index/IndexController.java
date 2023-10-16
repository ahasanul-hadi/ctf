package com.cirt.ctf.index;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cirt.ctf.settings.SettingsEntity;
import com.cirt.ctf.settings.SettingsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final SettingsService settingsService;

    @GetMapping(value = {"/","/index"})
    String getIndex(Model model){
        SettingsEntity settingsEntity = settingsService.findById(1L);
        model.addAttribute("settings", settingsEntity);
        return "index";
    }

    @GetMapping(value="/rules")
    String getRules(Model model) {
        return "home/rules";
    }
}
