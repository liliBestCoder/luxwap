@echo off
REM ============================================================
REM RuoYi / xray_partner Database Backup Script
REM Dumps full schema + data from local MySQL
REM ============================================================
setlocal enabledelayedexpansion

REM --- Database Config ---
set DB_HOST=127.0.0.1
set DB_PORT=3306
set DB_USER=root
set DB_PASS=123456
set DB_NAME=xray_partner

REM --- Backup Config ---
set BACKUP_DIR=%~dp0backup
set TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%
set BACKUP_FILE=%BACKUP_DIR%\%DB_NAME%_%TIMESTAMP%.sql
set ZIP_FILE=%BACKUP_FILE%.zip

if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

echo ============================================================
echo   RuoYi Database Backup
echo   Database: %DB_NAME%
echo   Timestamp: %TIMESTAMP%
echo ============================================================

echo [1/2] Dumping database structure + data...
mysqldump -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% ^
  --databases %DB_NAME% ^
  --routines ^
  --triggers ^
  --events ^
  --add-drop-database ^
  --add-drop-table ^
  --create-options ^
  --complete-insert ^
  --default-character-set=utf8mb4 ^
  --result-file="%BACKUP_FILE%"

if %ERRORLEVEL% neq 0 (
    echo [FAIL] mysqldump failed!
    pause
    exit /b 1
)

echo [2/2] Compressing...
powershell -Command "Compress-Archive -Path '%BACKUP_FILE%' -DestinationPath '%ZIP_FILE%' -Force"

if %ERRORLEVEL% neq 0 (
    echo [WARN] Compression failed, SQL file kept as-is.
) else (
    del "%BACKUP_FILE%"
    echo        Compressed: %ZIP_FILE%
)

echo ============================================================
echo   Backup complete!
echo   File: %ZIP_FILE%
echo ============================================================
endlocal
