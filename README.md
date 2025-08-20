Switching between profile happens in the application.properties
If using Dev Mode then you can delete the whole ressources.db folder
https://central.sonatype.com/artifact/org.xerial/sqlite-jdbc
Jwt Secret is all configurated in the normal application.properties
- always set the jwt key before a project!!!

sequenceDiagram
participant C as Client
participant AC as AuthController
participant AS as AuthService
participant UR as UserRepository
participant JP as JwtTokenProvider
participant RT as RefreshTokenService
participant DB as Database

    C->>AC: POST /auth/signin (username, password)
    AC->>AS: authenticate user
    AS->>UR: findByUsername
    UR->>DB: query user
    DB-->>UR: user found
    UR-->>AS: return user
    AS->>JP: generate access + refresh tokens
    AS->>RT: save refresh token
    RT->>DB: insert refresh token
    AS-->>AC: tokens
    AC-->>C: { accessToken, refreshToken }

    Note over C,AC: Client stores tokens

    C->>AC: GET /users/me (Authorization: Bearer <accessToken>)
    AC->>JwtAuthenticationFilter: validate token
    JwtAuthenticationFilter->>JP: validate JWT
    JP-->>JwtAuthenticationFilter: valid
    JwtAuthenticationFilter->>SecurityContext: set authentication
    AC-->>C: user profile

    Note over C,AC: Access token expires

    C->>AC: POST /auth/refresh { refreshToken }
    AC->>RT: validate refresh token
    RT->>DB: check token + expiry
    DB-->>RT: valid
    RT->>JP: generate new access token
    AC-->>C: { newAccessToken, refreshToken }