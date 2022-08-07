POINT-API
> Point-api
- 포인트 적립 API
- 포인트 사용 API
- 포인트 조회 API
- 포인트 내역 조회 API
- 포인트 사용 취소 API
> Point scheduler
- 매일 00시 00분 유효기간 지난 포인트 만료 처리
> Swagger
- http://localhost:8080/swagger-ui/index.html
- 포인트 적립 : http://localhost:8080/swagger-ui/index.html#/point-controller/savePointUsingPOST
- 포인트 사용 : http://localhost:8080/swagger-ui/index.html#/point-controller/usePointUsingPOST
- 포인트 조회 : http://localhost:8080/swagger-ui/index.html#/point-controller/getPointUsingGET
- 포인트 내역 조회 : http://localhost:8080/swagger-ui/index.html#/point-controller/getPointHistoryUsingGET
- 포인트 사용 취소 : http://localhost:8080/swagger-ui/index.html#/point-controller/cancelPointUsingDELETE
> DB 접속
- http://localhost:8080/h2
- connect
> 환경 및 설치
- java 1.8
- 다운로드: https://www.oracle.com/kr/java/technologies/javase/javase8-archive-downloads.html
 > ubuntu
* sudo apt-get update
* sudo apt-get install openjdk-8-jdk
> centOS
* yum update
* yum install java-1.8.0-openjdk-devel.x86_64
> 실행
- java -jar point-api.jar
>포트충돌 시 
- java -jar -DServer.port=포트번호 point-api.jar