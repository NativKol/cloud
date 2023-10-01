import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.*;
import static java.lang.Thread.sleep;

public class Client {
    public static void send_request() throws Exception {

        HttpPost httppost = new HttpPost("http://localhost:8081/");
// Request parameters and other properties.
        ArrayList<NameValuePair> postParameters;


        postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("param1", "param1_value"));
        postParameters.add(new BasicNameValuePair("param2", "param2_value"));

        String params = postParameters.toString();

        System.out.println(params.getBytes());

        String par = new String(params.getBytes());

        System.out.println(par);

        httppost.setEntity(new ByteArrayEntity(params.getBytes()));

        System.out.println(httppost.getEntity());
        System.out.println(EntityUtils.toString(httppost.getEntity(), "UTF-8"));



        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            HttpResponse response = httpclient.execute(httppost);   //send
            HttpEntity entity = response.getEntity();
            System.out.println(response);
        }
        sleep(500);


    }
    public static void main(String[] args) throws Exception {
        send_request();
    }
}
