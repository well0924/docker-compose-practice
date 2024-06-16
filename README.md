# Docker Compose  실습 

### 목표

1.**Docker-Compose를 사용해서 다중 컨테이너를 띄우기**

2.**Redis를 Session과 Cache서버로 나누기**

3.**스프링 서버를 여러대 띄운뒤 세션이 공유가 되는지 확인하기**

### 1. Docker-Compose?
Docker-Compose는 도커파일을 사용해서 프로젝트를 관리를 하면 여러개의 컨테이너를 실행을 해야
하는데 일일히 컨테이너마다 명령어를 입력을 하고 관리를 하는 것은 쉽지가 않다.이러한 불편함을
해소하고자 나온 것이 도커 컴포즈이다.

### 2. Docker-Compose를 사용해서 컨테이너 띄우기.

```
version: '3'

services:
  database:
    container_name: maria-db
    image: mariadb:10.11.2
    environment:
      MARIADB_DATABASE: test
      MARIADB_USER: "root"
      MARIADB_ROOT_PASSWORD: "root"
      MARIADB_HOST: '%'
      TZ: 'Asian/Seoul'
    ports:
      - "3307:3306"
    restart: always
    networks:
      - compose
  redis-cache:
    container_name: redis-cache
    image: redis:latest
    command: redis-server --port 6379
    ports:
      - "6379:6379"
    networks:
      - compose
  redis-session:
    container_name: redis-session
    image: redis:latest
    command: redis-server --port 6380
    ports:
      - "6380:6379"
    networks:
      - compose
  application1:
    container_name: application-1
    restart: always
    build: .
    ports:
      - "8081:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://maria-db:3306/test
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "root"
    depends_on:
      - database
      - redis-cache
      - redis-session
    networks:
      - compose

  application2:
    container_name: application-2
    restart: always
    build: .
    ports:
      - "8082:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://maria-db:3306/test
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "root"
    depends_on:
      - database
      - redis-cache
      - redis-session
    networks:
      - compose

  application3:
    container_name: application-3
    restart: always
    build: .
    ports:
      - "8083:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://maria-db:3306/test
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "root"
    depends_on:
      - database
      - redis-cache
      - redis-session
    networks:
      - compose
networks:
  compose:
  
```

위의 설정파일을 하나씩 보도록 하자.

``
version : '3' 
``
이것은 도커 컴포즈의 버전을 말합니다.

``
services:
``
service는 service 밑으로 실행하려는 컨테이너를 설정할 수 있습니다. 

````
database:
container_name: maria-db
image: mariadb:10.11.2
environment:
MARIADB_DATABASE: test
MARIADB_USER: "root"
MARIADB_ROOT_PASSWORD: "root"
MARIADB_HOST: '%'
TZ: 'Asian/Seoul'
ports:
- "3307:3306"
  restart: always
  networks:
- compose
redis-cache:
    container_name: redis-cache
    image: redis:latest
    command: redis-server --port 6379
    ports:
      - "6379:6379"
    networks:
      - compose
  redis-session:
    container_name: redis-session
    image: redis:latest
    command: redis-server --port 6380
    ports:
      - "6380:6379"
    networks:
      - compose
````
database(MariaDB)에 관련된 컨테이너의 설정입니다. 설정 내용은 다음과 같습니다.
container_name: 컨테이너의 이름
image: 해당 컨테이너의 이미지
environment : 디비에 관련된 설정을 의미합니다. 
port : 포트의 경우에는 '호스트 포트:컨테이너 포트'로 되어있습니다. 여기서 호스트 포트는 외부에 노출할 포트를 의미합니다. 
restart : 컨테이너를 다시 시작을 할때 사용되는 설정이고 특정 컨테이너가 작동하지 않을때만 사용이 되는 설정도 있습니다.  
network : 서비스(컨테이너)가 소속된 네트워크 입니다. 따로 지정하지 않을 경우 default_${project}와 같이 지정됩니다. 기본적으로 컨테이너는 같은 네트워크에 있어야 서로 통신이 가능합니다.
command : 컨테이너가 실행될 때 수행할 명령어를 설정합니다.

```
application1:
    container_name: application-1
    restart: always
    build: .
    ports:
      - "8081:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://maria-db:3306/test
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "root"
    depends_on:
      - database
      - redis-cache
      - redis-session
    networks:
      - compose

  application2:
    container_name: application-2
    restart: always
    build: .
    ports:
      - "8082:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://maria-db:3306/test
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "root"
    depends_on:
      - database
      - redis-cache
      - redis-session
    networks:
      - compose

  application3:
    container_name: application-3
    restart: always
    build: .
    ports:
      - "8083:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://maria-db:3306/test
      SPRING_DATASOURCE_USERNAME: "root"
      SPRING_DATASOURCE_PASSWORD: "root"
    depends_on:
      - database
      - redis-cache
      - redis-session
    networks:
      - compose
```
위에서 작성한 설정에서 중복된 부분은 제외한 부분을 설명하면 다음과 같다.

depends_on : 특정 컨테이너의 의존 관계를 설정합니다.
build : 도커 이미지를 빌드하기 위한 경로나 설정을 지정합니다.

### Docker-Compose 명령어

docker-compose를 실행하는 명령어

docker-compose up

docker-compose를 종료하는 명령어

docker-compose down





