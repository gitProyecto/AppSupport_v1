package gcm.play.android.samples.com.gcmquickstart;

/**
 * Created by luisafm on 13/11/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class helpBD extends SQLiteOpenHelper {

    String tableMessage = "CREATE TABLE messages(id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT unique, command TEXT, fromm TEXT, too TEXT, priority INTEGER, size INTEGER, checksum INTEGER, date INTEGER,  status INTEGER );";

    String tableiFilter = "CREATE TABLE ifilters(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT unique, company TEXT, latitud TEXT, longitud TEXT);";
    String tablechangeiFilter = "CREATE TABLE changeifilters(id INTEGER PRIMARY KEY AUTOINCREMENT, ifilter_id INTEGER, status INTEGER, lastchange INTEGER);";

    String tableiUps = "CREATE TABLE ups(id INTEGER PRIMARY KEY AUTOINCREMENT, ifilter_id INTEGER, ups_id TEXT, name TEXT, brand TEXT);";
    String tablechangeUps = "CREATE TABLE changeups(id INTEGER PRIMARY KEY AUTOINCREMENT, ups_id INTEGER, status INTEGER, batteryC REAL, batteryV REAL, lineV REAL, lastStateChange INTEGER, time_checked INTEGER);";

    String tableInter = "CREATE TABLE inter(id INTEGER PRIMARY KEY AUTOINCREMENT, ifilter_id INTEGER, name TEXT, ipaddr TEXT);";
    String tablechangeInter = "CREATE TABLE changeinter(id INTEGER PRIMARY KEY AUTOINCREMENT, inter_id INTEGER, status INTEGER, lastchange INTEGER);";

    String tableSwitch = "CREATE TABLE switch(id INTEGER PRIMARY KEY AUTOINCREMENT, ifilter_id INTEGER, name TEXT, ports INTEGER);";
    String tablechangeSwitch = "CREATE TABLE changeswitch(id INTEGER PRIMARY KEY AUTOINCREMENT, switch_id INTEGER, status INTEGER, lastchange INTEGER);";

    String tableServers = "CREATE TABLE servers(id INTEGER PRIMARY KEY AUTOINCREMENT, ifilter_id INTEGER, name TEXT);";
    String tablechangeServers = "CREATE TABLE changeservers(id INTEGER PRIMARY KEY AUTOINCREMENT, server_id INTEGER, status INTEGER, load REAL, lastchange INTEGER);";

    String tableVpns = "CREATE TABLE vpns(id INTEGER PRIMARY KEY AUTOINCREMENT, ifilter_id INTEGER, name TEXT);";
    String tablechangeVpns = "CREATE TABLE changevpns(id INTEGER PRIMARY KEY AUTOINCREMENT, vpn_id INTEGER, status INTEGER, lastchange INTEGER);";


    String tableUser = "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT unique, email TEXT, position TEXT, status INTEGER);";



    public helpBD(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tableMessage);

        db.execSQL(tableiFilter);
        db.execSQL(tablechangeiFilter);
        db.execSQL(tableiUps);
        db.execSQL(tablechangeUps);
        db.execSQL(tableInter);
        db.execSQL(tablechangeInter);
        db.execSQL(tableSwitch);
        db.execSQL(tablechangeSwitch);
        db.execSQL(tableServers);
        db.execSQL(tablechangeServers);
        db.execSQL(tableVpns);
        db.execSQL(tablechangeVpns);


        db.execSQL(tableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}