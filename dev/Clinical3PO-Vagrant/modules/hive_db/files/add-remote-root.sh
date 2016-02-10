#!/bin/bash
mysql -u root -pPWc3po << EOF
create user 'root'@'%' identified by 'PWc3po';
grant all privileges on *.* to 'root'@'%' with grant option;
EOF
