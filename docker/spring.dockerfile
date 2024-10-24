
# openjdk 버전
FROM openjdk:17
VOLUME /tmp
WORKDIR /app
# jar 파일의 위치

ARG JAR_FILE=./build/libs/actoon.jar
# ARG JAR_FILE=./actoon.jar
# ARG TEST_FILE=${PWD}/ddd.txt
# COPY ${TEST_FILE} test.txt
COPY ${JAR_FILE} app.jar

CMD ["ls"]

# 도커 시스템 진입점이 어디인가?
# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
COPY docker/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh && \
    mkdir -p /app/static/uploads && \
    chmod -R 777 /app/static

ENTRYPOINT ["/wait-for-it.sh","db:3306","--","java","-jar","app.jar"]

EXPOSE 8080