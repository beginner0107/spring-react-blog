package com.zoo.boardback.domain.user.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
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

  @Builder
  public User(String email, String password, String nickname, String telNumber, String address,
      String addressDetail, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.telNumber = telNumber;
    this.address = address;
    this.addressDetail = addressDetail;
    this.profileImage = profileImage;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}