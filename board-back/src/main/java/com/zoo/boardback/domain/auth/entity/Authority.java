package com.zoo.boardback.domain.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Authority {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  private String name;

  @JoinColumn(name = "roles")
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  private User users;

  public void setUser(User user) {
    this.users = user;
  }
}