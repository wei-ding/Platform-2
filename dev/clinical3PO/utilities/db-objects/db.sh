#!/bin/sh

#
# Change the below variables appropriately
#

USERNAME=root
HOST_NAME=127.0.0.1
DB_NAME=
PASSWORD=

echo "Creating Database"


mysql -u$USERNAME -p$PASSWORD -h$HOST_NAME -e "CREATE DATABASE $DB_NAME CHARACTER SET utf8"

echo "Creating Schema Users"


mysql -u $USERNAME -p$PASSWORD -h$HOST_NAME -D$DB_NAME < users.sql


echo "Creating Schema Accumulo Roles"


mysql -u $USERNAME -p$PASSWORD -h$HOST_NAME -D$DB_NAME < accumuloroles.sql


echo "Creating Schema Role"


mysql -u $USERNAME -p$PASSWORD -h$HOST_NAME -D$DB_NAME < role.sql


echo "Creating Schema Search Repository"


mysql -u $USERNAME -p$PASSWORD -h$HOST_NAME -D$DB_NAME < searchrepository.sql


echo "Creating Schema Search Parameters"


mysql -u $USERNAME -p$PASSWORD -h$HOST_NAME -D$DB_NAME < searchparameters.sql


echo "Loading Application data"


mysql -u $USERNAME -p$PASSWORD -h$HOST_NAME -D$DB_NAME < data.sql

