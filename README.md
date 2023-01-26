Docker-Elastic-Connector

This project brings up a three node Elastic cluster and a Kibana so you can see how things work.

Steps to follow (Assuming that docker is installed and docker daemon is running)


1. create a file called docker-compose.yml  
2. run docker-compose up -d (it will take few mins to run the setup)
3. docker ps (to check the current running processes, you'll notice 3 elastic and one kibana image)
4. curl -X GET "localhost:9200/_cat/nodes?v&pretty" (to check your elastic nodes status)
5. curl -X PUT "localhost:9200/index-logger?pretty" -H 'Content-Type: application/json' -d'
   {
     "settings": {
       "number_of_shards": 3,
       "number_of_replicas": 2
     }
   }
   '
   (to create an index named index-logger. also this elastic instance doesn't require any authentication to connect to)
 6. delete index using curl -X DELETE "localhost:9200/index-logger?pretty"

