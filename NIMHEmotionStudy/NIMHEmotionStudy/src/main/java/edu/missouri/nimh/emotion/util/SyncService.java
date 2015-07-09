package edu.missouri.nimh.emotion.util;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import edu.missouri.nimh.emotion.database.DAO;

/**
 * Created by compsci on 7/2/15.
 */
public class SyncService extends IntentService {

    public static final String TAG = "SyncService";
    public static final String URL = "http://dslsrv8.cs.missouri.edu/~jmkwdf/CrtNIMH/example.php";
    private URI uri;

    public SyncService(){
        super("SyncService");

        try {
            uri = new URI(URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w(TAG, "Service started in SyncService");
        performSync();

    }

    /**
     * Performs the synchronization with the server using TransmitJSONData
     */
    private void performSync() {

        DAO db = new DAO(this);

        // Get the event to sync
        JSONArray jsonArray = db.getEventsToSync();
        JSONObject[] events = new JSONObject[jsonArray.length()];

        // Turn the JSONArray into an array of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                events[i] = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Tell TransmitJSONData to send the JSON to server
        new TransmitJSONData(uri, TransmitJSONData.JSONMode.ALWAYS_OUTPUT_ARRAY, db).execute(events);
    }


}
