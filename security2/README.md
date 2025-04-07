# 🔐 02. JWT 기반 Spring Security 인증 방식

JWT(Json Web Token)는 클라이언트에게 Access Token과 Refresh Token을 발급하여, 서버의 세션 저장 없이도 인증 상태를 유지할 수 있는 **Stateless 인증 방식**입니다.

- 모바일 앱, 프론트엔드(React 등)에서 자주 사용
- 스프링 MVC 기반 SSR 프로젝트에서는 세션 인증 방식이 여전히 보편적이다.

<br>

## 스프링 시큐리티 필터 동작 원리

1. 클라이언트 → 서블릿 필터 → DispatcherServlet(컨트롤러)
2. Spring Security의 `DelegatingFilterProxy`가 요청을 가로챔
3. `SecurityFilterChain`을 통해 인증/인가 수행
4. 인증 성공 시 요청을 컨트롤러에 전달

> Spring Security는 서블릿 필터 체인과는 **논리적으로 분리된 필터 체인**으로 동작함

<br>

## `UsernamePasswordAuthenticationFilter` 비활성화

JWT 인증은 세션을 사용하지 않으므로 다음 설정을 통해 전통적인 로그인 방식을 비활성화해야 합니다:

```java
http.formLogin().disable();
http.httpBasic().disable();
http.csrf().disable();
```

<br>

## 로그인 로직 흐름 (JWT 발급)

1. 클라이언트가 /login 으로 username/password 요청
2. LoginFilter에서 감지 → AuthenticationManager로 인증 위임
3. 인증 성공 시 JWTUtil 통해 토큰 생성
4. 응답 헤더로 JWT 반환

```http
Authorization: Bearer <JWT_TOKEN>
```

<br>

### JWT 구조

```java
Header.Payload.Signature
```

- Header: 토큰 타입, 암호화 알고리즘
- Payload: username, role 등의 사용자 정보
- Signature: 서버 비밀키로 암호화된 서명 (위조 방지용)
  > ⚠️ 비밀번호와 같은 민감한 정보는 포함하지 말 것

<br>

### JWT 암호화 방식

- HS256 (대칭키 방식)
- 키는 .properties 또는 .yml에 저장:

```java
spring.jwt.secret=임의의복잡한문자열
```

<br>

### JWTUtil 클래스

| 메서드          | 설명                   |
| --------------- | ---------------------- |
| `createJwt()`   | 토큰 생성              |
| `getUsername()` | 토큰에서 username 추출 |
| `getRole()`     | 토큰에서 role 추출     |
| `isExpired()`   | 토큰 만료 여부 확인    |

```java
String token = jwtUtil.createJwt(username, role, 60 * 60 * 1000L);
response.addHeader("Authorization", "Bearer " + token);
```

<br>

### JWT 검증 흐름

1. 클라이언트는 매 요청마다 JWT 포함 (Authorization 헤더)
2. 서버는 JwtFilter를 통해 토큰 추출 및 유효성 검증
3. 토큰이 유효하면 SecurityContextHolder에 사용자 정보 저장
4. 이후 요청은 인증된 사용자로 처리됨

<br>

### 전체 인증 흐름 요약

| 단계 | 설명                                   |
| ---- | -------------------------------------- |
| 1    | 클라이언트 로그인 요청                 |
| 2    | LoginFilter → 인증 성공 시 JWT 발급    |
| 3    | 이후 요청 시 JWT 포함                  |
| 4    | JwtFilter → 토큰 유효성 검사           |
| 5    | 인증 객체 등록 (SecurityContextHolder) |

<br>

### 보안 유의사항

- 반드시 HTTPS를 사용하여 토큰 탈취 방지
- Access Token은 짧게, Refresh Token은 길게 설정
- Refresh Token은 서버 DB에 저장 후 재발급 정책 구현 필요
