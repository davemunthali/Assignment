import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * @DESRIPTION  This class create a client object
 * that can send data through a get request to a
 * Data server object
 * **/
public class Client {
    URL url;
    HttpURLConnection urlConnection;
    BufferedReader bufferedReader;
    StringBuilder data;
    /**
     * @DESRIPTION  sets & retrieves data from a server
     * @param url- The url of the server with the service port
     * @return Returns the response from the server
     * **/
    public String getData(String url){
        try{
            this.url = new URL(url);
            this.urlConnection = (HttpURLConnection) this.url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            /** send the request & get the response code **/
            int responseCode = urlConnection.getResponseCode();
            data = new StringBuilder();
            if(responseCode == HttpURLConnection.HTTP_OK){
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while((line = bufferedReader.readLine()) != null){
                    data.append(line);
                }
                bufferedReader.close();
            }else {
                System.err.println("Bad Response from the server");
                data.append("error");
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        urlConnection.disconnect();
        return data.toString();
    }
    /*
        creates client object to send and
        receive data from a data server object
     */
    public static void main(String[] args) {
        Client client = new Client();
        String response = client.getData("http://localhost:4000/user?name=Joe&sname=Doe");
        //String response = client.getData("http://localhost:4000/users");
        System.out.println(response);

    }
}

