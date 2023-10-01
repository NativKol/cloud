package server;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Communicator extends Thread{
    static Map<SocketAddress, ClientState> client_state;
    static Map<Socket, byte[]> requests;    //change to byte[]
    Communicator(){
        Map<SocketAddress, ClientState> hm = new HashMap<SocketAddress, ClientState>();
        client_state = hm;

        Map<Socket, byte[]> req = new HashMap<Socket, byte[]>();
        requests = req;
    }

    public String[] extract_params_server(String message){
        String[] lines = message.split("\r\n");
        String[] split_params = lines[lines.length - 1].substring(0, lines[lines.length - 1].length()-1).split("[=,]+");

        String[] params = new String[split_params.length / 2];
        int index = 0;
        for(int i = 1; i < split_params.length; i += 2) {
            params[index] = split_params[i];
            index++;
        }
        return params;
    }

    public static String get_code(String message){
        String[] lines = message.split("\r\n");
        for (String line : lines) {
            if(line.substring(0, 5).equals("code:")) //if header of code
                return line.substring(6, 9);
        }
        return "0";
    }


    //parser client
    public List<String> extract_params_client(String tmp){
        String signBoundary = "------WebKitFormBoundary";
        String signContent = "Content-Disposition:";
        List<String> params = new ArrayList<>();

        params.add(get_code(tmp));

        while (tmp.contains(signContent))
        {
            tmp = tmp.substring(tmp.indexOf(signBoundary), tmp.length());
            tmp = tmp.substring(tmp.indexOf('\n') + 1, tmp.length());
            tmp = tmp.substring(tmp.indexOf('\n') + 1, tmp.length());
            tmp = tmp.substring(tmp.indexOf('\n') + 1, tmp.length());
            params.add(tmp.substring(0, tmp.indexOf('\r')));
            tmp = tmp.substring(tmp.indexOf('\n') + 1, tmp.length());
        }

        System.out.println(Arrays.toString(params.toArray()));
        return params;
    }

    public void run() {
        while (true) {
            while (requests.isEmpty()) {
                try{Thread.sleep(5000);}catch(InterruptedException e){System.out.println(e + " exception is here");}
                continue;
            }

            //check if the client is new

            //System.out.println("yo");
            ClientState new_con = new ClientState(); //if client is new his new state will be assigned
            Map.Entry<Socket, byte[]> entry = requests.entrySet().iterator().next();
            Socket client_soc = entry.getKey();

            System.out.println("socket: " + client_soc);
            if (!client_state.containsKey(client_soc.getLocalSocketAddress())) //add a change of a client's state.
                client_state.putIfAbsent(entry.getKey().getLocalSocketAddress(), new_con); //adding new client to the list
            else
                new_con = client_state.get(client_soc.getLocalSocketAddress()); //get the state of user


            String data = new String( entry.getValue(), StandardCharsets.UTF_8); // for UTF-8 encoding
            System.out.println(data);

            //separates the boundary and gets params
            String signBoundary = "------WebKitFormBoundary";

            String tmp = data;
            List<String> params = new ArrayList<>();

            if (tmp.contains(signBoundary))
            {
                //parser client
                params = extract_params_client(tmp);
            }
            else
            {
                //parser server
                params.add(get_code(tmp));
            }

            RequestHandler handle = new RequestHandler(new_con, Integer.parseInt(params.get(0)) , params.subList(1, params.size()));

            String returned_params = "";
            String res = "";



            UserModel usr = client_state.get(client_soc.getLocalSocketAddress()).getUserState();

            try {
                if (Integer.parseInt(params.get(0)) == 301) //upload
                {
                    returned_params = handle.handleUploadRequest(entry.getValue());
                }
                else
                {
                    returned_params = handle.handleRequest(new_con);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (returned_params == "")
            {
                System.out.println("No params returned");
                res = "HTTP/1.1 200 OK\r\n";
            }
            else if (returned_params == "ERROR")
            {
                res = "HTTP/1.1 400 ERROR\r\n";
            }
            else
            {
                res = "HTTP/1.1 200 OK\r\n" + "Content-Type: application/json\r\n\n"
                        + returned_params;
            }



            SocketAddress adr = client_soc.getLocalSocketAddress();
            client_state.put(adr, new_con);
            if (Integer.parseInt(params.get(0)) == 107)
            {
                //sign out
                client_state.remove(adr);
            }

            System.out.println("the amount of client rn is: " + client_state.size());


            try{
                send_res(client_soc, res);
            } catch (IOException ex) {
                System.err.println(ex + "1");}

            requests.remove(entry.getKey());
        }
    }
    public static void send_res(Socket clientSocket, String response) throws IOException
    {
        System.out.println("response: " + response);
        OutputStream clientOutput = clientSocket.getOutputStream();
        clientOutput.write(response.getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.flush();
        clientOutput.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        (new Thread(new Communicator())).start();
        //server.LoginState st = new State();
        int port = 8081;
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Server is running on port: "+port);
        while(true) {
            Socket clientSocket = serverSocket.accept();
            System.err.println("Client connected");

            TimeUnit.SECONDS.sleep(1);

            byte[] resultBuff = new byte[0];

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for(int s = 0; (s=clientSocket.getInputStream().read(buffer)) > 0;)
            {
                System.out.println(s);
                baos.write(buffer, 0, s);
                //System.out.println(new String(baos.toByteArray()));
                if(s < 1024)
                    break;
            }

            resultBuff = baos.toByteArray();

            //requests.put(clientSocket ,resultBuff);       //swap

            System.out.println(resultBuff.length + " bytes read.");

            String par = new String(resultBuff);
            //System.out.println(par);

            requests.put(clientSocket ,resultBuff);

        }
    }

}

