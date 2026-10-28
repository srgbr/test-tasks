package com.bank.service;

import com.bank.common.ApiException;
import com.bank.dao.EmailDataRepository;
import com.bank.dao.PhoneDataRepository;
import com.bank.dao.UserRepository;
import com.bank.dao.entity.UserEntity;
import com.bank.dto.LoginRequest;
import com.bank.dto.TokenResponse;
import com.bank.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TokenResponse login(LoginRequest request) {
        boolean hasEmail = StringUtils.hasText(request.getEmail());
        boolean hasPhone = StringUtils.hasText(request.getPhone());
        if (hasEmail == hasPhone) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Provide either email or phone");
        }

        Long userId = hasEmail
                ? emailDataRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"))
                .getUserId()
                : phoneDataRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"))
                .getUserId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        log.info("User {} logged in", userId);
        return new TokenResponse(jwtService.createToken(userId));
    }
}
