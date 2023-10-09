package com.cirt.ctf.user;

import com.cirt.ctf.enums.Role;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping
    public String getUsers(Model model, Principal principal){
        List<User> users= userService.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()){
            // is normal user
            users= users.stream().filter(user->user.getRole()!= Role.ADMIN).toList();
        }

        List<UserDTO> dtos= users.stream().map(user -> modelMapper.map(user,UserDTO.class)).toList();
        model.addAttribute("users",dtos);

        return "user/users";
    }

    @GetMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id, Model model, Principal principal, final RedirectAttributes redirectAttributes){
       //check necessary permission
        User user= userService.findById(id);
        model.addAttribute("user",user);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "User has been deleted!");

        return "redirect:/users/update/"+user.getId();

    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model, Principal principal, final RedirectAttributes redirectAttributes){
        User user= userService.findById(id);
        userService.deleteUser(user);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "User has been deleted!");

        return "redirect:/users";
    }

}
