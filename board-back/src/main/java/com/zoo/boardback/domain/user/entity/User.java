package com.zoo.boardback.domain.user.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.zoo.boardback.domain.auth.entity.Authority;
import com.zoo.boardback.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "Users")
public class User extends BaseEntity {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

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

  @OneToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Authority> roles = new ArrayList<>();

  @Builder
  public User(String email, String password, String nickname, String telNumber, String address,
      String addressDetail, String profileImage, List<Authority> roles)
  {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.telNumber = telNumber;
    this.address = address;
    this.addressDetail = addressDetail;
    this.profileImage = profileImage;
    this.roles = roles;
  }

  public void addRoles(List<Authority> role) {
    this.roles = role;
    role.forEach(o -> o.setUser(this));
  }
}