#!/bin/bash -xe

apt-get update
export password="password"
apt-get install -y debconf-utils
echo "mysql-server mysql-server/root_password password $password" | sudo debconf-set-selections
echo "mysql-server mysql-server/root_password_again password $password" | sudo debconf-set-selections
apt-get install -y default-jdk tomcat8 mysql-server maven unzip

echo JAVA_HOME=\"$(readlink -f /usr/bin/java | sed "s:/bin/java::")\" >> /etc/environment
source /etc/environment

mysql -uroot -p$password < schema.sql

cd ~
wget https://s3.ap-south-1.amazonaws.com/training-downloads/webproject.zip
unzip webproject.zip
cd webproject/
sed -i -e 's/"root",""/"root","'"$password"'"/g' src/main/java/com/skaas/core/MySQLConnector.java
mvn package
cp ./target/webapp-1.0.0.war /var/lib/tomcat8/webapps/myapp.war
cd /var/lib/tomcat8/webapps/myapp