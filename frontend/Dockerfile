# this file is used by openshift to build
# this can also be used by docker compose to build the application e.g. for running it locally

# Basing on the image that can run on both openshift and docker, from the pubic repository
FROM node:22.18.0-alpine3.22@sha256:1b2479dd35a99687d6638f5976fd235e26c5b37e8122f786fcd5fe231d63de5b AS builder

# This Dockerfile uses labels from the label-schema namespace from http://label-schema.org/rc1/
LABEL maintainer="yana.panchenko@tuwien.ac.at" \
        org.label-schema.name="refop-fe" \
        org.label-schema.description="Unified information about funding programs and calls" \
        org.label-schema.usage="https://github.com/tuwien-csd/refop-fe/tree/master/README.md" \
        org.label-schema.vendor="Technische Universität Wien" \
        org.label-schema.url="https://github.com/tuwien-csd/refop-fe" \
        org.label-schema.vcs-url="https://github.com/tuwien-csd/refop-fe" \
        org.label-schema.schema-version="1.0" \
        org.label-schema.docker.cmd="docker run -d -p 8080:8080 refop"

WORKDIR /app

COPY package.json package-lock.json /app/

COPY . /app

# install and build the application on the builder container

RUN npm ci

ARG APP=refop-fe
ARG CONFIG=production

RUN npm run build ${APP} --configuration=${CONFIG}


# create a second container running a webserver and holding the built frontend application
FROM nginxinc/nginx-unprivileged@sha256:2d7f8850ae009a18b8b801114bbfac8606eb86dcf63ea7f26e77095d44adddbb AS runner
ADD docker/conf.d/* /etc/nginx/conf.d

COPY --from=builder --chown=1001:0 /app/dist/refop-fe/browser /usr/share/nginx/html/
