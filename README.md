
# Spring Boot & Angular App

Backend part of Full Stack Application: Angular and Java Spring Boot 

Frontend part: 
https://github.com/kubaokleja/spring-boot-angular-frontend

## Goals

The main goal was to learn angular and connect it with spring boot backend.

I decided to learn by coding. 
Additionally, I wanted to enhance my knowledge about Spring Security. 

## About

Application doesn't have any specific name because I would like to try few different things which are not connected with each other from the business perspective. 
I decided to package classes by domain and communicate between domains only by using their facades. Thanks to that I am able to provide better encapsulation.
I'm aware of my limitations like css styling. I'd like to improve it in the future, but for now I want to focus more on Angular & Spring Boot and try to do many different things to develop as a programmer. This is why also some edge cases are not considered.

### Features
#### User & Security: 
* User management (admin panel)
* User registration and login with JWT authentication
* Email - account activation and password reset
* Password encryption using BCrypt
* Role-based authorization with Spring Security
* Mitigation of Brute Force Attack

#### About me 
 * Page about me with usage of bootstrap
 
#### Home Page 
  * Leaflet map with place where I grew up
  
#### Football API
  * Premier League Top Scorers bookmark
  * Data are taken from external football API (https://www.football-data.org/)
  
#### Profiles
  * Prod profile has email sender with usage of AWS Email Service. Unfortunately, it has to be requested and paid to send emails to everyone. 
  I don't want to exceed my free tier, so in code you can see only configuration for that feature.

### Tech Stack
Java 11, Spring Boot, Angular, Spring Security, JWT,  MySQL, Flyway

### Running the application
You can run backend with usage of docker command: 
*docker-compose -f docker-compose-dev.yml up*

