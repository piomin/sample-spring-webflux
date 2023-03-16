## Spring WebFlux and Reactive WebClient Demo Project [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)

[![CircleCI](https://circleci.com/gh/piomin/sample-spring-webflux.svg?style=svg)](https://circleci.com/gh/piomin/sample-spring-webflux)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-webflux)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-webflux&metric=bugs)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-microservices-new)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-webflux&metric=coverage)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-microservices-new)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-webflux&metric=ncloc)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-microservices-new)

In this demo project you may examples of reactive endpoints built on top of Spring WebFlux, and using `WebClient` for calling other resources.
1. Explaining different communication models in reactive application with Spring WebFlux. Differences between standard `application/json` content type and `application/stream+json`. Introduction to back pressure term. Detailed description can be found here: [Using Reactive WebClient with Spring WebFlux](https://piotrminkowski.com/2019/11/04/using-reactive-webclient-with-spring-webflux/)
2. A detailed analyse of threading and concurrency model used by Spring WebFlux and Reactor Netty. It also threats about `WebClient` and Spring Boot Actuator pooling. A detailed description can be found here: [A Deep Dive Into Spring WebFlux Threading Model](https://piotrminkowski.com/2020/03/30/a-deep-dive-into-spring-webflux-threading-model/)

### Guide

Required:
1. JDK 11+
2. Maven

How to run ?
`mvn clean install`

There are two JUnit tests: `PerformanceSpringWebFluxTest` and `SampleSpringWebFluxTest`.

To provide some standalone tests just run the main class `SampleSpringWebFluxApp`.
For generating some traffic to the application run application `SendingApp`. It runs `MockWebServer` on localhost:8082 in order to mock downstream service called by `WebClient` and than calls endpoints exposed by the application usung `TestRestTemplate`.