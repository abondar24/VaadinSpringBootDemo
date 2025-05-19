package org.abondar.experimental.articlemanager.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class ContainerConfig {

     static final GenericContainer<?> NEO_4_J;


   static final GenericContainer<?> LOCAL_STACK;

   static {
       NEO_4_J = new Neo4jContainer<>("neo4j:latest")
               .withExposedPorts(7687)
               .withEnv("NEO4J_AUTH", "none");
       NEO_4_J.start();

       LOCAL_STACK = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
               .withServices(LocalStackContainer.Service.S3)
               .withExposedPorts(4566);
       LOCAL_STACK.start();
   }
}
