package com.zoo.boardback.global.config.security.filter.token_condition;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenConditionFactory {

    private final JwtTokenValidCondition jwtTokenValidCondition;
    private final AccessTokenReissueCondition accessTokenReissueCondition;

    public List<JwtTokenCondition> createJwtTokenConditions() {
        return List.of(jwtTokenValidCondition, accessTokenReissueCondition);
    }
}