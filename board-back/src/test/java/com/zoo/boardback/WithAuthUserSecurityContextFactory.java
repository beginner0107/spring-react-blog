package com.zoo.boardback;

import static com.zoo.boardback.domain.auth.entity.role.UserRole.GENERAL_USER;

import com.zoo.boardback.domain.auth.details.CustomUserDetails;
import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.domain.auth.entity.role.UserRole;
import com.zoo.boardback.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {

  @Override
  public SecurityContext createSecurityContext(WithAuthUser annotation) {
    String userId = annotation.userId();
    UserRole role = UserRole.findRole(annotation.role());

    User user = User.builder()
        .id(Long.parseLong(userId))
        .roles(List.of(Authority.builder().role(role).build()))
        .build();
    List<Authority> authorities = user.getRoles();
    List<String> roles = authorities.stream()
        .map(Authority::getRoleName)
        .collect(Collectors.toList());
    CustomUserDetails userDetails = new CustomUserDetails(userId, roles);

    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(token);
    return context;
  }
}
