import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.Locale;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.joda.time.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//Writing files
import java.io.IOException;

//import org.opendatakit.wink.client.WinkClient;
import org.opendatakit.sync.client.SyncClient;
public class TestPostOpenFn
{
    /*
    * GLOBAL VARIABLES
    *
    * These are used by most of the methods below;
    * Please change with care.
    *
    * */
    private static DateTime lastPullTimestamp = new DateTime(0);
    private static DateTime newTimeStamp;
    private static String agg_url = "https://abalobi-monitor.appspot.com/";
    private static String appId = "odktables/default";
    private static String userName = "carl";
    private static String password = "carlcarlcarl";
    private static String version = "2";
//    private static String POST_URL = "http://197.85.186.65:8080";
    private static String POST_URL = "https://www.openfn.org/inbox/3afab0f1-3937-4ca8-95a3-5491f6f32a4e";

    //Switches for file writing
    private static String TIMESTAMP_FILE = "odk2timestamp.txt";
    private static boolean append_to_file = false;

    //TO BE REMOVED
    private static String testTableId = "catch_test";


    public static void main(String[] args)
    {
        //TODO: Modify this to read in from file
        lastPullTimestamp = readTimeStampFromFile(TIMESTAMP_FILE);
//        lastPullTimestamp = DateTime.now();
//        lastPullTimestamp = new DateTime( /*System.currentTimeMillis()*/0 ); //At the moment, this will run at system time

        //This is what will eventually be stored as the last query date.
        //newTimeStamp gets set as the last occurring date in any of the records.
        newTimeStamp = lastPullTimestamp;

        //After reading from file, this global will be used to query all the records.
        System.out.println("INITIAL DATE USED FROM ARGS: " + lastPullTimestamp);

        //Query the tables based on global arguments, and store each table full of new rows, in a JSON array.
        JSONArray rowsMonitor = getTableItems("abalobi_monitor");
        JSONArray rowsTrip = getTableItems("abalobi_boat");
        JSONArray rowsCatch = getTableItems("abalobi_catch");
        JSONArray rowsSample = getTableItems("abalobi_sample");

        //Create a JSON object with an array containing all new records
        //Note: Each of these will try to compete for the latest date.
        JSONObject objMonitor = createPayload(rowsMonitor);
        JSONObject objTrip = createPayload(rowsTrip);
        JSONObject objCatch = createPayload(rowsCatch);
        JSONObject objSample = createPayload(rowsSample);

        //Modify this to write to file
        System.out.println("LATEST DATE: " + newTimeStamp);
        writeTimeStampToFile(newTimeStamp.toString());

        //Finally, send all these JSON records to OpenFunction.
        try {
            realPostToOpenFn(objMonitor);
            realPostToOpenFn(objTrip);
            realPostToOpenFn(objCatch);
            realPostToOpenFn(objSample);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            SyncClient wc = new SyncClient();
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

        //This should be the last
        String startTime = lastPullTimestamp.toString();

        System.out.println("Date used is: " + lastPullTimestamp);
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
            SyncClient wc = new SyncClient();
            wc.init(host, userName, password);
            tableSchemaETag = wc.getSchemaETagForTable(agg_url, appId, tableID);
//            wc.getTableDataETag();
            System.out.println("SchemaETag: " + tableSchemaETag);
            System.out.println();

            System.out.println("Querying Aggregate (" + agg_url + ") with user " +  userName + "...");
            JSONObject res = wc.queryRowsInTimeRangeWithLastUpdateDate(agg_url, appId, tableID, tableSchemaETag, startTime, null, null, null);
            JSONObject test2 = wc.getAllDataChangesSince(agg_url, appId, tableID, tableSchemaETag, null, null, null);

//            System.out.println(test2.getString("dataEtag"));
            String dETag = test2.getString("dataETag");
            System.out.println("Data E TAG: " + dETag);

            /*getAllDataChangesSince(String uri, String appId,
 +      String tableId, String schemaETag, String dataETag, String cursor,
 +      String fetchLimit)*/
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

        //Start building a JSON object in order to return later.
        JSONObject returnMe = new JSONObject();

        //Set the first key, 'source', as the table name from the JSON.
        //Note: If this is empty, there will be no new rows. This is handled in the post method.
        try{
            returnMe.put("source", printMe.getJSONObject(0).getString("formId"));
            returnMe.put("filter", "odk_2_publisher_heroku");
        } catch (Exception e){
//            e.printStackTrace();
            System.out.println("Unable to create payload, no new rows found!");
        }

        //If not empty, create the full array
        if (printMe.size() != 0){

            //Create date parser
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS", Locale.ENGLISH);

            //Run through the array
            for (int i = 0; i < printMe.size(); i++){
                try{
                    //Get the date entry of the current object in the row, as a string.
                    String dateFromJSON = printMe.getJSONObject(i).getString("savepointTimestamp");

                    //Convert the date string to a DateTime object from the JodaTime library.
                    DateTime compareMe = new DateTime( dateFromJSON ) ;

                    //Set the date as the latest date, if applicable.
                    compareLatestDate(compareMe);

                } catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    //Add the current data entry as an object in the 'data' array, in the JSON
                    returnMe.put("data", (printMe));
                } catch (Exception e){
                    System.out.println("Unable to create payload, no new rows found! 2");
                }

            }
            return returnMe;
        } else{
            //Return an empty object, to be handled in another method.
            return returnMe;
        }
    }

    private static void postToOpenFn(JSONObject obj)
    {
        try {
            // configure the SSLContext with a TrustManager
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


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

    private static void realPostToOpenFn(JSONObject obj) throws Exception{
//        String html = new ApacheRequest("https://www.openfn.org/inbox/3afab0f1-3937-4ca8-95a3-5491f6f32a4e")
////                .uri().path("/users").queryParam("id", 333).back()
//                .method(Request.POST)
//                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
//                .fetch()
//                .as(RestResponse.class)
////                .assertStatus(HttpURLConnection.HTTP_OK)
//                .body()
//                .header().back();
        SSLUtilities.trustAllHostnames();
        SSLUtilities.trustAllHttpsCertificates();

        if (obj.size() > 0){
            System.out.println("Creating post request...");

            String httpsURL = POST_URL;

            URL myurl = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
            con.setRequestMethod("POST");
//        con.setRequestProperty("data", "test");

//        con.setRequestProperty("Content-length", String.valueOf(query.length()));
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
            con.setDoOutput(true);
            con.setDoInput(true);

            DataOutputStream output = new DataOutputStream(con.getOutputStream());

//        StringEntity params =new StringEntity(obj.toString());
            output.writeBytes(obj.toString());


//        output.writeBytes(query);

            output.close();

            DataInputStream input = new DataInputStream( con.getInputStream() );



            for( int c = input.read(); c != -1; c = input.read() )
                System.out.print( (char)c );
            input.close();

            System.out.println("Resp Code:"+con .getResponseCode());
            System.out.println("Resp Message:"+ con .getResponseMessage());
        } else{
            System.out.println("No data in current JSON, skipping post...");
        }

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

        if (sentDate.compareTo(newTimeStamp) > 0){
            newTimeStamp = sentDate;
//            System.out.println("REPLACING " + lastPullTimestamp + " WITH " + sentDate);
        }

//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
//        Date date = new Date(0);
//        String startTime = dateFormat.format(date);
    }

    //Reads the latest timestamp from a saved file
    public static DateTime readTimeStampFromFile(String filepath){
        try {
            FileReader fr = new FileReader(filepath);
            BufferedReader textReader = new BufferedReader(fr);

            String readDate = textReader.readLine();
            System.out.println("SUCCESSFULLY READ FROM FILE! " );
            return new DateTime( readDate ) ;

        } catch (Exception e) {

            //If something goes wrong, chances are the file doesn't exist.

            //Create a file with the timestamp of 0
            try {
                FileWriter write = new FileWriter ( filepath, append_to_file );
                PrintWriter print_line = new PrintWriter(write);
                print_line.println(new DateTime( 0 ));
                print_line.close();
                System.out.println("FILE CREATED!");

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            //Print errors.
//            e.printStackTrace();

            //Finally, set the timestamp as 0
            System.out.println("ERRORS OCCURRED: Setting timestamp as ZERO!");
            return new DateTime( 0 ) ;
        }

    }

    public static void writeTimeStampToFile(String timestamp){
        try {
            FileWriter write = new FileWriter ( TIMESTAMP_FILE, append_to_file );
            PrintWriter print_line = new PrintWriter(write);
            print_line.println(timestamp);
            print_line.close();
            System.out.println("SUCCESSFULLY WROTE TIME TO FILE!");

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    //Print an entire JSON object to console
    public static String prettyPrint(JSONObject printMe){
        String returnMe = null;
        try {
            returnMe = printMe.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnMe;
    }

    private static class DefaultTrustManager implements X509TrustManager {

//        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

//        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

//        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }



}
