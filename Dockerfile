FROM registry.globalrescue.com:4567/gr/grcom/gr-census-management-service/grcms-base:beta
MAINTAINER Sohail Meer <smeer@globalrescue.com>

ARG mysql 
ARG username 
ARG password
ENV db_host=$mysql db_username=$username db_password=$password 

RUN mkdir /opt/wildfly/standalone/deployments/gr-census-management-service.war
ADD build/standalone/deployments/gr-census-management-service.war /opt/wildfly/standalone/deployments/gr-census-management-service.war
RUN touch /opt/wildfly/standalone/deployments/gr-census-management-service.war.dodeploy

EXPOSE 8080

CMD ["/opt/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]