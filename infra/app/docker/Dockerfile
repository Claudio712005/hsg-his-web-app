FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /build
COPY . .

RUN mvn -B clean package -DskipTests

RUN curl -L -o /tmp/postgresql.jar \
    https://repo1.maven.org/maven2/org/postgresql/postgresql/42.6.0/postgresql-42.6.0.jar

FROM quay.io/wildfly/wildfly:26.1.3.Final-jdk11

COPY --from=builder /tmp/postgresql.jar /opt/postgresql.jar

COPY infra/app/docker/configure.cli /opt/configure.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/configure.cli

COPY --from=builder /build/hsg-his-ear/target/hsg-his-ear-1.0-SNAPSHOT.ear \
    /opt/jboss/wildfly/standalone/deployments/hsg-his.ear

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", \
     "-b", "0.0.0.0", \
     "-bmanagement", "0.0.0.0"]
