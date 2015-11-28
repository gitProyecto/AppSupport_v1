package gcm.play.android.samples.com.gcmquickstart;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        launchRingDialog();
    }

    public void launchRingDialog(){
        final ProgressDialog ringProgressDialog = ProgressDialog.show(Main.this, "Please wait ...", "Downloading Image ...", true);
        ringProgressDialog.setCancelable(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                }

                ringProgressDialog.dismiss();
            }

        }).start();
    }

}
