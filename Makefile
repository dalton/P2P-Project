all: build

JAR=target/CNT5106C-1.0.jar

build:
	mvn package
		 
clean:
	rm -rf *.log *.log*
	mvn clean dependency:copy-dependencies
