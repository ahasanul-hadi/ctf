package com.cirt.ctf.team;

import com.cirt.ctf.email.MailService;
import com.cirt.ctf.payload.*;
import com.cirt.ctf.scoreboard.ScoreBoardService;
import com.cirt.ctf.submission.SubmissionService;
import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final TeamService teamService;
    private final UserService userService;
    private final MailService mailService;
    private final ScoreBoardService scoreBoardService;
    private final SubmissionService submissionService;

    @PostMapping("/registration")
    public String addTeam(@Valid @ModelAttribute("team") TeamRegistration team, BindingResult result, Model model, final RedirectAttributes redirectAttributes){

        model.addAttribute("team",team);

        if (result.hasErrors()) {
            return "team/registration";
        }

        String reason=null;
        try{
           reason= teamService.addTeam(team);
        }
        catch (Exception ee){
            System.out.println("exp:"+ee.getLocalizedMessage()+" \n"+ee.getMessage());
           reason="Use unique email and order id.";
        }
        if(reason!=null){
            model.addAttribute("type", "error");
            model.addAttribute("message", reason);
            return "team/registration";
        }
        mailService.sendRegistrationMail(team);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Your Team has been created successfully. Check your Email");
        return "redirect:/";

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public String getTeams( ModelMap model, HttpServletRequest request){
        List<TeamDTO> teams= teamService.getTeams();
        model.addAttribute("teams", teams);
        return "team/teams";
    }

    @GetMapping("/{id}")
    public String getTeamMembers( @PathVariable("id") Long id,  ModelMap model){
        TeamDTO teamDTO = teamService.findById(id);
        model.addAttribute("team", teamDTO);
        //model.addAttribute("solved",teamService.getSolvedCount(teamDTO));
        //model.addAttribute("failed",teamService.getFailedCount(teamDTO));
        //model.addAttribute("categoryList", scoreBoardService.getCategoryWiseSolved(teamDTO));
        model.addAttribute("place",scoreBoardService.getPlace(teamDTO.getId()));
        return "team/teamMembers";
    }

    @PreAuthorize("hasAnyAuthority('TEAM_LEAD', 'MEMBER')")
    @GetMapping("/my-team")
    public String getMyTeam( ModelMap model, Principal principal){
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();

        TeamDTO teamDTO = teamService.findById(user.getTeam().getId());
        model.addAttribute("team", teamDTO);
        model.addAttribute("place",scoreBoardService.getPlace(teamDTO.getId()));
        //model.addAttribute("submissions", submissionService.getSubmissionSummary(teamDTO.getId()));
        return "team/myTeam";
    }

    @GetMapping("/registration")
    public String getRegistration( ModelMap model, HttpServletRequest request){
        model.addAttribute("team",new TeamRegistration());
        return "team/registration";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/approve/{id}")
    public String approveTeam(@PathVariable("id") Long id, Model model, Principal principal, final RedirectAttributes redirectAttributes){
       User admin= userService.findUserByEmail(principal.getName()).orElseThrow();
        teamService.approve(id, admin);
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Team Has Been Approved!");
        return "redirect:/teams";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable("id") Long id, Model model, Principal principal, final RedirectAttributes redirectAttributes){

        TeamDTO dto = teamService.findById(id);
        teamService.deleteById(id);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Team has been deleted!");

        return "redirect:/teams";
    }


}
