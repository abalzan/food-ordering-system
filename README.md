project based on course: https://www.udemy.com/course/microservices-clean-architecture-ddd-saga-outbox-kafka-kubernetes/

## Project structure:
To generate the structure of the project, I used the following command:
```
mvn com.github.ferstl:depgraph-maven-plugin:aggregate -DcreateImage=true -DreduceEdges=false -Dscope=compile "-Dincludes=com.andrei.food.ordering.system*:*"
```
![img.png](img.png)

## Running kafka using docker
Run this commands in sequence:
```
 docker compose -f common.yml -f postgresql.yml up
 docker-compose -f common.yml -f zookeeper.yml up
 docker-compose -f common.yml -f kafka_cluster.yml up
 docker-compose -f common.yml -f init_kafka.yml up
```
