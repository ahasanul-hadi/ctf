package com.cirt.ctf.user;


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

    private static final String CONTEXT_PATH="/boithok";

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;




    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserByPersonalMeetingID(String meetingID) {
        return userRepository.findByPersonalMeetingID(meetingID);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(usernameOrEmail);
        return userOptional.orElseThrow(()->new UsernameNotFoundException("Invalid email. User not Found!"));

    }


}