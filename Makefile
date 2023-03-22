
clean:
	rm -rf *.class

build:
	javac *.java

all:
	make clean
	make build

