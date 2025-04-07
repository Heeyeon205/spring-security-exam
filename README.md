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

🔐 Step 2: 로그인과 인증 방식
로그인 동작 개요


인증 정보 저장 방식 비교
방식	저장 위치	특징
쿠키 (Cookie)	클라이언트 (브라우저)	사용자 조작 가능, 중요 정보 저장 ❌
세션 (Session)	서버	인증 정보 저장, 일반적인 로그인 방식
토큰 (Token)	클라이언트	JWT 등, 서버 상태 저장 없음 (Stateless)
캐시 (Cache)	클라이언트 or 서버	반복 요청 최적화용 데이터 저장
🛡️ Step 3: CSRF(Cross-Site Request Forgery)
CSRF는 사용자가 의도하지 않은 요청을 특정 사이트로 보내는 공격 방식입니다.
예: 사용자 모르게 회원 정보 변경, 게시글 조작 등

CSRF 공격 방지 개요


서버가 클라이언트에게 CSRF 토큰을 발급

클라이언트는 요청마다 해당 토큰을 포함

서버는 전달받은 토큰과 세션의 토큰을 비교하여 일치 여부 확인

📌 CSRF 적용 예시
✅ HTML form 방식 (JSP / Thymeleaf)
```html
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
```
✅ 파일 업로드 (GET 파라미터 방식)
```html
<form action="${cp}/member/memImageUpdate.do?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
```
✅ GET 요청은 기본적으로 CSRF 검증 생략됨
⚙️ CSRF 설정 방식
1. 개발 환경 (csrf 비활성화) 
```java
http.csrf(AbstractHttpConfigurer::disable);
```
로그인 등의 테스트를 빠르게 하기 위한 설정

실제 배포 환경에서는 반드시 활성화 필요

2. 배포 환경 (csrf enable 시 처리)
Spring Security는 CSRF가 활성화되면 POST, PUT, DELETE 요청 시 토큰을 요구합니다.

✅ HTML form 예시 (Thymeleaf)
```html
<form action="/loginProc" method="post">
  <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
  <input type="text" name="username" />
  <input type="password" name="password" />
  <input type="submit" value="로그인" />
</form>
```
✅ AJAX 요청 시
```html
<meta name="_csrf" content="{{_csrf.token}}"/>
<meta name="_csrf_header" content="{{_csrf.headerName}}"/>
```
```javascript
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

xhr.setRequestHeader(csrfHeader, csrfToken);
```

🚪 로그아웃 처리 (GET 방식 허용)
기본적으로 로그아웃은 POST 요청을 통해 처리해야 하나, 설정을 통해 GET 요청도 허용할 수 있습니다.

✅ Security Config 설정
```java
http.logout((auth) -> auth
    .logoutUrl("/logout")
    .logoutSuccessUrl("/")
);
```
✅ LogoutController
```java
@Controller
public class LogoutController {
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }
}
```
🛡️ 세션 고정 공격 방어 (Session Fixation)
로그인 이후에도 세션이 유지되면 공격자가 세션을 탈취할 수 있는 위험이 있습니다.
Spring Security는 아래 설정을 통해 이를 방지할 수 있습니다.



✅ 설정 방식
```java
http.sessionManagement(auth -> auth
    .sessionFixation().changeSessionId()
);
```
✅ 옵션 설명
옵션	설명
none()	기존 세션 유지
newSession()	새 세션 생성
changeSessionId()	기존 세션 그대로, ID만 새로 발급 → 일반적으로 이 옵션 사용
🧪 API 서버의 경우
API 서버는 주로 Stateless 환경(세션 없이)에서 동작하므로
csrf.disable() 설정을 유지해도 무방합니다.
