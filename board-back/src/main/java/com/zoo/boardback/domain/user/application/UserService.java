package com.zoo.boardback.domain.user.application;

import com.zoo.boardback.domain.auth.dto.request.SignInReqDto;
import com.zoo.boardback.domain.auth.dto.response.SignInResDto;
import com.zoo.boardback.domain.auth.dto.request.SignUpReqDto;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  @Transactional
  public SignInResDto login(SignInReqDto reqDto) {
    User user = userRepository.findByEmail(reqDto.getEmail()).orElseThrow(() ->
        new BadCredentialsException("잘못된 계정정보입니다."));

    if (!passwordEncoder.matches(reqDto.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("잘못된 계정정보입니다.");
    }

    return SignInResDto.builder()
        .email(user.getEmail())
        .roles(user.getRoles())
        .token(jwtProvider.createToken(user.getEmail(), user.getRoles()))
        .build();
  }

  @Transactional
  public void register(SignUpReqDto reqDto) {
    Optional<User> userOp = userRepository.findByEmail(reqDto.getEmail());
    if (userOp.isPresent()) {
      throw new RuntimeException("이메일이 중복되었습니다.");
    }
    User user = reqDto.toEntity(passwordEncoder);
    user.addRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

    userRepository.save(user);
  }
}
