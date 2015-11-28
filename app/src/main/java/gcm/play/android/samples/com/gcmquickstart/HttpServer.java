package gcm.play.android.samples.com.gcmquickstart;

/**
 * Created by Luis Fm on 25/11/2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

//




public class HttpServer extends AsyncTask<TextView, Void, String>  {

    TextView t;
    String result = "fail";
    String []datos;
    Context context;
    ProgressDialog ringProgressDialog;

    public HttpServer(String []datos, Context contetxt){
        this.context=contetxt;
        this.datos=datos;

        ringProgressDialog = ProgressDialog.show(context, "Please wait ...", "Loading ...", true);
        ringProgressDialog.setCancelable(false);

    }

    @Override
    protected String doInBackground(TextView... params) {
        // TODO Auto-generated method stub
        this.t = params[0];
        return GetSomething();
    }

    final String GetSomething()
    {
        String url = "http://development.techno-world.net/appSupport/reg_devices.php";
        BufferedReader inStream = null;
        try {


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpRequest = new HttpPost(url);


            if(datos != null) {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("action", datos[0]));
                params.add(new BasicNameValuePair("name", datos[1]));
                params.add(new BasicNameValuePair("email", datos[2]));
                params.add(new BasicNameValuePair("pass", datos[3]));
                params.add(new BasicNameValuePair("regId", datos[4]));
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
        if(page.trim().equals("Ok")){

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit().putBoolean("user", true).apply();
            sharedPreferences.edit().putString("email", datos[2]).apply();

            ringProgressDialog.dismiss();
            reiniciarActivity((Activity) context);
        }else{
            Toast.makeText(context,page.trim(),Toast.LENGTH_LONG).show();
            ringProgressDialog.dismiss();
        }







    }

    public static void reiniciarActivity(Activity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());
        actividad.startActivity(intent);
        actividad.finish();
    }









}