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

import java.util.List;
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

    public User saveUser(UserDTO userDTO, MultipartFile file) throws RuntimeException{

        if(findUserByEmail(userDTO.getEmail()).isPresent())
            throw new RuntimeException("User Already present");

        User user = new User(userDTO.getName(), userDTO.getEmail(), userDTO.getMobile(), passwordEncoder.encode(userDTO.getPassword()), userDTO.getRole());
        user.setTeam(userDTO.getTeam());
        user.setDesignation(userDTO.getDesignation());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false);

        if(file!=null && file.getSize()>0){
            try{
                DocumentEntity doc= documentService.saveDocument(file);
                user.setAvatarID(doc.getId());
            }catch (Exception ignored){}
        }
        try {
            user = userRepository.save(user);
        }catch (Exception e) {
            throw e;
        }

        return user;
    }

    public void update(User user){
        userRepository.save(user);
    }

    public User updateUser(UserDTO userDTO){
        User user= findById(userDTO.getId());
        user.setName(userDTO.getName());
        user.setMobile(userDTO.getMobile());
        user.setDesignation(userDTO.getDesignation());
        if(userDTO.getPassword()!=null && !userDTO.getPassword().isEmpty())
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        if(userDTO.getFile()!=null && userDTO.getFile().getSize()>0){

            //String reson=Utils.checkFileValidity(userDTO.getFile());
            try{
                DocumentEntity doc= documentService.saveDocument(userDTO.getFile());
                user.setAvatarID(doc.getId());



            }catch (Exception ignored){}
        }
        return userRepository.save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public void deleteUser(User user) {
        userRepository.deleteById(user.getId());
    }
}