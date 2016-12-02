//import org.apache.http.*;
//import org.apache.http.HttpEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
//import java.util.UUID;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.Locale;

import jdk.nashorn.internal.parser.JSONParser;
//import org.apache.wink.json4j.*;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
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
import org.joda.time.*;
//import ;

//import org.json.JSONException;
//import org.json.JSONObject;

import org.opendatakit.wink.client.WinkClient;

public class TestPostOpenFn
{
    /*
        GLOBAL VARIABLES

        These will be queried by all methods below.
     */
    private static DateTime lastPullTimestamp = new DateTime(0);
    private static String agg_url = "https://abalobi-monitor.appspot.com/";
    private static String appId = "odktables/default";
    private static String userName = "carl";
    private static String password = "carlcarlcarl";
    private static String version = "2";

    //TO BE REMOVED
    private static String testTableId = "catch_test";


    public static void main(String[] args)
    {
//        testCheckForNewRows();
        JSONArray rowsMonitor = getTableItems("abalobi_monitor");
        JSONArray rowsTrip = getTableItems("abalobi_boat");
        JSONArray rowsCatch = getTableItems("abalobi_catch");
        JSONArray rowsSample = getTableItems("abalobi_sample");

        JSONObject objMonitor = createPayload(rowsMonitor);
        JSONObject objTrip = createPayload(rowsTrip);
        JSONObject objCatch = createPayload(rowsCatch);
        JSONObject objSample = createPayload(rowsSample);

        System.out.println("LATEST DATE: " + lastPullTimestamp);

//        postToOpenFn(objMonitor);

//        prettyPrint(rowsTrip);
        System.out.println("DEBUG DATA: " + prettyPrint(objTrip));

    }
    private static void testCheckForNewRows() {

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

    //Queries the database and fetches new entries
    public static JSONArray getTableItems(String tableID){

        JSONArray returnMe;
        URL url;
        String host;
        String tableSchemaETag = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        Date date = new Date(0);
        String startTime = dateFormat.format(date);

        System.out.println("Date used is: " + date);
        System.out.println("Start time: " + startTime);

        int sizeOfSeqTable = 50;

        try {
            System.out.println("\nSelected Table: " + tableID);
            System.out.println("Initialising WinkClient....");

            /*
                Set up settings using global variables
             */
            url = new URL(agg_url);
            host = url.getHost();
            WinkClient wc = new WinkClient();
            wc.init(host, userName, password);
            tableSchemaETag = wc.getSchemaETagForTable(agg_url, appId, tableID);
            System.out.println("SchemaETag: " + tableSchemaETag);
            System.out.println();

            System.out.println("Querying Aggregate (" + agg_url + ") with user " +  userName + "...");
            JSONObject res = wc.queryRowsInTimeRangeWithLastUpdateDate(agg_url, appId, tableID, tableSchemaETag, startTime, null, null, null);
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

                wc.close();
                return rowsObj;

            }
            else {
                System.out.println("No 'rows' found in result");

                wc.close();
                return new JSONArray();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }









        return new JSONArray();
    }

    /*
        Description: Return an Object of a JSON Array, ready to sent through to OpenFn.
        Note: This method will inject the 'source' and 'data'

     */
    public static JSONObject createPayload(JSONArray printMe){

        JSONObject returnMe = new JSONObject();
        //Set the source
        try{
            returnMe.put("source", printMe.getJSONObject(0).getString("formId"));
        } catch (Exception e){
            e.printStackTrace();
        }

        //If not empty, create the full array
        if (printMe.size() != 0){

            //Create date parser
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS", Locale.ENGLISH);
//            SimpleDateFormat parser = new SimpleDateFormat("2016-09-23'T'11:39:10.774000000");

            //Run through the array
            for (int i = 0; i < printMe.size(); i++){
                try{
                    String dateFromJSON = printMe.getJSONObject(i).getString("savepointTimestamp");
//                    System.out.println(dateFromJSON);
//                    Date compareMe = parser.parse(dateFromJSON);
                    DateTime compareMe = new DateTime( dateFromJSON ) ;
                    System.out.println(compareMe + " PARSED FROM " + dateFromJSON);

                    compareLatestDate(compareMe);

                } catch (Exception e){
                    e.printStackTrace();

                }


                try{
                    returnMe.put("data", (printMe));
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
            return returnMe;
        } else{
            return returnMe;
        }
    }

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


        /* Use these two functions from WinkClient.java:

          public JSONObject queryRowsInTimeRangeWithLastUpdateDate(String uri, String appId, String tableId, String schemaETag,
          String startTime, String endTime, String cursor, String fetchLimit) throws Exception

            public JSONObject queryRowsInTimeRangeWithSavepointTimestamp(String uri, String appId, String tableId, String schemaETag,
          String startTime, String endTime, String cursor, String fetchLimit) throws Exception

        Examples in WinkClientTest.java

        */
    }

    // Utility Methods

    //Return a String of a JSON array
    public static String jsonArrayToString(JSONArray printMe){

        JSONObject returnMe = new JSONObject();
        //Set the source
        try{
            returnMe.put("source", printMe.getJSONObject(0).getString("formId"));
        } catch (Exception e){
            e.printStackTrace();
        }
        if (printMe.size() != 0){
            for (int i = 0; i < printMe.size(); i++){
                try{
                    try{
                        returnMe.put("data", (printMe));

                    } catch (Exception e){
                        e.printStackTrace();
                        return "";
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    return "";
                }

            }
            return returnMe.toString();
        } else{
            return "";
        }
    }

    //Updates the global latest date
    public static void compareLatestDate(DateTime sentDate){

        if (sentDate.compareTo(lastPullTimestamp) > 0){
            lastPullTimestamp = sentDate;
//            System.out.println("REPLACING " + lastPullTimestamp + " WITH " + sentDate);
        }

//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
//        Date date = new Date(0);
//        String startTime = dateFormat.format(date);
    }

    //Print an entire JSON array to console
    public static String prettyPrint(JSONObject printMe){
        String returnMe = null;
        try {
            returnMe = printMe.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnMe;
    }


    
}
