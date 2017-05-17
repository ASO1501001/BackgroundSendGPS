package st.asojuku.ac.jp.backgroundsendgps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  implements LocationListener{


    private final int REQUEST_PERMISSION = 1000;


    private LocationManager locationManager;


    private static final int MinTime = 5000;
    private static final float MinDistance = 5;

    private Button frontBtn,backBtn,clearBtn;
    private TextView textView;
    private String text;

    private Intent backService;

    private boolean startFlg,backFlg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirstConnect firstConnect = new FirstConnect(this);

        if(firstConnect.isFirstFlg()){
            firstConnect.execute();
        }




        if(Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        frontBtn = (Button)findViewById(R.id.front);
        backBtn = (Button)findViewById(R.id.back);
        clearBtn = (Button)findViewById(R.id.clear);
        textView = (TextView)findViewById(R.id.text_view);

        backService = new Intent(getApplication(), BackgroundGPS.class);

        text = "";

        startFlg =true;
        frontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //前面処理開始
                if(startFlg){
                    startGPS();
                    text += "start \n";
                    textView.setText(text);
                }else{
                    stopGPS();
                    text += "stop \n";
                    textView.setText(text);
                }
                startFlg = !startFlg;
            }
        });

        backFlg = true;
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //裏面処理開始

                if(backFlg){
                    startService(backService);
                    text += "service start \n";
                    textView.setText(text);
                }else{
                    stopService(backService);
                    text += "service stop \n";
                    textView.setText(text);
                }
                backFlg = !backFlg;
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = "";
                textView.setText(text);
            }
        });




    }

    private void startGPS(){
        Log.v("GPS","startGPS START!");

        final boolean gpsEnabled
                = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // GPSを設定するように促す
            enableLocationSettings();
        }

        if (locationManager != null) {
            Log.d("LocationActivity", "locationManager.requestLocationUpdates");
            // バックグラウンドから戻ってしまうと例外が発生する場合がある
            try {
                // minTime = 1000msec, minDistance = 50m
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)!=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)!=
                                PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MinTime, MinDistance, this);
            } catch (Exception e) {
                e.printStackTrace();

                Toast toast = Toast.makeText(this,
                        "例外が発生、位置情報のPermissionを許可していますか？", Toast.LENGTH_SHORT);
                toast.show();

                //MainActivityに戻す
                finish();
            }
        } else {
            text += "locationManager=null\n";
            textView.setText(text);
        }
        Log.v("GPS","startGPS END!");

        super.onResume();
    }

    private void stopGPS(){

        Log.v("GPS","stopGPS START!");
        if (locationManager != null) {
            Log.d("LocationActivity", "onStop()");
            // update を止める
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        } else {
            text += "onStop()\n";
            textView.setText(text);
        }

        Log.v("GPS","stopGPS END!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopGPS();
    }

    @Override
    protected void onPause() {

        if (locationManager != null) {
            Log.d("LocationActivity", "locationManager.removeUpdates");
            // update を止める
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(this);
        } else {
            text += "onPause()\n";
            textView.setText(text);
        }

        super.onPause();

    }

    public void checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            Log.v("permission","OK!");
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this,
                    "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("permission","change OK!");
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }



    @Override
    public void onLocationChanged(Location location) {

        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        text += "Latitude=" + latitude + "\n";
        text += "Longitude=" + longitude + "\n";
        text += "time" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "\n";

        textView.setText(text);

        new JsonSend().execute(latitude,longitude);
    }

    @Override
    public void onStatusChanged(String s, int status, Bundle bundle) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                text += "LocationProvider.AVAILABLE\n";
                textView.setText(text);

                break;
            case LocationProvider.OUT_OF_SERVICE:
                text += "LocationProvider.OUT_OF_SERVICE\n";
                textView.setText(text);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                text += "LocationProvider.TEMPORARILY_UNAVAILABLE\n";
                textView.setText(text);
                break;
        }
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
