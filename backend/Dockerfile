# Use multi-stage builds to keep the final image as small as possible
FROM maven:3.9.9-eclipse-temurin-21 AS builder
ARG BUILD_HOME=/home/app
RUN mkdir $BUILD_HOME && mkdir -p $BUILD_HOME/.m2/repository && chown -R 1000:0 $BUILD_HOME
USER 1000
WORKDIR $BUILD_HOME

COPY --chown=1000:0 . .

VOLUME ["/home/app/.m2/repository"]

RUN mvn -Duser.home=$BUILD_HOME -B package -DskipTests

FROM registry.access.redhat.com/ubi8/ubi-minimal:8.9 AS runner
ARG JAVA_PACKAGE=java-21-openjdk-headless
ARG RUN_JAVA_VERSION=1.3.8
ARG BUILD_HOME=/home/app
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
# Install java and the run-java script
# Also set up permissions for user `1001`
RUN microdnf install openssl curl ca-certificates ${JAVA_PACKAGE} \
    && microdnf update \
    && microdnf clean all \
    && mkdir /deployments \
    && chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments \
    && curl https://repo1.maven.org/maven2/io/fabric8/run-java-sh/${RUN_JAVA_VERSION}/run-java-sh-${RUN_JAVA_VERSION}-sh.sh -o /deployments/run-java.sh \
    && chown 1001 /deployments/run-java.sh \
    && chmod 755 /deployments/run-java.sh \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/lib/security/java.security

# Configure the JAVA_OPTIONS
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Duser.home=/deployments -Dquarkus.profile=docker"

COPY --from=builder $BUILD_HOME/bootstrap/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder $BUILD_HOME/bootstrap/target/quarkus-app/*.jar /deployments/
COPY --from=builder $BUILD_HOME/bootstrap/target/quarkus-app/app/ /deployments/app/
COPY --from=builder $BUILD_HOME/bootstrap/target/quarkus-app/quarkus/ /deployments/quarkus/

# Custom configurations
EXPOSE 8080
USER 1001
HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost:8080/q/health/live || exit 1
ENTRYPOINT [ "/deployments/run-java.sh" ]
