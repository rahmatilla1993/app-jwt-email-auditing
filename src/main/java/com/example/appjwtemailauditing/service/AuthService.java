package com.example.appjwtemailauditing.service;

import com.example.appjwtemailauditing.entity.User;
import com.example.appjwtemailauditing.entity.enums.RoleName;
import com.example.appjwtemailauditing.payload.ApiResponse;
import com.example.appjwtemailauditing.payload.LoginDto;
import com.example.appjwtemailauditing.payload.UserDto;
import com.example.appjwtemailauditing.repository.RoleRepository;
import com.example.appjwtemailauditing.repository.UserRepository;
import com.example.appjwtemailauditing.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Lazy
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    @Lazy
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;

    public ApiResponse registerUser(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            return new ApiResponse("This email already exists", false);
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setRoles(Collections.singleton(roleRepository.findByName(RoleName.ROLE_USER)));
        user.setEmailCode(UUID.randomUUID().toString());
        userRepository.save(user);

        Boolean sendEmail = sendEmail(user.getEmail(), user.getEmailCode());
        return new ApiResponse("Confirm account to email", sendEmail);
    }

    public Boolean sendEmail(String toEmail, String emailCode) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("example@gmail.com");
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Confirm account");
        mailMessage.setText("http://localhost:8080/api/auth/verifyEmail?emailCode=" + emailCode + "&email=" + toEmail);
        javaMailSender.send(mailMessage);
        return true;
    }

    public ApiResponse verifyEmail(String emailCode, String email) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setEmailCode(null);
            userRepository.save(user);
            return new ApiResponse("Account confirmed successfully", true);
        }
        return new ApiResponse("Account already confirmed", false);
    }

    public ApiResponse loginToSystem(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()));

            User user = (User) authentication.getPrincipal();
            String token = jwtProvider.generateToken(loginDto.getUsername(), user.getRoles());
            return new ApiResponse("Token generated", true, token);
        } catch (BadCredentialsException e) {
            return new ApiResponse("Password or username incorrect",false);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new UsernameNotFoundException("User not found");
    }
}
