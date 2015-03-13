all: build

JAR=target/CNT5106C-1.0.jar

build:
	mvn package

build-rel:
	ant -f build/build.xml

zip-rel: clean
	ant -f build/build.xml zip

clean:
	rm -rf *.log *.log* *.zip
	mvn clean dependency:copy-dependencies
