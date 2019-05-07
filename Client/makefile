CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude -lpthread
LDFLAGS:=-lboost_system -lpthread

all: Client
	g++ -o bin/BGSclient bin/connectionHandler.o bin/clientMain.o bin/encoderDecoder.o bin/Sender.o $(LDFLAGS) 

Client: bin/connectionHandler.o bin/clientMain.o bin/encoderDecoder.o bin/Sender.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/clientMain.o: src/clientMain.cpp
	g++ $(CFLAGS) -o bin/clientMain.o src/clientMain.cpp

bin/encoderDecoder.o: src/encoderDecoder.cpp
	g++ $(CFLAGS) -o bin/encoderDecoder.o src/encoderDecoder.cpp

bin/Sender.o: src/Sender.cpp
	g++ $(CFLAGS) -o bin/Sender.o src/Sender.cpp

	
.PHONY: clean
clean:
	rm -f bin/*
