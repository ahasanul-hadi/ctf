package com.cirt.ctf.settings;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {
    
    @GetMapping
    public String getSettingsPage(Model model) {
        return "settings";
    }
}
