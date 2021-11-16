# Project Title
User Backend Example Project
# Project Description
This project is a web backend that contains basic CRUD functions and there are two roles (admin and user).
# Feature
- login using username and password, and strengthened by using JWT (JSON Web Token)
- admin can add, view, modify and delete user data
- user can only change phone number
# Deploy
This application can be deployed using a docker container, do the following:
- clone this project
- open a terminal in the project directory
- type `docker build -t backend-user .` to create a docker image
- type `docker-compose up` to create a docker container
- run container
- now the project is running in `localhost:8080` and the database running in `localhost:3306`
# Dependency
1. spring-boot-starter-data-jpa
2. spring-boot-starter-security
3. spring-boot-starter-web
4. spring-boot-devtools
5. mysql-connector-java
6. lombok
7. java-jwt
