# TCP-Server

The Server folder contains the files used for the server side:

To find the actual server code, navigate to "src/main/java/tcpserver":

Server folder:

Server.java contains the main server-loop, by accepting incoming connection requests and starting threads for each client while keeping track of the connected clients.

ClientHandler.java is a runnable and contains a single socket. The main loop is taking input from a BufferedInputStream, and processing the static parts of the incoming packet. then it sends the content to the ProtocolHandler.

ProtocolHandler.java is responsible for handling the incoming packet depending on the packet type. If the packet requires a response it tells ClientHandler which then tells ClientWriter.

ClientWriter.java is responsible for the BufferedOutputStream, thus writing to the other end of the socket.




Backend folder, communicating with thingsofinter.net via MQTT:

BackendClient.java is the main loop communicating with thingsofinter.net.

MQTT_PubPayload.java contains the fields of the JSON format sent when publishing.

CommunicationFlow folder keeps track of packet awaiting acknowledgement

MQTT_ProtocolHandler process the incoming packets.




The Client folder contains the files used for a dummy client side, used for testing

ClientDummy.java contains the socket and the main loop of writing to the BufferedOutputStream.

Receiver.java is responsible for receiving from the BufferedInputStream, and printing the received message.




The Downlink folder contains the program responsible for accepting incoming clients that wants to send commands to the trackers.




To find extra programs, such as the UDP code, navigate to "Extra":

The UDP folder contains the program that posts data to thingsofinter.net via UDP.

The CreateIMEI folder contains a program that creates 1000 different IMEIs and saves them to a file.

The Client folder contains a client that sends  whatever is written in the terminal to our server on thingsofinter.



The server implemented on thingsofinter.net can be run from this file: TCP-Server_With_MQTT.jar
