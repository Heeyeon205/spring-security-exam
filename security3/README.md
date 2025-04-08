# 🔐 03 [Spring Security] OAuth2 로그인 구현

### OAuth2
OAuth2는 외부 서비스(Google, Naver 등) 에 사용자 인증을 위임하여, 애플리케이션이 직접 사용자 정보를 다루지 않고도 인증할 수 있도록 해주는 인증 프로토콜이다.
> Authorization Code Grant Type 인증 방식 

<br>

### 인증 절차 
1. 사용자가 로그인 버튼 클릭 → 인증 서버(구글/네이버)로 이동
2. 로그인 성공 시, 인증 서버가 redirect URI로 authorization code 전달
3. 서버는 해당 code를 이용해 access token 을 요청
4. access token으로 사용자 정보 요청 후 로그인 처리

<br>

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
