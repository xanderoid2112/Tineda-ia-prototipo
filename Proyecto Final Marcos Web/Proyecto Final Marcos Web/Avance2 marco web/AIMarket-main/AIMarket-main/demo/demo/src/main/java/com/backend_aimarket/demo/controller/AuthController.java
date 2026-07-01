package com.backend_aimarket.demo.controller;

import com.backend_aimarket.demo.dto.AuthRequestDTO;
import com.backend_aimarket.demo.dto.AuthResponseDTO;
import com.backend_aimarket.demo.model.Usuario;
import com.backend_aimarket.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            AuthResponseDTO response = authService.login(authRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody Usuario usuario) {
        try {
            AuthResponseDTO response = authService.register(usuario);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Usuario> getUsuarioActual(@AuthenticationPrincipal Usuario usuario) {
        try {

            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}