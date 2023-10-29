package com.zoo.boardback.domain.user.application;

import com.zoo.boardback.domain.auth.dao.AuthRepository;
import com.zoo.boardback.domain.auth.dto.SignUpReqDto;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.auth.entity.enums.Role;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthRepository authRepository;

  @Transactional
  public void register(SignUpReqDto reqDto) {
    Optional<User> userOp = userRepository.findByEmail(reqDto.getEmail());
    User user;
    if (userOp.isPresent()) {
      throw new RuntimeException("이메일이 중복되었습니다.");
    }
    Set<Authority> authorities = new HashSet<>();
    authorities.add(Authority.builder().role(Role.ROLE_USER).build());
    authorities.add(Authority.builder().role(Role.ROLE_ADMIN).build());
    user = reqDto.toEntity(passwordEncoder);
    user.addAuthorities(authorities);
    userRepository.save(user);
  }
}
