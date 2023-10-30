package com.zoo.boardback.domain.auth.application;

import com.zoo.boardback.domain.auth.userdetail.CustomUserDetails;
import com.zoo.boardback.domain.user.dao.UserRepository;
import com.zoo.boardback.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username).orElseThrow(
        () -> new UsernameNotFoundException("Invalid authentication")
    );
    return new CustomUserDetails(user);
  }
}
