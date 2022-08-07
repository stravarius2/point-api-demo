#POINT-API-DEMO
## Point-api
- 포인트 적립 API
- 포인트 사용 API
- 포인트 조회 API
- 포인트 내역 조회 API
- 포인트 사용 취소 API
## Point scheduler
- 매일 00시 00분 유효기간 지난 포인트 만료 처리

## 환경
- java 1.8
- gradle 7.5
### java 설치
- ubuntu
<pre>
$ sudo apt-get update
</pre>
<pre>
$ sudo apt-get install openjdk-8-jdk
</pre>
- centOS
<pre>
$ yum update
</pre>
<pre>
$ yum install java-1.8.0-openjdk-devel.x86_64
</pre>
### Gralde 설치
- https://gradle.org/install/
## 빌드
#### project root directory
<pre>
$ ./gradlew bootJar
</pre>
#### Excutable jar 생성 확인
<pre>
{projectRoot}/build/libs directory > point-api.jar
</pre>
## 실행
<pre>
$ java -jar point-api.jar
</pre>
## 포트충돌 시 
<pre>
$ java -jar -DServer.port=포트번호 point-api.jar
</pre>
## Swagger
- URL : http://localhost:8080/swagger-ui/index.html
- 포인트 적립 : http://localhost:8080/swagger-ui/index.html#/point-controller/savePointUsingPOST
- 포인트 사용 : http://localhost:8080/swagger-ui/index.html#/point-controller/usePointUsingPOST
- 포인트 조회 : http://localhost:8080/swagger-ui/index.html#/point-controller/getPointUsingGET
- 포인트 내역 조회 : http://localhost:8080/swagger-ui/index.html#/point-controller/getPointHistoryUsingGET
- 포인트 사용 취소 : http://localhost:8080/swagger-ui/index.html#/point-controller/cancelPointUsingDELETE
## DB 접속
- http://localhost:8080/h2
- JDBC URL: jdbc:h2:./point
- connect