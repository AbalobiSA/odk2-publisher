//import org.apache.http.*;
//import org.apache.http.HttpEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
//import java.util.UUID;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.URL;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;


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

//import org.json.JSONException;
//import org.json.JSONObject;

import org.opendatakit.wink.client.WinkClient;

public class TestPostOpenFn
{
    public static String lastPullTimestamp;
/*
    private static void postToOpenFn(JSONObject obj)
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {            
            System.out.println("Trying to POST to OpenFn...");
            
            //HttpPost httpPost = new HttpPost("http://requestb.in/1mo2tk51");
            HttpPost httpPost = new HttpPost("https://www.openfn.org/inbox/3afab0f1-3937-4ca8-95a3-5491f6f32a4e");
           
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");



            httpPost.setEntity(new StringEntity(obj.toString(), "UTF-8"));

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
    }
    
    */
    
    /* Use these two functions from WinkClient.java:  
    
      public JSONObject queryRowsInTimeRangeWithLastUpdateDate(String uri, String appId, String tableId, String schemaETag,
      String startTime, String endTime, String cursor, String fetchLimit) throws Exception 
      
        public JSONObject queryRowsInTimeRangeWithSavepointTimestamp(String uri, String appId, String tableId, String schemaETag,
      String startTime, String endTime, String cursor, String fetchLimit) throws Exception
    
    Examples in WinkClientTest.java
    
    */



    public static void main(String[] args)
    {    
        testCheckForNewRows();
    
    /*  JSONObject obj = new JSONObject();  
        obj.put("source", "ODK2 Test Publisher");
        obj.put("data", "Add some data here...");
        postToOpenFn(obj); 
    */
    }
    private static void testCheckForNewRows() {

        String agg_url = "https://abalobi2-0.appspot.com/";
        String appId = "odktables/default";
        String testTableId = "catch_test";
        String userName = "publisher";
        String password = "tmfUT5FCNdA43pM8dR3y";
        String version = "2";
        int batchSize = 1000;

        URL url;
        String host;


        //String colName = "seq_num";
        //String colKey = "seq_num";
        //String colType = "string";

        //String testTableSchemaETag = "createRowsForQueryTest";
        String tableSchemaETag = null;
        //String listOfChildElements = "[]";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        Date date = new Date(0);
        String startTime = dateFormat.format(date);

        System.out.println("Date used is: " + date);
        System.out.println("Start time: " + startTime);

        int sizeOfSeqTable = 50;

        try {
            System.out.println("Initialising WinkClient...");

            url = new URL(agg_url);
            host = url.getHost();

            WinkClient wc = new WinkClient();
            wc.init(host, userName, password);
            tableSchemaETag = wc.getSchemaETagForTable(agg_url, appId, testTableId);
            System.out.println("SchemaETag: " + tableSchemaETag);
            System.out.println();


          /*  Adds new rows (from WinkClient test code)
          ArrayList<Column> columns = new ArrayList<Column>();

          columns.add(new Column(colKey, colName, colType, listOfChildElements));

          JSONObject result = wc.createTable(agg_url, appId, testTableId, testTableSchemaETag, columns);

          if (result.containsKey("tableId")) {
            tableSchemaETag = result.getString("schemaETag");
          }

          ArrayList<Row> rowList = new ArrayList<Row>();
          for (int i = 0; i < sizeOfSeqTable; i++) {
            DataKeyValue dkv = new DataKeyValue(colName, Integer.toString(i));
            ArrayList<DataKeyValue> dkvl = new ArrayList<DataKeyValue>();
            dkvl.add(dkv);
            String RowId = "uuid:" + UUID.randomUUID().toString();
            Row row = Row.forInsert(RowId, null, null, null, null, null, null, dkvl);
            rowList.add(row);
          }

          wc.createRowsUsingBulkUpload(agg_url, appId, testTableId, tableSchemaETag, rowList, 0);
          */

            System.out.println("Querying Aggregate (" + agg_url + ") with user " +  userName + "...");
            JSONObject res = wc.queryRowsInTimeRangeWithLastUpdateDate(agg_url, appId, testTableId, tableSchemaETag, startTime, null, null, null);
            System.out.println("Done querying, checking result");

            if (res.containsKey("rows")) {
                JSONArray rowsObj = res.getJSONArray("rows");
                //assertEquals(rowsObj.size(), sizeOfSeqTable);
                System.out.println("Found " + rowsObj.size() + " new rows.");
                if (rowsObj.size() > 0)
                {
                    //Run through all entries, and get the last
                    System.out.println("JSON[0]:");
                    System.out.println(rowsObj.getJSONObject(0).toString());
                }

            }
            else {
                System.out.println("No 'rows' found in result");
            }

            //wc.deleteTableDefinition(agg_url, appId, testTableId, tableSchemaETag);

            wc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getTableItems(String tableID){
        return new JSONArray();
    }
    
    
}
