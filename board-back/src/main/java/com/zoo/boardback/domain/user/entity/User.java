package com.zoo.boardback.domain.user.entity;

import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.auth.entity.Authority;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Users")
public class User {
  @Id
  @Column(name = "email", length = 50)
  private String email;

  @Column(name = "password", length = 100)
  private String password;

  @Column(name = "nickname", length = 20)
  private String nickname;

  @Column(name = "telNumber", length = 15)
  private String telNumber;

  @Column(name = "address")
  private String address;

  @Column(name = "addressDetail")
  private String addressDetail;

  @Column(name = "profileImage")
  private String profileImage;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "user_authorities",
      joinColumns = @JoinColumn(name = "email"),
      inverseJoinColumns = @JoinColumn(name = "authority_id"))
  private Set<Authority> authorities;

  @Builder
  public User(String email, String password, String nickname, String telNumber, String address,
      String addressDetail, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt,
      Set<Authority> authorities) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.telNumber = telNumber;
    this.address = address;
    this.addressDetail = addressDetail;
    this.profileImage = profileImage;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.authorities = authorities;
  }

  public void addAuthorities(Set<Authority> authorities) {
    if (this.authorities == null) {
      this.authorities = new HashSet<>();
    }
    this.authorities.addAll(authorities);
  }
}