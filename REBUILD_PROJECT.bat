@echo off
echo ====================================
echo Limpiando y reconstruyendo proyecto
echo ====================================
echo.

cd /d "%~dp0"

echo Paso 1: Limpiando proyecto...
call gradlew.bat clean

echo.
echo Paso 2: Reconstruyendo proyecto...
call gradlew.bat build

echo.
echo ====================================
echo Proceso completado
echo ====================================
pause

