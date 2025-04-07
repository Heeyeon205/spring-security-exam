# 스프링 시큐리티 2

# JWT 인증 프로젝트

## 🔐 JWT란?

[🌐 JWT 토큰 인증이란? (쿠키 vs 세션 vs 토큰)](https://inpa.tistory.com/entry/WEB-%F0%9F%93%9A-JWTjson-web-token-%EB%9E%80-%F0%9F%92%AF-%EC%A0%95%EB%A6%AC)

JWT를 도입하여 액세스 토큰, 리프레시 토큰을 사용해 토큰 유효성을 검사하고 사용자 인증을 진행합니다.

![JWT 개념도](attachment:1fbe5032-6423-48ad-907e-043915cbad00:image.png)

> ✅ 보통 모바일 앱 + API 백엔드 서버, React 기반 프론트엔드 앱에서 많이 사용  
> ✅ 스프링 자체에서 모든 처리를 하는 SSR 방식은 **세션 인증**이 아직 보편적입니다.
