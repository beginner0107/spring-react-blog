package com.zoo.boardback.domain.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zoo.boardback.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @JoinColumn(name = "users")
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  private User user;

  public void setUser(User user) {
    this.user = user;
  }
}