all: build

JAR=target/CNT5106C-1.0.jar

build:
	mvn package

build-rel:
	ant -f build/build.xml

clean:
	rm -rf *.log *.log*
	mvn clean dependency:copy-dependencies
