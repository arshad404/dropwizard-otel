run:
	mvn clean install && java -jar target/helloworld-0.0.0.jar server config.yml

start-jeagar:
	docker-compose up
