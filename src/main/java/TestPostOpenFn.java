//import org.apache.http.*;
//import org.apache.http.HttpEntity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import org.json.JSONException;
import org.json.JSONObject;

public class TestPostOpenFn
{
    public static void main(String[] args)
    {
    
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            /*HttpGet httpGet = new HttpGet("http://httpbin.org/get");
            CloseableHttpResponse response1 = null;
            try {
                response1 = httpclient.execute(httpGet);
            } catch(java.io.IOException e){
                e.printStackTrace();
                return;
            }
              */  
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            /*
            try {
                System.out.println(response1.getStatusLine());
                HttpEntity entity1 = response1.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity1);
            } 
            catch(java.io.IOException e){
                   e.printStackTrace();
                   return;
            } finally {
               try {
                   response1.close();
               } catch(java.io.IOException e){
                   e.printStackTrace();
                   return;
               }
            }*/

            System.out.println("Trying to POST to OpenFn...");
            
            //HttpPost httpPost = new HttpPost("http://requestb.in/1mo2tk51");
            HttpPost httpPost = new HttpPost("https://www.openfn.org/inbox/3afab0f1-3937-4ca8-95a3-5491f6f32a4e");
           
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            JSONObject obj = new JSONObject();  
            //obj.put("userID", "user5");
            //obj.put("FirstName", "test");
            //obj.put("LastName", "2");
            obj.put("source", "ODK2 Test Publisher");
            obj.put("data", "Add some data here...");

            httpPost.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            
            /*List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("Content-Type", "application/json"));
            nvps.add(new BasicNameValuePair("username", "vip"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));*/
            
            CloseableHttpResponse response2 = httpclient.execute(httpPost);

            try {
                System.out.println(response2.getStatusLine());
                HttpEntity entity2 = response2.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2);
            } finally {
                response2.close();
            }
            
            System.out.println("Done.");
            
        } catch(java.io.IOException e){
           e.printStackTrace();
           return;
        } finally {
            try {
                httpclient.close();
            } catch(java.io.IOException e){
               e.printStackTrace();
               return;
            }
        }
        
        /*
        System.out.println("Trying to POST to OpenFn...");
        
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://www.openfn.org/inbox/3afab0f1-3937-4ca8-95a3-5491f6f32a4e");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("param-1", "12345"));
        params.add(new BasicNameValuePair("param-2", "Hello!"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                // do something useful
            } finally {
                instream.close();
            }
        }

        System.out.println("Done.");        */
    }    
}
