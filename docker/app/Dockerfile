
# Build using tools-version of elixir
FROM openjdk:8-jre-alpine AS base
ARG uid
ARG scala_version
ARG sbt_version

# install packages
RUN apk add --no-cache bash curl git

# create user
RUN adduser -D -h /home/app -s /bin/bash -u $uid app

# change shell to bash
SHELL ["/bin/bash", "-l", "-c"]

# install scala and sbt
RUN \
  echo "$scala_version $sbt_version" && \
  mkdir -p /usr/lib/jvm/java-1.8-openjdk/jre && \
  touch /usr/lib/jvm/java-1.8-openjdk/jre/release && \
  curl -fsL http://downloads.typesafe.com/scala/$scala_version/scala-$scala_version.tgz | tar xfz - -C /usr/local && \
  ln -s /usr/local/scala-$scala_version/bin/* /usr/local/bin/ && \
  scala -version && \
  scalac -version
RUN \
  curl -fsL https://github.com/sbt/sbt/releases/download/v$sbt_version/sbt-$sbt_version.tgz | tar xfz - -C /usr/local && \
  ln -s /usr/local/sbt/bin/* /usr/local/bin/ && \
  sbt sbt-version || sbt sbtVersion || true


FROM base AS deps
USER app
WORKDIR /app
RUN sbt compile

FROM deps AS final
## Link to local history for preservation
RUN ln -s /app/docker/app/.bash_history /home/app/.bash_history
RUN ln -s /app/docker/app/.scala_history /home/app/.scala_history
CMD ["/bin/bash", "/app/docker/app/init.sh"]



