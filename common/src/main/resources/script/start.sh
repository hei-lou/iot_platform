docker start redis
docker start zookeeper

nohup java -jar /app/jar/heilou-iot.jar > /dev/null 2>heilou-iot-error.log &
nohup java -jar /app/jar/heilou-web.jar > /dev/null 2>heilou-web-error.log &


docker exec -it zookeeper /bin/bash