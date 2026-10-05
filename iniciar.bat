@echo off
title Backend - Sistema de Cotizaciones (Spring Boot + MySQL)
echo ========================================================
echo   Iniciando Backend Spring Boot en http://localhost:8080
echo ========================================================
cd /d "%~dp0"
mvn spring-boot:run
pause
