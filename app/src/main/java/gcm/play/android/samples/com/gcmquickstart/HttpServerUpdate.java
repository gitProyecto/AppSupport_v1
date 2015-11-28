package gcm.play.android.samples.com.gcmquickstart;

/**
 * Created by Luis Fm on 25/11/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//


public class HttpServerUpdate extends AsyncTask<WebView, Void, String>  {

    WebView t;
    String result = "fail";
    String []datos;
    Context context;
    ProgressDialog ringProgressDialog;
    String url;
    boolean action;

    public HttpServerUpdate(String[] datos, String url,boolean action, Context contetxt){
        this.context=contetxt;
        this.datos=datos;
        this.action=action;

        this.url=url;
        ringProgressDialog = ProgressDialog.show(context, "Please wait ...", "Loading ...", true);
        ringProgressDialog.setCancelable(false);

    }

    @Override
    protected String doInBackground(WebView... params) {
        // TODO Auto-generated method stub
        this.t = params[0];
        return GetSomething();
    }

    final String GetSomething()
    {
        String url = "http://development.techno-world.net/appSupport/synchronization.php";
        BufferedReader inStream = null;
        try {


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpRequest = new HttpPost(url);


            if(datos != null) {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("action", datos[0]));
                params.add(new BasicNameValuePair("idUser", datos[1]));
                params.add(new BasicNameValuePair("descrip", datos[2]));
                httpRequest.setEntity(new UrlEncodedFormEntity(params));

            }

            HttpResponse response = httpClient.execute(httpRequest);
            inStream = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent()));

            StringBuffer buffer = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = inStream.readLine()) != null) {
                buffer.append(line + NL);
            }
            inStream.close();

            result = buffer.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    protected void onPostExecute(String page)
    {
        if(action) {
            updateData(page.trim());
        }else{
            updateDataPull(page.trim());
        }

        ringProgressDialog.dismiss();
    }



    public void updateData(String data){

        helpBD aBD;
        SQLiteDatabase db=null;

        t.loadUrl(url);

        Toast.makeText(context,"PULL",Toast.LENGTH_LONG).show();



    }


        public void updateDataPull(String data){

        helpBD aBD;
        SQLiteDatabase db=null;



        try {

            aBD=new helpBD(context,"data.db",null,1);
            db = aBD.getWritableDatabase();

            if (db!=null) {

                    JSONObject json = new JSONObject(data);


//Message
                    JSONObject jsonM = json.getJSONObject("Message");

                    String uuid = jsonM.getString("uuid");
                    String command = jsonM.getString("command");
                    String fom = jsonM.getString("from");
                    String to = jsonM.getString("to");
                    String priority = jsonM.getString("priority");
                    String bytes = jsonM.getString("bytes_size");
                    String check = jsonM.getString("checksum");
                    String date = jsonM.getString("date");
                    String status = jsonM.getString("status");

                    db.execSQL("INSERT INTO messages(id, uuid, command, fromm, too, priority, size, checksum, date, status) VALUES(null,'" + uuid + "','" + command + "','" + fom + "','" + to + "'," + priority + "," + bytes + ","+check+","+date+","+status+");");

                    JSONObject jsonP = jsonM.getJSONObject("payload");
                    String include = jsonP.getString("iFilters_included");


//iFilter
                    JSONArray jsoniF = jsonP.getJSONArray("iFilters");

                        for (int i = 0; i < jsoniF.length(); i++) {
                            JSONObject jsoni = jsoniF.getJSONObject(i);

                            String namei = jsoni.getString("name");
                            String companyi = jsoni.getString("company");
                            String statusi = jsoni.getString("status");
                            String lastchangei = jsoni.getString("lastStateChange");
                            JSONArray jsonlo = jsoni.getJSONArray("location");
                            String x = jsonlo.getString(0);
                            String y = jsonlo.getString(1);

                            db.execSQL("INSERT INTO ifilters(id, name, company, latitud, longitud) VALUES(null,'" + namei + "','" + companyi + "','" + x + "','" + y + "');");
                            db = aBD.getReadableDatabase();
                            Cursor cursor = db.rawQuery("SELECT id FROM ifilters WHERE name='" + namei + "'", null);
                            cursor.moveToNext();
                            String ifilter_id= cursor.getString(0);
                            db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null," + ifilter_id + "," + statusi + "," + lastchangei + ");");


//Ups
                            JSONObject jsonu = jsoni.getJSONObject("ups");
                            String upsu = jsonu.getString("ups_id");
                            String nameu = jsonu.getString("upsname");
                            String brandu = jsonu.getString("brand");
                            String statusu = jsonu.getString("status");
                            String batteryC = jsonu.getString("batteryCharge");
                            String batteryV = jsonu.getString("batteryVoltage");
                            String lineV = jsonu.getString("lineVoltage");
                            String load = jsonu.getString("load");
                            String lastchangeu = jsonu.getString("lastStateChange");
                            String timecheck = jsonu.getString("time_checked");

                            db.execSQL("INSERT INTO ups(id, ifilter_id, ups_id, name, brand) VALUES(null," + ifilter_id + ",'" + upsu + "','" + nameu + "','"+brandu+"');");
                            cursor = db.rawQuery("SELECT id FROM ups WHERE ups_id='" + upsu + "'", null);
                            cursor.moveToNext();
                            String ups_id= cursor.getString(0);
                            db.execSQL("INSERT INTO changeups VALUES(null," + ups_id + "," + statusu + "," + batteryC + "," + batteryV + "," + lineV + "," + lastchangeu + "," + timecheck + ");");


//Internet
                            JSONObject jsonl = jsoni.getJSONObject("internetLink");
                            String namel = jsonl.getString("prov_name");
                            String statusl = jsonl.getString("status");
                            String ipaddrl = jsonl.getString("ipAddr");
                            String lastchangel = jsonl.getString("lastStateChange");

                            db.execSQL("INSERT INTO inter VALUES(null," + ifilter_id + ",'" + namel + "','" + ipaddrl + "');");
                            cursor = db.rawQuery("SELECT id FROM inter WHERE ifilter_id='" + ifilter_id + "'", null);
                            cursor.moveToNext();
                            String inter_id= cursor.getString(0);
                            db.execSQL("INSERT INTO changeinter (id, inter_id, status, lastchange) VALUES(null," + inter_id + "," + status + "," + lastchangel + ");");


//Switch
                            JSONObject jsons = jsoni.getJSONObject("switchs");
                            String names = jsons.getString("name");
                            String ports = jsons.getString("ports");
                            String lastchanges = jsons.getString("lastStateChange");

                            db.execSQL("INSERT INTO switch VALUES(null," + ifilter_id + ",'" + names + "'," + ports + ");");
                            cursor = db.rawQuery("SELECT id FROM switch WHERE ifilter_id='" + ifilter_id + "'", null);
                            cursor.moveToNext();
                            String switch_id= cursor.getString(0);
                            db.execSQL("INSERT INTO changeswitch VALUES(null," + switch_id + ",1," + lastchanges + ");");


//Server
                            JSONObject jsonw = jsoni.getJSONObject("servers");
                            String namew = jsonw.getString("name");
                            String statusw = jsonw.getString("status");
                            String loadw = jsonw.getString("load");
                            String lastchangew = jsonw.getString("lastStateChange");

                            db.execSQL("INSERT INTO servers VALUES(null," + ifilter_id + ",'" + namew + "');");
                            cursor = db.rawQuery("SELECT id FROM servers WHERE name='" + namew + "'", null);
                            cursor.moveToNext();
                            String server_id= cursor.getString(0);
                            db.execSQL("INSERT INTO changeservers VALUES(null," + server_id + ","+statusw+","+loadw+"," + lastchangew + ");");


//VPN
                            JSONObject jsonv = jsoni.getJSONObject("vpn");
                            String namev = jsonv.getString("name");
                            String statusv = jsonv.getString("status");
                            String lastchangev = jsonv.getString("lastStateChange");

                            String tableVpns = "CREATE TABLE vpns(id INTEGER PRIMARY KEY AUTOINCREMENT, ifilter_id INTEGER, name TEXT);";
                            String tablechangeVpns = "CREATE TABLE changevpns(id INTEGER PRIMARY KEY AUTOINCREMENT, vpn_id INTEGER, status INTEGER, lastchange INTEGER);";


                            db.execSQL("INSERT INTO vpns VALUES(null," + ifilter_id + ",'" + namev + "');");
                            cursor = db.rawQuery("SELECT id FROM vpns WHERE name='" + namev + "'", null);
                            cursor.moveToNext();
                            String vpn_id= cursor.getString(0);
                            db.execSQL("INSERT INTO changevpns VALUES(null," + vpn_id + "," + statusv + "," + lastchangew + ");");


                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            sharedPreferences.edit().putBoolean("pull", true).apply();


                        }


                db.close();


            Toast.makeText(context,"pull Server : ok",Toast.LENGTH_LONG).show();



            }else
                        Toast.makeText(context, "db fue null :-(", Toast.LENGTH_LONG).show();

        }catch (Exception ex){
        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }








    }

















}