ECHO OFF

REM
REM Change the below variables appropriately
REM

SET USERNAME=root
SET HOST_NAME=127.0.0.1
SET DB_NAME=
SET PASSWORD=

ECHO ON
ECHO Creating Database 

ECHO OFF
mysql -u%USERNAME% -p%PASSWORD% -h%HOST_NAME% -e "CREATE DATABASE %DB_NAME% CHARACTER SET utf8mb4"

ECHO "Creating Schema Users"

ECHO OFF
mysql -u %USERNAME% -p%PASSWORD% -h%HOST_NAME% -D%DB_NAME% < users.sql
ECHO ON

ECHO "Creating Schema Accumulo Roles"

ECHO OFF
mysql -u %USERNAME% -p%PASSWORD% -h%HOST_NAME% -D%DB_NAME% < accumuloroles.sql
ECHO ON

ECHO "Creating Schema Role"

ECHO OFF
mysql -u %USERNAME% -p%PASSWORD% -h%HOST_NAME% -D%DB_NAME% < role.sql
ECHO ON

ECHO "Creating Schema Search Repository"

ECHO OFF
mysql -u %USERNAME% -p%PASSWORD% -h%HOST_NAME% -D%DB_NAME% < searchrepository.sql
ECHO ON

ECHO "Creating Schema Search Parameters"

ECHO OFF
mysql -u %USERNAME% -p%PASSWORD% -h%HOST_NAME% -D%DB_NAME% < searchparameters.sql
ECHO ON

ECHO "Loading Application data"

ECHO OFF
mysql -u %USERNAME% -p%PASSWORD% -h%HOST_NAME% -D%DB_NAME% < data.sql
ECHO ON
