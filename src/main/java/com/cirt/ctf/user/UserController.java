package com.cirt.ctf.user;

import com.cirt.ctf.enums.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.ContentHandler;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String getUsers(Model model, Principal principal){
        List<User> users= userService.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()){
            // is normal user
            //users= users.stream().filter(user->user.getRole()!= Role.ADMIN).toList();
        }

        List<UserDTO> dtos= users.stream().map(user -> modelMapper.map(user,UserDTO.class)).toList();
        model.addAttribute("users",dtos);

        return "user/users";
    }

    @GetMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id, Model model, Principal principal){
       //check necessary permission
        User admin= userService.findUserByEmail(principal.getName()).orElseThrow();
        if(admin.getRole()!=Role.ADMIN && admin.getId()!=id){
            return "redirect:/login?logout";
        }

        User user= userService.findById(id);
        UserDTO dto= modelMapper.map(user, UserDTO.class);
        model.addAttribute("userDTO",dto);

        return "user/userUpdate";

    }

    @GetMapping("/new")
    public String createNewUser(Model model, Principal principal){
        model.addAttribute("userDTO",new UserDTO());
        return "user/userNew";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/new")
    public String createUser(@Valid UserDTO userDTO, BindingResult result, Model model, final RedirectAttributes redirectAttributes){

        if(userDTO.getPassword().isEmpty() || !userDTO.getPassword().equals(userDTO.getRePassword())){
            FieldError fe = new FieldError("userDTO", "rePassword", "password and rePassword does not Match!");
            result.addError(fe);
        }

        if(result.hasErrors()){
            model.addAttribute("userDTO", userDTO);
            return "user/userNew";
        }

        User user= userService.saveUser(userDTO, userDTO.getFile());
        user.setEnabled(true);
        userService.update(user);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "User has been added!");

        return "redirect:/users";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult result, Model model, final RedirectAttributes redirectAttributes){

        User user= userService.findById(userDTO.getId());

        if(userDTO.getPassword()!=null && !userDTO.getPassword().isEmpty()){
            if(!passwordEncoder.matches(userDTO.getCurrentPassword(),user.getPassword())){
                FieldError fe = new FieldError("userDTO", "currentPassword", "Current Password does not Match!");
                result.addError(fe);
            }
            if(userDTO.getPassword().isEmpty() || !userDTO.getPassword().equals(userDTO.getRePassword())){
                FieldError fe = new FieldError("userDTO", "rePassword", "password and rePassword does not Match!");
                result.addError(fe);
            }
        }

        if(result.hasErrors()){
            model.addAttribute("userDTO", userDTO);
            return "user/userUpdate";
        }

        System.out.println("going to update....");
       userService.updateUser(userDTO);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "User has been updated!");

        return "redirect:/users";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model, Principal principal, final RedirectAttributes redirectAttributes){
        User user= userService.findById(id);
        userService.deleteUser(user);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "User has been deleted!");

        return "redirect:/users";
    }

}
