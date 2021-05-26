FROM openjdk:11

MAINTAINER Ihar Niakhlebau

ENV DOWNLOAD https://h2database.com/h2-2019-03-13.zip
ENV DATA_DIR /opt/h2-data

RUN mkdir -p ${DATA_DIR} \
    && curl ${DOWNLOAD} -o h2.zip \
    && unzip h2.zip -d /opt/ \
    && rm h2.zip

COPY h2.server.properties /root/.h2.server.properties

WORKDIR /opt/h2-data
COPY init.sql /opt/h2-data/init.sql

CMD java -cp /opt/h2/bin/h2*.jar org.h2.tools.Server -ifNotExists\
 	-web -webAllowOthers -webPort 81 \
 	-tcp -tcpAllowOthers -tcpPort 1521 \
 	-baseDir ${DATA_DIR} ${H2_OPTIONS}