FROM azul/zulu-openjdk-alpine:17-latest
LABEL AUTHOR = YoeungHyeon(yhoung11@gmail.com)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} milkweb.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/milkweb.jar"]

