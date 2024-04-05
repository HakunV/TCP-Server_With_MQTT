# TCP-Server

The Server folder contains the files used for the server side:

Server.java contains the main server-loop, by accepting incoming connection requests and starting threads for each client while keeping track of the connected clients.

ClientHandler.java is a runnable and contains a single socket. The main loop is taking input from a BufferedInputStream, and processing the static parts of the incoming packet. then it sends the content to the ProtocolHandler.

ProtocolHandler.java is responsible for handling the incoming packet depending on the packet type. If the packet requires a response it tells ClientHandler which then tells ClientWriter.

ClientWriter.java is responsible for the BufferedOutputStream, thus writing to the other end of the socket. It also opens a Window, which has som templates of commands that are available to send.

Window.java is a GUI, where you can write a command you want to send through the socket. It shows the available commands, and also contains templates of them.

CRC_Table.java is responsible for encoding the message using CRC-ITU.



The Client folder contains the files used for a dummy client side

ClientDummy.java contains the socket and the main loop of writing to the BufferedOutputStream.

Receiver.java is responsible for receiving from the BufferedInputStream, and printing the received message.
