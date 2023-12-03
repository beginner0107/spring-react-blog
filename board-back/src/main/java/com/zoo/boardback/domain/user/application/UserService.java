package com.zoo.boardback.domain.user.application;

import static com.zoo.boardback.global.error.ErrorCode.USER_NOT_FOUND;

import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.dto.response.GetSignUserResponseDto;
import com.zoo.boardback.domain.user.entity.User;
import com.zoo.boardback.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public GetSignUserResponseDto getSignUser(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(email, "email", USER_NOT_FOUND));
    return GetSignUserResponseDto.from(user);
  }
}
