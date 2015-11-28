/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm.play.android.samples.com.gcmquickstart;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    boolean userR = false;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;

    private WebView myWebView;
    private String LOCAL_FILE = "file:///android_asset/login.html";
    private String LOCAL_FILE1 = "file:///android_asset/menu.html";

    helpBD aBD;
    SQLiteDatabase db=null;

    ProgressDialog ringProgressDialog;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.srlContainer);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setRefreshing(false);

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mInformationTextView.setVisibility(TextView.GONE);

        ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "...", true);
        ringProgressDialog.setCancelable(false);

        try {
            Intent intent = getIntent();
            String value = intent.getStringExtra("alerta");
            //Toast.makeText(this, value, Toast.LENGTH_LONG).show();
        }catch (Exception ex) {
            Toast.makeText(this, "Error al recibir datos", Toast.LENGTH_LONG).show();
        }

        myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebView.setWebViewClient(new WebViewClient());

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    ringProgressDialog.dismiss();
                }
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                boolean pull = sharedPreferences.getBoolean("pull", false);
                String texto = sharedPreferences.getString("token", "");
                boolean userExist = sharedPreferences.getBoolean("user", false);

                if(pull == false){
                    final HttpServerUpdate tarea =  new HttpServerUpdate(null,"", pull,MainActivity.this);
                    tarea.execute(myWebView);
                }

                if (sentToken && userExist) {
                    myWebView.loadUrl(LOCAL_FILE1);

                    Toast.makeText(context, "Soporte técnico.", Toast.LENGTH_LONG).show();
                } else {
                    //Ejemplos();
                    myWebView.loadUrl(LOCAL_FILE);

                }
            }
        };

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < 2; i++) {
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) { bConectado = true; }
        }
        return bConectado;
    }

    @Override
    public void onRefresh() {
        final String webUrl = myWebView.getUrl();
        swipeContainer.setRefreshing(false);


        String datos[] ={"1","","","",""};
        String url="file:///android_asset/menu.html";

        if(webUrl.equals("file:///android_asset/menu.html") || webUrl.equals("file:///android_asset/chat.html") || webUrl.equals("file:///android_asset/settings.html")) {

            swipeContainer.setRefreshing(false);
            dataMessagess();

        }else {
                    switch (webUrl) {
                        case "file:///android_asset/ifilter.html":
                            url="file:///android_asset/ifilter.html";
                            break;
                        case "file:///android_asset/tickets.html":
                            url="file:///android_asset/tickets.html";
                            break;
                        case "file:///android_asset/chat.html":
                            url="file:///android_asset/chat.html";
                            break;
                        default:
                            break;
                    }
                    // Remove widget from screen.

                    SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    boolean pull = sharedPreferences.getBoolean("pull", false);
                    final HttpServerUpdate tarea =  new HttpServerUpdate(datos,url, pull,MainActivity.this);
                    tarea.execute(myWebView);

                    ringProgressDialog.dismiss();

        }




    }


    //Interactua con Javascript
    private class WebAppInterface {
        Context mContext;
        int i=0;

        WebAppInterface(Context c) {
            mContext = c;
        }

        // Pruebas desde Javascript
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
        }

        // Login
        @JavascriptInterface
        public void logIn(String email, String pass) {

            SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String regId = sharedPreferences.getString("token", "");
            String datos[] ={"1","",email,pass,regId};
            final HttpServer tarea =  new HttpServer(datos,MainActivity.this);
            tarea.execute(mInformationTextView);
        }

        // Register
        @JavascriptInterface
        public void registerLogin(String name, String email, String pass) {
            SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String regId = sharedPreferences.getString("token", "");
            String datos[] ={"0",name,email,pass,regId};
            final HttpServer tarea =  new HttpServer(datos,MainActivity.this);
            tarea.execute(mInformationTextView);
        }

        // Obteber datos de Ticket
        @JavascriptInterface
        public  String dataMessage(){

            JSONArray array = new JSONArray();

            try{
                aBD=new helpBD(mContext,"data.db",null,1);
                db = aBD.getReadableDatabase();
                if (db!=null) {
                    Cursor cursor = db.rawQuery("SELECT * FROM ticket ",null);
                    while (cursor.moveToNext()){
                        JSONObject obj = new JSONObject();
                        obj.put("id",cursor.getString(0));
                        array.put(obj);
                    }
                    cursor.close();
                    db.close();
                }
                else
                    Toast.makeText(mContext, "db fue null :-(", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                String cad2 = "ERROR " + e.getMessage();
            }
            return array.toString();
        }

        // Obteber datos de Hosts
        @JavascriptInterface
        public  String dataiFilter(){

            JSONArray array = new JSONArray();
            try{
                aBD=new helpBD(mContext,"data.db",null,1);
                db = aBD.getReadableDatabase();
                if (db!=null) {
                    Cursor cursor = db.rawQuery("SELECT  i.id, i.name, i.company, i.latitud, i.longitud, changeifilters.status FROM ifilters i LEFT JOIN changeifilters " +
                            "ON i.id = changeifilters.ifilter_id AND  changeifilters.id = (SELECT MAX(id) FROM changeifilters WHERE  ifilter_id= i.id ) ;",null);
                    while (cursor.moveToNext()){

                        JSONObject obj = new JSONObject();
                        obj.put("id",cursor.getString(0));
                        obj.put("name",cursor.getString(1));
                        obj.put("company",cursor.getString(2));
                        obj.put("latitud",cursor.getString(3));
                        obj.put("longitud",cursor.getString(4));
                        obj.put("status",cursor.getString(5));
                        array.put(obj);
                    }
                    cursor.close();
                    db.close();
                }
                else
                    Toast.makeText(mContext, "db fue null :-(", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                String cad2 = "ERROR " + e.getMessage();
            }
            return array.toString();
        }

        // Obteber datos de Usuarios
        @JavascriptInterface
        public  String dataUsers(){

            JSONArray array = new JSONArray();
            try{
                aBD=new helpBD(mContext,"data.db",null,1);
                db = aBD.getReadableDatabase();
                if (db!=null) {
                    Cursor cursor = db.rawQuery("SELECT * FROM users",null);
                    while (cursor.moveToNext()){
                        JSONObject obj = new JSONObject();
                        obj.put("id",cursor.getString(0));
                        obj.put("name",cursor.getString(1));
                        obj.put("email",cursor.getString(2));
                        obj.put("position",cursor.getString(3));
                        obj.put("status",cursor.getString(4));
                        array.put(obj);
                    }
                    cursor.close();
                    db.close();
                }
                else
                    Toast.makeText(mContext, "db fue null :-(", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                String cad2 = "ERROR " + e.getMessage();
            }
            return array.toString();
        }



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()){
                        myWebView.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public  String dataMessagess(){

        JSONArray array = new JSONArray();

        try{
            aBD=new helpBD(this,"data.db",null,1);
            db = aBD.getReadableDatabase();
            if (db!=null) {
                Cursor cursor = db.rawQuery("SELECT * FROM vpns",null);
                while (cursor.moveToNext()){
                    JSONObject obj = new JSONObject();
                    obj.put("id",cursor.getString(0));
                    obj.put("ifilter",cursor.getString(1));
                    obj.put("name",cursor.getString(2));

                    array.put(obj);
                }
                cursor.close();
                db.close();
            }
            else
                Toast.makeText(this, "db fue null :-(", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            String cad2 = "ERROR " + e.getMessage();
        }
        Toast.makeText(this, array.toString(), Toast.LENGTH_LONG).show();
        return array.toString();
    }



}
