package st.asojuku.ac.jp.backgroundsendgps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by Itchy on 2017/05/17.
 */
public class FirstConnect extends AsyncTask<Void,Void,Void>{

    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase sqLiteDatabase;

    private boolean firstFlg;

    private final String FIRSTURL = "http://163.44.165.36/php_debug/php_code/ParentFirstConnect.php";
    //"http://" + R.string.server_ip + "/php_debug/php_code/ParentFirstConnect.php"
    private HttpURLConnection httpsURLConnection;
    private URL url;
    private String gParentID;

    public FirstConnect(Context c){
        mySQLiteOpenHelper = new MySQLiteOpenHelper(c);
        sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();


        firstFlg = firstConnect();


    }

    public boolean isFirstFlg() {
        return firstFlg;
    }

    public String getgParentID() {
        return gParentID;
    }

    private boolean firstConnect(){
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM localtbl WHERE localname = ?;",new String[]{"gParentID"});
        cursor.moveToNext();
        int count = cursor.getCount();
        System.out.println(count);
        if(count == 0){
            return true;
        }else {

            this.gParentID = cursor.getString(1);
            Log.v("FIRST CONNECT",this.gParentID);
            return false;
        }
    }

    private void insertGParentID(String gParentID){
        ContentValues contentValues = new ContentValues();
        contentValues.put("localname","gParentID");
        contentValues.put("localmember",gParentID);
        sqLiteDatabase.insert("localtbl",null,contentValues);

        this.gParentID = gParentID;
        Log.v("FIRST CONNECT",gParentID);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(!firstFlg) return null;

        try {

            url = new URL(FIRSTURL);
            httpsURLConnection = (HttpURLConnection)url.openConnection();
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.setRequestProperty("Accept-Language","jp");
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setRequestProperty("Content-Type","application/json; charset=utf-8");

            httpsURLConnection.connect();

            int status = httpsURLConnection.getResponseCode();

            switch (status) {
                case HttpURLConnection.HTTP_OK:
                    InputStream is = httpsURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String httpSource = new String();
                    String str;
                    while (null != (str = reader.readLine())) {
                        Log.v("rec",str);
                        //ローカルDB挿入
                        insertGParentID(str);

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
}
