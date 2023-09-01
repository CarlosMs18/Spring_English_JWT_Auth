package com.springboot.spring3.LoginSpringBoot3.Service;

import com.springboot.spring3.LoginSpringBoot3.Repository.UserRepository;
import com.springboot.spring3.LoginSpringBoot3.models.Role;
import com.springboot.spring3.LoginSpringBoot3.models.User;
import com.springboot.spring3.LoginSpringBoot3.request.AuthenticationRequest;
import com.springboot.spring3.LoginSpringBoot3.request.RegisterRequest;
import com.springboot.spring3.LoginSpringBoot3.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user  = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .pasword(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )

        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
