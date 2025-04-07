# 스프링 시큐리티

1. 클라이언트가 요청을 보내면, 요청은 서블릿 컨테이너로 전달됩니다.
2. 서블릿 컨테이너 내부의 **필터 체인(Filter Chain)** 을 통해 요청이 처리됩니다.
3. **Spring Security 설정 클래스(SecurityConfig)** 가 등록되어 있다면, 해당 필터 체인에서 먼저 요청을 가로챕니다.
4. Spring Security는 이 단계에서 인증(Authentication) 및 권한(Authorization) 검사를 수행합니다.
5. 인증된 사용자라면 요청은 컨트롤러까지 전달되며, 비인증 사용자는 로그인 페이지 등으로 리다이렉트됩니다.
6. 로그인에 성공하면 Spring Security는 **세션(Session)** 에 해당 사용자 정보를 등록합니다.
7. 이후 사용자는 세션을 통해 인증된 상태로 인식되어, 마이페이지 같은 권한이 필요한 페이지에 접근할 수 있습니다.

## 🧩 Step 1: 회원가입 시 비밀번호 암호화
### 🔐 스프링 시큐리티 암호화 개념
Spring Security는 로그인 시 사용자의 입력 비밀번호를 단방향 해시로 암호화한 후,
DB에 저장된 암호화된 비밀번호와 비교하여 인증합니다.

따라서 회원가입 시 비밀번호를 미리 단방향 암호화하여 저장해야 합니다.

🛠 사용 기술: BCryptPasswordEncoder
Spring Security는 기본적으로 BCryptPasswordEncoder를 제공하며, 가장 권장되는 암호화 방식입니다.
해당 클래스를 @Bean으로 등록하면 프로젝트 전역에서 사용할 수 있습니다.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
🔁 단방향 vs 양방향 암호화
구분	설명	예시
양방향 암호화	암호화한 값을 복호화(복원)할 수 있음	대칭키, 비대칭키
단방향 암호화	암호화한 값을 복호화할 수 없으며, 동일한 입력값은 항상 동일한 해시값 출력	해시(BCrypt 등)
🧪 해시 예시 (단방향)
plaintext
복사
편집
입력값: 1234

결과값: e39a71de-9a09-424c-907c-8f5233e27fda
같은 입력값이라도 BCrypt는 **Salt(난수)**를 포함하여 매번 다른 결과를 생성합니다.
