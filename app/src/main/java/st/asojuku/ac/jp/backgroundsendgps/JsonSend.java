package st.asojuku.ac.jp.backgroundsendgps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Itchy on 2017/05/16.
 */
public class JsonSend extends AsyncTask<String,Void,Void> {

    private HttpURLConnection httpsURLConnection;
    private URL url;

    private BufferedReader reader;



    private final String SENDURL = "http://163.44.165.36/php_debug/php_code/jsonReceive.php";


    @Override
    protected Void doInBackground(String... strings) {
        String latitude = strings[0];
        String longitude = strings[1];
        try {

            url = new URL(SENDURL);
            httpsURLConnection = (HttpURLConnection)url.openConnection();
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.setRequestProperty("Accept-Language","jp");
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setRequestProperty("Content-Type","application/json; charset=utf-8");

            JSONObject jsonObject =JsonMake(latitude,longitude);

            OutputStream outputStream = httpsURLConnection.getOutputStream();


            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(String.valueOf(jsonObject));
            writer.flush();
            writer.close();
            outputStream.close();

            int status = httpsURLConnection.getResponseCode();

            switch (status) {
                case HttpURLConnection.HTTP_OK:
                    InputStream is = httpsURLConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is));

                    String httpSource = new String();
                    String str;
                    while (null != (str = reader.readLine())) {
                        Log.v("rec",str);
                    }

                    is.close();
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    return null;
    }

    private JSONObject JsonMake(String latitude,String longitude){
        String gParentID = "2";
        String childID = "6";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("GParentID",gParentID);
            jsonObject.put("childID",childID);
            jsonObject.put("date",dateFormat.format(date).toString());
            jsonObject.put("time",timeFormat.format(date).toString());
            jsonObject.put("latitude",latitude);
            jsonObject.put("longitude",longitude);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("time",timeFormat.format(date).toString());

        return jsonObject;
    }
}
