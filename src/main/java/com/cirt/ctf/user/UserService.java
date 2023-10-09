package com.cirt.ctf.user;


import com.cirt.ctf.document.DocumentEntity;
import com.cirt.ctf.document.DocumentService;
import com.cirt.ctf.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Service
@Data
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DocumentService documentService;

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(usernameOrEmail);
        return userOptional.orElseThrow(()->new UsernameNotFoundException("Invalid email. User not Found!"));

    }

    public User saveUser(UserDTO userDTO, MultipartFile file){

        User user = new User(userDTO.getName(), userDTO.getEmail(), userDTO.getMobile(), passwordEncoder.encode(userDTO.getPassword()), userDTO.getRole());
        user.setDesignation(userDTO.getDesignation());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false);

        if(file!=null){
            try{
                DocumentEntity doc= documentService.saveDocument(file);
                user.setAvatarID(doc.getId());
            }catch (Exception ignored){}
        }

        user= userRepository.save(user);

        return user;
    }


}