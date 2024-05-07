project based on course: https://www.udemy.com/course/microservices-clean-architecture-ddd-saga-outbox-kafka-kubernetes/

project structure:
To generate the structure of the project, I used the following command:
```
mvn com.github.ferstl:depgraph-maven-plugin:aggregate -DcreateImage=true -DreduceEdges=false -Dscope=compile "-Dincludes=com.andrei.food.ordering.system*:*"
```
![img.png](img.png)
