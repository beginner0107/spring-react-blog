# :pushpin: zoosBlog

> 나만의 블로그 만들기  
> https:// 배포예정

</br>

## 1. 제작 기간 & 참여 인원

- 2023년 10월 15일 ~ 월 일 (진행중)
- 개인 프로젝트

</br>

## 2. 사용 기술

#### `Back-end`

- Java 17
- Spring Boot 3.0.12
- Gradle
- Spring Data JPA
- QueryDSL
- H2
- MySQL
- Spring Security

#### `Front-end`

- TypeScript
- React

</br>

## 3. ERD 설계

![](./ZoosBlog.drawio.svg)

## 4. 핵심 기능

이 서비스의 핵심 기능은 주간 TOP 3 게시물을 보여주고, 게시물을 작성, 조회하는 기능입니다.

비회원인 사용자도 블로그의 글을 조회할 수 있으며, 회원인 사용자는 글을 작성, 수정, 삭제, 조회할 수 있습니다.

회원인 사용자는 다른 사람들의 프로필을 클릭 시 클릭한 사람의 글 목록을 볼 수 있습니다.

회원인 사용자는 마이페이지가 존재하여 자신의 글 목록을 볼 수 있습니다.

## 6. 그 외 트러블 슈팅
<details>
<summary>회원가입 테스트 코드 작성 시 발생한 문제점</summary>
<div markdown="1">

  - presentation Layer를 테스트할 때 [WebMvcTest] 로 필요한 빈들만 주입하여 테스트 하던 도중 403, 401 예외 발생
  - 403 인가 - csrf() 설정 추가로 해결
  - 401 인증 - Spring Security 설정은 WebMvcTest가 주입해주지 않음
  - 블로그 정리 : https://url.kr/gbw8vl
</div>
</details>
<details>
<summary>시큐리티 인증 객체 NullPointException</summary>
<div markdown="2">

  - 게시글 등록 기능을 테스트하려고 할 때 [WebMvcTest]로 필요한 빈들만 주입 받고 테스트 코드 작성 중
  ```java
  @PostMapping("")
  public ResponseEntity<Void> createBoard(
      @RequestBody @Valid PostCreateRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    boardService.create(requestDto, userDetails.getUsername());
    return ResponseEntity.ok().build();
  }
  ```
  - AuthenticationPrincipal CustomUserDetails userDetails이 NULL값 발생
  - 커스텀 어노테이션을 만들어 해결
  - 블로그 정리 : https://url.kr/gbw8vl
  - PR : https://github.com/beginner0107/spring-react-blog/pull/64

</div>
</details>
<details>
<summary>추천 기능이 반대로 작동</summary>
<div markdown="3">

  - 게시글 추천 기능에 대해 테스트 코드를 작성하는 도중에 추천기능이 제대로 작동하지 않는 것을 확인
  - 코드에 대한 실수를 발견해 수정
  - PR : https://github.com/beginner0107/spring-react-blog/pull/66

</div>
</details>
<details>
<summary>rest docs 작성 중 Path Variable에 대해 IllegalArgumentException</summary>
<div markdown="4">

  - MockMvcRequestBuilders -> RestDocumentationRequestBuilders 변경
  - 블로그 정리 : https://url.kr/gbw8vl
</div>
</details>
<details>
<summary>댓글을 삭제할 때 무결성 위배 JdbcSQLIntegrityConstraintViolationException</summary>
<div markdown="5">

  - Cascade 옵션 제거
  - 블로그 정리 : https://url.kr/gbw8vl
</div>
</details>
</br>

## 6. 회고 / 느낀점

> 프로젝트 개발 회고 글:
