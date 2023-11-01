package com.zoo.boardback.domain.auth.application;

import com.zoo.boardback.domain.auth.dto.request.SignRequestDto;
import com.zoo.boardback.domain.auth.dto.response.SignResponseDto;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  @Transactional
  public SignResponseDto login(SignRequestDto request) throws Exception {
    User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
        new BadCredentialsException("잘못된 계정정보입니다."));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("잘못된 계정정보입니다.");
    }

    return SignResponseDto.builder()
        .email(user.getEmail())
        .roles(user.getRoles())
        .token(jwtProvider.createToken(user.getEmail(), user.getRoles()))
        .build();
  }

  @Transactional
  public boolean register(SignRequestDto request) throws Exception {
    try {
      User user = User.builder()
          .email(request.getEmail())
          .password(passwordEncoder.encode(request.getPassword()))
          .nickname(request.getNickname())
          .telNumber(request.getTelNumber())
          .address(request.getAddress())
          .addressDetail(request.getAddressDetail())
          .build();

      user.addRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

      userRepository.save(user);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new Exception("잘못된 요청입니다.");
    }
    return true;
  }

  @Transactional
  public SignResponseDto getUser(String email) throws Exception {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new Exception("계정을 찾을 수 없습니다."));
    return new SignResponseDto(user);
  }
}
