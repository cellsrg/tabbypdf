.PHONY: assembly-single single jar install build compile clean all run

VERSION=1.0-SNAPSHOT
JAR=target/tabbypdf-$(VERSION)-jar-with-dependencies.jar
SRC=$(shell find src -name "*.java")
RES=src/test/resources/pdf
all: assembly-single

jar: assembly-single

single: assembly-single

assembly-single: $(JAR)

$(JAR): $(SRC)
	mvn clean compile assembly:single

compile:
	mvn compile

build: install

install:
	mvn install

clean:
	mvn clean
	# Also remove "out" and everything under it.
	rm -rf out/
	rm -rf $(RES)/edit/xml/*.xml
	rm -rf $(RES)/edit/html/*.html
	rm -rf $(RES)/edit/*.pdf

run: assembly-single
	java -jar $(JAR)


