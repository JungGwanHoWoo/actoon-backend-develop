


## 서버 실행 방법

### 서버 코드 빌드
- 해당 작업 후 lib 폴더 내부에 jar 파일 생성
```
./gradlew build -x test
```

### 서버 구동 방법
```
./gradlew bootrun
```

## 도커 실행 방법
- -d는 백그라운드 실행
- mysql, redis, spring boot 컨테이너 생성

```
cd 'compose.yml 경로'
docker-compose up [-d]
```



## 프로젝트 구조
- src 기준(root)
```
.
├── main
│   ├── java
│   │   └── com
│   │       └── actoon
│   │           └── actoon
│   │               ├── ActoonApplication.java
│   │               ├── configuration
│   │               │   ├── CorsConfig.java
│   │               │   ├── RedisConfig.java
│   │               │   └── SecurityConfiguration.java
│   │               ├── controller
│   │               │   ├── AdminController.java
│   │               │   ├── FileUploadController.java
│   │               │   ├── MailController.java
│   │               │   ├── NonUserController.java
│   │               │   ├── UserController.java
│   │               │   └── WebtoonController.java
│   │               ├── domain
│   │               │   ├── EmailAuth.java
│   │               │   ├── FileInfoRegister.java
│   │               │   ├── NonUser.java
│   │               │   ├── RefreshToken.java
│   │               │   ├── User.java
│   │               │   ├── Webtoon.java
│   │               │   └── WebtoonFileInfo.java
│   │               ├── dto
│   │               │   ├── BasicResponse.java
│   │               │   ├── ExceptionResponse.java
│   │               │   ├── FileDto.java
│   │               │   ├── FileInfoDto.java
│   │               │   ├── JwtAuthenticationRequest.java
│   │               │   ├── JwtAuthenticationResponse.java
│   │               │   ├── MailDto.java
│   │               │   ├── NonMemberDto.java
│   │               │   ├── NonUserDto.java
│   │               │   ├── PatchAdminWebToonDto.java
│   │               │   ├── PatchWebToonDto.java
│   │               │   ├── TokenDto.java
│   │               │   ├── UploadFileDto.java
│   │               │   ├── UserDto.java
│   │               │   ├── WebtoonInfoDto.java
│   │               │   ├── WebtoonRequestDto.java
│   │               │   └── WebtoonResponseDto.java
│   │               ├── exception
│   │               │   ├── ErrorCode.java
│   │               │   └── ExceptionResponseHandler.java
│   │               ├── repository
│   │               │   ├── EmailRedisRepository.java
│   │               │   ├── FileUploadRepository.java
│   │               │   ├── NonUserRepository.java
│   │               │   ├── TokenRepository.java
│   │               │   ├── UserRepository.java
│   │               │   ├── WebtoonFileRepository.java
│   │               │   └── WebtoonRepository.java
│   │               ├── service
│   │               │   ├── AuthenticationServiceImpl.java
│   │               │   ├── FileUploadService.java
│   │               │   ├── JwtServiceImpl.java
│   │               │   ├── MailService.java
│   │               │   ├── NonUserService.java
│   │               │   ├── UserAuthService.java
│   │               │   ├── WebtoonService.java
│   │               │   └── interfaces
│   │               │       ├── AuthenticationService.java
│   │               │       ├── JwtService.java
│   │               │       └── UserService.java
│   │               └── util
│   │                   ├── JwtAuthenticationFilter.java
│   │                   ├── PasswordEncryptFactory.java
│   │                   ├── ProductInitializer.java
│   │                   ├── Role.java
│   │                   ├── ScheduledTasks.java
│   │                   └── WebtoonProgressState.java
│   ├── main.iml
│   └── resources
│       ├── application.yml
│       ├── db
│       │   └── changelog
│       │       └── changelog-master.xml
│       ├── static
│       │   └── index.html
│       └── templates
├── test
│   ├── application.yml
│   ├── java
│   │   └── com
│   │       └── actoon
│   │           └── actoon
│   │               └── ActoonApplicationTests.java
├── docker
│   ├── conf
│   │   └── my.cnf
│   ├── init.sql
│   ├── redis
│   ├── static
│   ├── spring.dockerfile
│   └── wait-for-it.sh
│ 
└── static

```

## 소스 코드 설명

### Service

#### AuthenticationServiceImpl
- 유저 및 인증에 관련한 로직이 담겨 있는 클래스입니다.
- AuthenticationService를 implements하고 있습니다. 
- 사용자 로그인, 회원가입, 토큰 저장, 리프레시 토큰 재발행, 닉네임 변경, 사용자 정보를 불러오는 코드가 작성되어 있습니다.

#### FileUploadService
- 파일에 관련한 기능이 담겨 있는 클래스입니다.
- 파일 저장, 파일 다운로드, 프로필 파일 저장, 웹툰 완료 파일 저장의 기능을 하는 코드가 작성되어 있습니다.

#### JwtServiceImpl
- 토큰에 관련한 모든 작업이 들어있는 서비스입니다
- 토큰 생성, 토큰 유효성 검사, 토큰 정보 추출 등의 기능을 하는 코드가 작성되어 있습니다.

#### MailService
- 메일에 관련한 모든 작업이 들어있는 서비스입니다.
- 이메일 내용 생성, 이메일 전송, 이메일 인증을 위한 랜덤 번호를 생성 및 레디스에 저장하는 코드가 작성되어 있습니다.
- MailService의 senderEmail의 경우 사용자 이메일에 전송하는 송신용 이메일입니다. 그러므로 해당 변수에 관리자 메일을 할당해주시면 됩니다.

#### NonUserService
- 비회원 유저에 관련한 모든 작업이 들어있는 서비스입니다.
- 비회원 유저 정보 저장, 비회원 리스트를 불러오는 코드가 작성되어 있습니다.

#### UserAuthService
- 해당 로직은 spring security에서 토큰으로 사용자를 검증할 때 사용하는 서비스입니다.
- db에서 사용자 역할을 불러와서 해당 토큰을 사용하는 유저의 역할을 판별합니다.

#### webtoonService
- 웹툰에 관련한 모든 작업이 들어있는 서비스입니다.
- [사용자] - 웹툰 리스트 불러오는 로직, [어드민] - 모든 웹툰 리스트를 불러오는 로직, 웹툰 생성, 웹툰별 상태를 불러오기, 웹툰 진행 상태 변경 로직이 담겨있습니다.


### Repository

#### EmailRedisRepository
- 이메일 인증에 사용되는 레포지토리로, 레디스를 사용했습니다.
- key를 생성한 랜덤 넘버로, value를 email로 저장하여, 추후 사용자 인증에 활용했습니다.
 
#### TokenRepository
- 토큰 관리를 위한 레포지토리로, 레디스를 사용했습니다.
- key를 refresh 토큰을, value로 access token로 저장하여 추후 access Token을 재발행을 할 때 활용하였습니다.

#### FileUploadRepository
- 파일 업로드를 위한 레포지토리입니다.
- 저장의 경우, save 메서드를 서비스에서 사용하여 저장하였기 때문에, 따로 구현하지 않았습니다.


#### NonUserRepository
- 비회원 정보를 관리하는 레포지토리 입니다.  
- 저장의 경우, save 메서드를 서비스에서 사용하여 저장하였기 때문에, 따로 구현하지 않았습니다.


#### UserRepository
- 유저 정보를 관리하는 레포지토리입니다.
- 저장의 경우, save 메서드를 서비스에서 사용하여 저장하였기 때문에, 따로 구현하지 않았습니다.


#### WebtoonFileRepository
- 완료된 웹툰 파일을 관리하는 레포지토리입니다.
- 저장의 경우, save 메서드를 서비스에서 사용하여 저장하였기 때문에, 따로 구현하지 않았습니다.


#### WebtoonRepository
- 웹툰 관련 기능을 수행하는 레포지토리입니다.
- 해당 레포지토리에서는, 커스텀 정렬 및 필터링을 수행하기 때문에 query를 직접 적어주었습니다.
- 저장의 경우, save 메서드를 서비스에서 사용하여 저장하였기 때문에, 따로 구현하지 않았습니다.


### Util

#### JwtAuthenticationFilter
- 필터링 로직을 수행하는 클래스입니다.
- 해당 클래스에서 token에 대한 유효성을 검사합니다.

#### PasswordEncryptFactory
- 비밀번호 암호화 및 복호화 로직을 수행합니다.

#### ProductInitializer
- 서버 수행 시 가장 처음 수행되는 로직을 담는 로직입니다.
- admin 계정의 경우 임의로 추가할 수 없기 때문에, 해당 클래스에서 미리 db에 저장합니다.

#### Role
- 유저의 권한을 지정해둔 enum 클래스입니다.

#### ScheduledTasks
- 지정된 시간마다 수행되는 task를 모아놓은 클래스입니다.
- 현재, 오전 0시마다 만들어진지 14일 이상이 된 완료된 웹툰 파일이 있을 시, zip 및 pdf 파일을 삭제하는 로직을 담고 있습니다.

#### WebtoonProgressState
- 웹툰 진행 상황을 정의해둔 enum 클래스입니다.


### docker

#### conf/my.conf
- DB에 대한 환경 설정이 담긴 파일입니다.

#### init.sql
- DB 생성을 담은 sql 파일입니다.

#### wait-for-it.sh
- 이미지가 올라가는 속도가 랜덤이므로 특정 이미지가 올라갈 때까지 기다리는 스크립트입니다.
- 현재는 mysql이 올라간 후 spring이 올라가도록 설정되어 있습니다. 해당 내용은 spring.dockerfile에서 지정되어 있습니다. 

#### spring.dockerfile
- spring 이미지를 생성할 때 필요한 환경 정보를 담은 dockerfile입니다.

#### redis/
- redis container와 mount된 폴더입니다.

#### db/
- mysql container와 mount된 폴더입니다.


### 그 외

#### application.yml
- 서버 환경 설정을 하는 파일입니다.
- 토큰 키, 파일 업로드 위치, 메일 인증에 대한 설정을 해당 파일에서 진행합니다.
- 메일 인증의 경우 username(email) 및 password, host(사용하는 mail 시스템 정보)를 적어주셔야 기능이 올바르게 작동하오니 꼭 기입하시길 바랍니다.
- datasource(DB) 설정은 현재 docker 환경으로 지정되어 있습니다. 추후 환경에 따라 datasource 내부 url을 변경하시길 바랍니다.

#### static/
- src 와 같은 레벨의 static 폴더는 실제 파일(이미지, pdf 등)이 저장되는 위치입니다.
