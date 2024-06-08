package com.samichlaus.api.auth;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.samichlaus.api.exception.InvalidCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.samichlaus.api.config.JwtService;
import com.samichlaus.api.domain.user.Authoritie;
import com.samichlaus.api.domain.user.Role;
import com.samichlaus.api.domain.user.User;
import com.samichlaus.api.domain.user.UserRepository;
import com.samichlaus.api.domain.token.TokenRepository;
import com.samichlaus.api.domain.token.Token;
import com.samichlaus.api.domain.token.TokenType;
import com.samichlaus.api.exception.UsernameAlreadyExistsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) throws UsernameAlreadyExistsException {
    if (repository.existsByEmail(request.getEmail())){
      throw new UsernameAlreadyExistsException("Username already in use");
    }  
    var user = User.builder()
        .first_name(request.getFirstname())
        .last_name(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER_ROLE)
        .authorities(List.of(Authoritie.read))
        .last_login_date(new Date())
        .created_at(new Date())
        .isActive(false)
        .isNotLocked(true)
        .failed_login_attempts(0)
        .build();
    repository.save(user);
    return AuthenticationResponse.builder()
        .token("")
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) throws InvalidCredentialsException {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );
  } catch (BadCredentialsException ex) {
      final Integer MAX_LOGIN_ATTEMPTS = 3;
      // Password authentication failed, increment failed_login_attempts
        try {
            var user = repository.findByEmail(request.getEmail())
                    .orElseThrow();
            user.setFailed_login_attempts(user.getFailed_login_attempts() + 1);
            if (user.getFailed_login_attempts() >= MAX_LOGIN_ATTEMPTS) {
                user.setNotLocked(false);
            }
            repository.save(user);
            revokeAllUserTokens(user);
        } catch (Exception exp) {
            throw new InvalidCredentialsException("Bad credentials");
        }
      throw new InvalidCredentialsException("Bad credentials");
  }

    try {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        user.setFailed_login_attempts(0);
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    } catch (NoSuchElementException ex) {
       throw new InvalidCredentialsException("Bad credentials");
    }
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }
}
