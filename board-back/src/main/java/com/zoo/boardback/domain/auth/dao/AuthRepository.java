package com.zoo.boardback.domain.auth.dao;

import com.zoo.boardback.domain.auth.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Authority, String> {

}
