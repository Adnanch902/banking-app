package com.example.bankingapp.controller;

import com.example.bankingapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authMgr;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        Authentication auth = authMgr.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        return ResponseEntity.ok(new LoginRes(jwtUtil.generateToken(auth)));
    }

    record LoginReq(String username, String password) {}
    record LoginRes(String token) {}
}
