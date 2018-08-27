
./gradlew clean bootRepackage

docker build --rm . --tag yangliumingtest/zuul:1

docker stack deploy todo -c docker-compose.yml
