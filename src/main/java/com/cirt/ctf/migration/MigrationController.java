package com.cirt.ctf.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@RestController
@RequestMapping("/from-excel")
@RequiredArgsConstructor
public class MigrationController {

    private final MigrationService migrationService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/register")
    public ResponseEntity<?> loadUser(Model model, Principal principal, final RedirectAttributes redirectAttributes){
        int count=migrationService.loadUser();
        return new ResponseEntity<>("inserted rows:"+count, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/auto-challenge-add")
    public ResponseEntity<?> loadAnswerTable(Model model, Principal principal, final RedirectAttributes redirectAttributes){
        int count=migrationService.loadChallenges();
        return new ResponseEntity<>("inserted rows:"+count, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/auto-answer-update")
    public ResponseEntity<?> updateAnswerTable(Model model, Principal principal, final RedirectAttributes redirectAttributes){
        int count=migrationService.loadChallenges();
        return new ResponseEntity<>("inserted rows:"+count, HttpStatus.OK);
    }

}
