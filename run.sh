#!/bin/sh

if [ -z `docker-compose ps -q rethinkdb` ] || [ -z `docker ps -q --no-trunc | grep $(docker-compose ps -q rethinkdb)` ]; then
  docker-compose up
fi

./gradlew clean runShadow
