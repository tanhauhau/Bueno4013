# Bueno4013

> KinderBueno is the main source of inspiration for this project. May the code with KinderBueno chocolate!


## About
This is a project for Nanyang Tehcnological University CE/CZ 4013 Distributed Systems.

The project is to design and implement a distributed system for remote file access based on client-server architecture. The client-server communication is carried out using UDP. Different invocation semantics is used: at-least-once and at-most-once.

## Folder Structure
Directory structure and contents

Directory | Content
----------| -------
`Client/` | Contains code for the client
`Server/` | Contains code for the server
`Client/src/com/client/pack` `Server/src/com/server/pack` | Contains code for marshalling and unmarshalling
`Client/src/com/client/socket` `Server/src/com/server/socket`  | Contains code for various socket decorator
`Client/src/com/client/strategy` `Server/src/com/server/strategy` | Contains code for strategy, such as `WriteStrategy`, `ReadStrategy`, and etc.
`Client/src/net/sourceforge/argparse4j` `Server/src/net/sourceforge/argparse4j` | [Argparse4j Library](https://argparse4j.github.io) for parsing argument
`merge.sh` | Bash script to concatenate all Java code into a file

## Get Started

To start server

`
java Server [-h] -p PORT [-iv {AT-LEAST-ONCE,AT-MOST-ONCE}] -f FOLDER [-l LAG] [-g GIBBERISH] [-s SEND]
`

To start client

`
java Client [-h] -ip IP -p PORT -t TIMEOUT -f FOLDER -cf CACHEFRESH [-l LAG] [-g GIBBERISH] [-s SEND][-r RECEIVE] [-i {true,false}]
`

### Optional arguments 
These are optional arguments which are useful for testing purposes.

***-l Lag***

Specify in seconds the delay in sending and receiving packets.

***-g Gibberish***

Specify the probability `[0, 1]` of sending malformed packets. Default `0`.

***-s Send***

Specify the probability `[0, 1]` of packet loss when sending packets. Default `0`.

***-r Receive***

Specify the probability `[0, 1]` of packet loss when receiving packets. Default `0`.

***-i Info***

Specify in `true` or `false` whether to show more debug info 


## Concatenate all the code one single file
For submission purpose, the code has to be submitted as one file, for ease of plagiarism checking.

**For Mac/Linux user**

```bash
find . -type f | grep .java | xargs cat > code.txt
``` 


## Contributor
* Tan Li Hau
* Gilbert Khoo