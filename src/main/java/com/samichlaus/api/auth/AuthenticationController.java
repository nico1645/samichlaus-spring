package com.samichlaus.api.auth;

import com.samichlaus.api.exception.InvalidCredentialsException;
import com.samichlaus.api.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request)
      throws UsernameAlreadyExistsException {
    return ResponseEntity.ok(service.register(request));
  }

  @PostMapping("authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) throws InvalidCredentialsException {
    return ResponseEntity.ok(service.authenticate(request));
  }
}
