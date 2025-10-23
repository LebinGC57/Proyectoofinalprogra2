@echo off
echo ========================================
echo LIMPIANDO PROYECTO SMARTBOY APP
echo ========================================
echo.

cd /d "%~dp0"

echo [1/4] Eliminando carpeta build...
if exist "app\build" (
    rmdir /s /q "app\build"
    echo ✓ Carpeta build eliminada
) else (
    echo ✓ Carpeta build no existe
)

echo.
echo [2/4] Eliminando carpeta .gradle...
if exist ".gradle" (
    rmdir /s /q ".gradle"
    echo ✓ Carpeta .gradle eliminada
) else (
    echo ✓ Carpeta .gradle no existe
)

echo.
echo [3/4] Eliminando archivos .iml...
for /r %%i in (*.iml) do (
    del "%%i"
    echo ✓ Eliminado: %%~nxi
)

echo.
echo [4/4] Eliminando carpeta .idea (opcional)...
if exist ".idea" (
    rmdir /s /q ".idea"
    echo ✓ Carpeta .idea eliminada
) else (
    echo ✓ Carpeta .idea no existe
)

echo.
echo ========================================
echo LIMPIEZA COMPLETADA
echo ========================================
echo.
echo PROXIMOS PASOS:
echo 1. Abre Android Studio
echo 2. File ^> Invalidate Caches / Restart
echo 3. Espera que reindexe el proyecto
echo 4. File ^> Sync Project with Gradle Files
echo 5. Build ^> Clean Project
echo 6. Build ^> Assemble Project
echo 7. Ejecuta con el boton verde ▶️
echo.
echo ========================================
pause

