FROM openjdk:11.0.16-jre-slim
ENV APP_FILE sample-spring-webflux-0.0.2.jar
ENV APP_HOME /usr/apps
EXPOSE 8080
COPY target/$APP_FILE $APP_HOME/
WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $APP_FILE"]