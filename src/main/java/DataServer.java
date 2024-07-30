import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * @DESRIPTION  This class creates a server that is able
 * to receive a get request and returns a json object
 * it is also able to receive data in a get request
 * and save it to a file
 * **/
public class DataServer {
    int port;
    ServerSocket serverSocket;
    Socket client_socket;
    BufferedReader reader;
    PrintWriter writer;
    /**
     * @DESRIPTION  Create a Data server object
     * @param port- The port number for service
     * @return void
     * **/
    public DataServer(int port) {
        this.port = port;
        init();
    }
    /**
     * @DESRIPTION  creates and initialises a server socket
     * @return void
     * **/
    public void init() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @DESRIPTION  The server loop, it starts the server socket,
     * and handles the request from clients. it parses the url and responds
     * accordingly
     * @return void
     * **/
    public void listen() {
        try {
            System.out.println("server started... listening on port " + this.port);
            while (true) {
                client_socket = serverSocket.accept();
                /** request received **/
                System.out.println("Received a request: "+new Date());
                /** initialise stream the stream reader & writer */
                reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
                writer = new PrintWriter(client_socket.getOutputStream(), true);
                /* Stream buffer */
                StringBuilder request = new StringBuilder();
                String line;
                /* read the request */
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    request.append(line);
                }
                /* convert the header to an array */
                String[] requestArray = request.toString().split("\\n");
                /* get the first line */
                String[] firstLine = requestArray[0].split(" ");
                /* select an action based on the url */
                if (firstLine[0].equals("GET") && firstLine[1].equals("/users")) {
                    returnData();
                } else if (firstLine[0].equals("GET") && firstLine[1].startsWith("/user?")) {
                    String[] content = firstLine[1].split("\\?");
                    saveData(content[1]);
                    /* handle an invalid request */
                } else {
                    writer.println("HTTP/1.1 404 Not Found");
                    writer.println();
                    writer.close();
                }
                client_socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @DESRIPTION- creates a data server instance
     * run this file before any client request
     * @return void
     * **/
    public static void main(String[] args) {
        DataServer dataServer = new DataServer(4000);
        dataServer.listen();
    }
    /**
     * @DESRIPTION  responds to a get request by sending
     * data saved in a file
     * @return void
     * **/
    public void returnData() {
        //** Read the file if it exists **/
        try {
            File file = new File("objects.json");
            /* do not read it if it does not exist */
            if (file.exists() && file.length() > 0) {
                FileReader fileReader = new FileReader("objects.json");
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: application/json");
                writer.println();
                /* read the file and send the data through the output stream */
                char[] buffer = new char[1024];
                int bytesRead;
                while ((bytesRead = fileReader.read(buffer)) != -1) {
                    writer.write(buffer, 0, bytesRead);
                }
                fileReader.close();
                writer.close();
            }else {
                /* the file does not exist, sends an empty json object */
                this.writer.println("HTTP/1.1 200 OK");
                this.writer.println("Content-Type: application/json");
                this.writer.println();
                this.writer.println("{}");
                this.writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @DESRIPTION  responds to a get request with parameters,
     * it saves the data saved to a file
     * @param content- url parameter data from the client
     * @return void
     * **/
    private void saveData(String content) {
        //** create objects to parse json data from a file and hold it **/
        JSONArray objectsArray = new JSONArray();
        JSONParser jsonParser = new JSONParser();
        //** Read the file if it exists, keep the data in memory **/
        try {
            File file = new File("objects.json");
            if (file.exists() && file.length() > 0) {
                FileReader fileReader = new FileReader("objects.json");
                Object jsonobject = jsonParser.parse(fileReader);
                objectsArray = (JSONArray) jsonobject;
                MyObject.count = objectsArray.size();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /* parse the parameters received */
        if (content.contains("&")) {
            /* create a holding object [POJO]
             * and set the data
             * */
            MyObject object = new MyObject();
            String[] parameters = content.split("\\&");
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].startsWith("name")) {
                    String[] property = parameters[i].split("\\=");
                    object.setName(property[1]);
                }
                if (parameters[i].startsWith("sname")) {
                    String[] property = parameters[i].split("\\=");
                    object.setSname(property[1]);
                }
            }
            /* create a JSONObject to representing our POJO */
            JSONObject myobjectDetails = new JSONObject();
            myobjectDetails.put("id", object.getId());
            myobjectDetails.put("name", object.getName());
            myobjectDetails.put("same", object.getSname());

            /* create a JSONObject to contain our POJO */
            JSONObject myobject = new JSONObject();
            myobject.put("object", myobjectDetails);
            /* add it to the array */
            objectsArray.add(myobject);
            try {
                /* save the data to a json file */
                FileWriter fileWriter = new FileWriter("objects.json");
                fileWriter.append(objectsArray.toJSONString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            /* handle one parameter data here, you wo'nt need it */
            System.err.println("One parameter requests not allowed");
        }
        /** Return a success message **/
        this.writer.println("HTTP/1.1 200 OK");
        this.writer.println("Content-Type: text/plain");
        this.writer.println();
        this.writer.println("Success");
        this.writer.close();
    }
}

