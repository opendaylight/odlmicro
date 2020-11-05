#This dockerfile is for building demo netconf container and same can be used for others as well.
FROM woahbase/alpine-openjdk11

LABEL name="OdlMicro Dockerfile" \
      version="1.0" \
      vendor="OpenDaylight" \
      summary="OpenDaylight Micro Alpine Jre Headless Dockerfile"

ENV JAVA_HOME /usr/lib/jvm/jre-11-openjdk/

RUN mkdir -p /opt/odlmicro/
WORKDIR /opt/odlmicro/
#Extracted directory of micro.tar artifact should be present in the same directory as this file
COPY micro-netconf-1.0.0-SNAPSHOT /opt/odlmicro/

EXPOSE 8101
EXPOSE 8181
EXPOSE 22
EXPOSE 9090

CMD ["java","-Xms128M", "-Xmx2048m", "-XX:+UnlockDiagnosticVMOptions", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:+DisableExplicitGC", "-Dcom.sun.management.jmxremote", "-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/./urandom", "-cp", "etc/initial/*:/opt/odlmicro/lib/*", "org.opendaylight.netconf.micro.NetconfMain"]
