# 02 JWT Token

## 🔐 JWT란?

[🌐 JWT 토큰 인증이란? (쿠키 vs 세션 vs 토큰)](https://inpa.tistory.com/entry/WEB-%F0%9F%93%9A-JWTjson-web-token-%EB%9E%80-%F0%9F%92%AF-%EC%A0%95%EB%A6%AC)

JWT를 도입하여 액세스 토큰, 리프레시 토큰을 사용해 토큰 유효성을 검사하고 사용자 인증을 진행합니다.

![JWT 개념도](attachment:1fbe5032-6423-48ad-907e-043915cbad00:image.png)

> 보통 모바일 앱 + API 백엔드 서버, React 기반 프론트엔드 앱에서 많이 사용  
> 스프링 자체에서 모든 처리를 하는 SSR 방식은 **세션 인증**이 아직 보편적입니다.

🔐 스프링 시큐리티 필터 동작 원리
스프링 시큐리티는 클라이언트의 요청이 여러 개의 필터를 거쳐 DispatcherServlet(Controller) 으로 향하는 중간 과정에서, 필터에서 요청을 가로채 인증 및 인가를 수행한다.

```css
[Client] → [서블릿 필터] → [시큐리티 필터 체인] → [Controller]
```
✅ 필터 체인 흐름
클라이언트 요청 → 서블릿 필터 → 서블릿(Controller)
: 우리의 필터는 톰캣과 같은 서블릿 컨테이너 내부에서 실행된다.

✅ DelegatingFilterProxy
서블릿 필터 체인에 등록되는 필터로, 모든 요청을 Spring Security 필터 체인에 위임한다.

서블릿 컨텍스트와 시큐리티 컨텍스트는 논리적으로 분리되어 있으며,
Spring Security의 필터는 자체 컨텍스트에서 동작한다.

```plaintext
[Servlet Filter Chain]
     ↓ DelegatingFilterProxy
[Spring Security Filter Chain]
     ↓ Authentication, Authorization 등 처리
[DispatcherServlet → Controller]
```
💡 이 구조 안에서 DelegatingFilterProxy가 SecurityFilterChain으로 위임하여 시큐리티 처리를 담당한다.

🔄 SecurityFilterChain 필터 순서
SecurityFilterChain은 다양한 보안 필터들로 구성되어 있으며, 순서에 따라 요청을 처리하거나 거부한다.

필터명	설명
UsernamePasswordAuthenticationFilter	로그인 인증 필터 (form 로그인 방식에서 사용)
BasicAuthenticationFilter	HTTP Basic 인증 처리
CsrfFilter	CSRF 토큰 검증 필터
ExceptionTranslationFilter	인증 실패, 인가 실패 예외 처리
SecurityContextPersistenceFilter	인증 정보 저장 및 복원
⚠️ 모든 필터가 기본으로 활성화되는 것은 아니며, Security 설정에 따라 다르게 동작한다.

🚫 Form 로그인 비활성화와 필터 구현
UsernamePasswordAuthenticationFilter는 기본적으로 활성화되어 있으며
formLogin().disable()을 통해 비활성화할 수 있다.

```java
http.formLogin(AbstractHttpConfigurer::disable);
```
위 설정을 비활성화하면 Spring Security는 더 이상 form 로그인을 처리하지 않음.

이 경우, 커스텀 로그인 필터(LoginFilter) 를 작성해서 수동으로 로그인 처리를 구현해야 한다.

✅ Form 로그인 방식 동작 방식
클라이언트가 아이디/비밀번호를 전송

UsernamePasswordAuthenticationFilter가 요청을 가로채 인증 시도

인증 로직은 AuthenticationManager → UserDetailsService → DB 조회

인증 성공 시 SecurityContextHolder에 인증 정보 저장

🎯 JWT 로그인 로직 구현 목표
구현 항목	설명
LoginFilter	사용자 인증을 위한 커스텀 로그인 필터
UserDetailsService	DB의 회원 정보 조회 및 검증 로직 구현
SuccessHandler	로그인 성공 시 JWT 토큰 생성 및 반환
SecurityConfig	필터 등록 및 필터 순서 제어
```java
http
    .addFilterBefore(new LoginFilter(...), UsernamePasswordAuthenticationFilter.class);
```
🧩 참고 다이어그램
필터 체인 흐름
```css
[클라이언트 요청]
      ↓
[DelegatingFilterProxy]
      ↓
[SecurityFilterChain]
      ↓
[UsernamePasswordAuthenticationFilter]
      ↓
[인증 로직 → AuthenticationManager → UserDetailsService]
      ↓
[인증 완료 후 SecurityContextHolder에 저장]
```
