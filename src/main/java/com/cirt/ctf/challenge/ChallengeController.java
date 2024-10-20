package com.cirt.ctf.challenge;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.cirt.ctf.document.DocumentService;
import com.cirt.ctf.hints.HintsDTO;
import com.cirt.ctf.settings.SettingsEntity;
import com.cirt.ctf.settings.SettingsService;
import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cirt.ctf.enums.Category;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.submission.SubmissionDTO;
import com.cirt.ctf.submission.SubmissionService;
import com.cirt.ctf.team.TeamDTO;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.team.TeamService;
import com.cirt.ctf.user.UserDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController{
    private final ChallengeService challengeService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final SubmissionService submissionService;
    private final SettingsService settingsService;
    private final TeamService teamService;
    private final AutoAnswerControllerService autoAnswerService;

    @GetMapping
    public String getChallengesPage(Model model, Principal principal) {
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();

        String role = user.getRole().toString();
        SettingsEntity settingsEntity = settingsService.findById(1L);

        if(role.equals(Role.ADMIN.toString()))
            model.addAttribute("challenges", challengeService.getChallengesForAdmin());
        else {
            String pageVisibility = settingsEntity.getStartTime().isBefore(LocalDateTime.now()) ? "public": "hidden";
            long tId = user.getTeam().getId();
            List<ChallengeDTO> challengeDTOs = challengeService.getChallengesForUser();
            for(ChallengeDTO challengeDTO: challengeDTOs) {
                int attemptsDone = submissionService.getSubmissionCount(tId, challengeDTO.getId());
                challengeDTO.setAttemptsDone(attemptsDone);
                String attemptStatus = challengeDTO.getAttemptsDone() >= challengeDTO.getAttempts() ? "over" : "remains";

                Date date = null;
                try {
                    date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")).parse(challengeDTO.getDeadline());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String deadlineStatus = date.before(new Date())? "over" : "remains";
                challengeDTO.setAttemptStatus(attemptStatus);
                challengeDTO.setDeadlineStatus(deadlineStatus);
            }
            model.addAttribute("settings", settingsEntity);
            model.addAttribute("pageVisibility", pageVisibility);
            model.addAttribute("challenges", challengeDTOs);
            model.addAttribute("submission", new SubmissionDTO());
        }
            
        String htmlPage = role.equals(Role.ADMIN.toString()) ? "challenge/admin/home" : "challenge/user/home";
        return htmlPage;
    }

    @PostMapping
    public String submitForm(Model model, @ModelAttribute("challenge") ChallengeDTO challengeDTO, BindingResult result, final RedirectAttributes redirectAttributes) {
        this.challengeService.saveChallenge(challengeDTO);
        redirectAttributes.addFlashAttribute("message", "Challenge is successfully saved");
        redirectAttributes.addFlashAttribute("type", "success");
        return "redirect:/challenges";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER')")
    @GetMapping("/add" )
    public String getAddChallengePage(Model model, Principal principal) {
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();

        List<TeamDTO> teamList = teamService.getTeams();
        String role = user.getRole().toString();
        model.addAttribute("categories", Category.values());
        ChallengeDTO challengeDTO = new ChallengeDTO();
        challengeDTO.setTitle("Challenge ");
        challengeDTO.setTotalMark(150);
        challengeDTO.setMarkingType("auto");
        challengeDTO.setVisibility("hidden");
        challengeDTO.setAttempts(1);
        challengeDTO.setAnswer("");
        model.addAttribute("challenge", challengeDTO);
        model.addAttribute("teams", teamList);
        if(!role.equals(Role.ADMIN.toString())) {
            return "redirect:/challenges";
        }
        return "challenge/admin/add";
    }

    @GetMapping("/{id}")
    public String getSingleChallengePage(@PathVariable("id") Long id, Model model, Principal principal) {
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();
        List<TeamDTO> teamList;

        String role = user.getRole().toString();
        ChallengeEntity challengeEntity = challengeService.getChallengeById(id);
        if(challengeEntity.getMarkingType().equals("auto")) {
            teamList = teamService.getTeams();
            model.addAttribute("teams", teamList);
        }
        ChallengeDTO challengeDTO = new ChallengeDTO();
        challengeDTO.setId(challengeEntity.getId());
        challengeDTO.setTitle(challengeEntity.getTitle());
        challengeDTO.setTotalMark(challengeEntity.getTotalMark());
        challengeDTO.setMarkingType(challengeEntity.getMarkingType());
        challengeDTO.setVisibility(challengeEntity.getVisibility());
        challengeDTO.setAttempts(challengeEntity.getAttempts());
        challengeDTO.setCategory(challengeEntity.getCategory());
        challengeDTO.setDescription(challengeEntity.getDescription());
        //challengeDTO.setAnswer(challengeEntity.getAnswer());
        challengeDTO.setHint(modelMapper.map(challengeEntity.getHint(), HintsDTO.class));
        String[] deadlineTokens = challengeEntity.getDeadline().toString().split(":");
        String deadline = String.join(":", deadlineTokens[0], deadlineTokens[1]);
        challengeDTO.setDeadline(deadline);
        model.addAttribute("challenge", challengeDTO);
        model.addAttribute("categories", Category.values());
        if(!role.equals(Role.ADMIN.toString()) ) {
            return "redirect:/challenges";
        }
        return "challenge/admin/update";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}")
    public String updateChallenge(@PathVariable("id") Long id, Model model, @ModelAttribute("challenge") ChallengeDTO challengeDTO, Principal principal, RedirectAttributes redirectAttributes) {
        challengeService.updateChallenge(id, challengeDTO);
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Challenge successfully updated");
        return "redirect:/challenges";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/delete/{id}" )
    public String deleteChallenge(@PathVariable("id") Long id,  Model model, Principal principal) {
        challengeService.delete(id);
        return "redirect:/challenges";
    }
    
}
