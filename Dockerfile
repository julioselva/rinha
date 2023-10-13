FROM sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.8.1_1_1.9.6_2.13.12 AS BUILDER

WORKDIR /app

COPY build.sbt  /app/build.sbt
COPY project    /app/project
COPY src        /app/src

RUN sbt clean universal:packageZipTarball
RUN tar -xvf target/universal/rinha-*.tgz --directory=/app/target/universal

FROM public.ecr.aws/amazoncorretto/amazoncorretto:17.0.8-al2023-headless

WORKDIR /app

COPY --from=BUILDER --chown=nobody:nobody /app/target/universal/bin         /app/bin
COPY --from=BUILDER --chown=nobody:nobody /app/target/universal/lib         /app/lib
COPY --from=BUILDER --chown=nobody:nobody /app/target/universal/resources   /app/resources

ADD https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_x86_64 /bin/dumb-init
RUN chmod +x /bin/dumb-init

RUN chown -R nobody:nobody /app
USER nobody

ENTRYPOINT ["dumb-init", "/app/bin/rinha"]
