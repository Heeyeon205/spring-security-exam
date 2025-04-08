# 🔐 03 [Spring Security] OAuth2 로그인 구현

### 기능 요약
1. Google, Naver 소셜 로그인
2. 최초 로그인 시 사용자 DB에 등록
3. 커스텀 로그인 페이지 제공
4. 인증된 사용자만 접근이 가능한 페이지 보호 처리

<br>

### 디렉토리 구조
|Diractory| Class                            | 설명       |
|--|----------------------------------|----------|
|config| `SecurityConfig`                   | 보안 필터 체인 설정, 로그인 방식 및 권한 처리 |
|controller| `MainController`, `UserController` | 메인 페이지 처리|
|domain|`UserEntity`|DB 테이블 매핑, 사용자 정보 Entitiy|
|dto|`OAuth2Response`, `GoogleResponse`, `NaverResponse`, `CustomOAuth2User`|외부 응답 파싱 및 커스텀 사용자 정보 객체|
|oauth2|`SocialClientRegistration`, `CustomClientRegistrationRepo`|OAuth2 클라이언트 등록 정보 설정|
|repository|`UserRepository`|사용자 정보 DB 접근 레포지토리|
|service|`CustomOAuth2UserService`|사용자 정보 로드 및 가공 서비스|
|main|`Security3Application`|애플리케이션 실행 클래스|

<br>

### OAuth2 인증 데이터 흐름
1. 사용자가 `"/login"`페이지에서 Google 또는 Naver 로그인 클릭
2. Spring Security의 `oauth2Login()` 동작 -> provider 인증 페이지 이동
3. 인증 성공 시 `CustomOAuth2UserService` 동작
   - provider 별 응답을 `OAuth2Response` 구현체로 Google/Naver에 맞춰 파싱
   - `CustomOAth2User` 객체로 변환
   - DB에 사용자 정보를 `UserRepository`로 저장하거나 조회한다.
4. 인증 완료 시 SecurityContextHolder에 인증 객체를 저장한다.
