package com.cirt.ctf.team;

import com.cirt.ctf.payload.*;
import com.cirt.ctf.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final UserService userService;

    @PostMapping("/reg")
    public String addTeam(@Valid @ModelAttribute("team") TeamRegistration team, BindingResult result, Model model){

        model.addAttribute("team",team);

        if (result.hasErrors()) {
            return "team/registration";
        }

        String reason = teamService.addTeam(team);
        if(reason!=null){
            model.addAttribute("type", "error");
            model.addAttribute("message", reason);
            return "team/registration";
        }
        return "redirect:/teams";
    }


    @GetMapping()
    public String getTeams( ModelMap model, HttpServletRequest request){
        List<TeamDTO> teams= teamService.getTeams();
        model.addAttribute("teams", teams);
        return "team/teams";
    }

    @GetMapping("/registration")
    public String getRegistration( ModelMap model, HttpServletRequest request){
        model.addAttribute("team",new TeamRegistration());
        return "team/registration";
    }

    @GetMapping("/approve/{id}")
    public String approveTeam(@PathVariable("id") Long id, Model model, HttpServletRequest request, final RedirectAttributes redirectAttributes){
       teamService.approve(id);
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Team Has Been Approved!");
        return "redirect:/teams";
    }


}
