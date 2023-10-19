package com.zoo.boardback.domain.user.dao;

import com.zoo.boardback.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
