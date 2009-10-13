package net.sourceforge.subsonic.androidapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import net.sourceforge.subsonic.androidapp.R;
import net.sourceforge.subsonic.androidapp.util.Constants;
import net.sourceforge.subsonic.androidapp.service.DownloadService;
import net.sourceforge.subsonic.androidapp.service.StreamService;

public class MainActivity extends OptionsMenuActivity {

    private static final int DIALOG_ID_SEARCH = 1;
    private AlertDialog searchDialog;

    /**
    * Called when the activity is first created.
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        startService(new Intent(this, DownloadService.class));
        startService(new Intent(this, StreamService.class));
        setContentView(R.layout.main);

        Button browseButton = (Button) findViewById(R.id.main_browse);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SelectArtistActivity.class));
            }
        });

        final Button searchButton = (Button) findViewById(R.id.main_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_ID_SEARCH);
            }
        });

        Button downloadQueueButton = (Button) findViewById(R.id.main_download_queue);
        downloadQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DownloadQueueActivity.class));
            }
        });

        Button streamQueueButton = (Button) findViewById(R.id.main_stream_queue);
        streamQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StreamQueueActivity.class));
            }
        });

        ImageView settingsButton = (ImageView) findViewById(R.id.main_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        ImageView helpButton = (ImageView) findViewById(R.id.main_help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });

        LayoutInflater factory = LayoutInflater.from(this);
        View searchView = factory.inflate(R.layout.search, null);

        final Button searchViewButton = (Button) searchView.findViewById(R.id.search_search);
        final TextView queryTextView = (TextView) searchView.findViewById(R.id.search_query);
        searchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, SelectAlbumActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_NAME_QUERY, String.valueOf(queryTextView.getText()));
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(searchView);
        builder.setCancelable(true);
        searchDialog = builder.create();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID_SEARCH) {
            return searchDialog;
        }
        return null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            showDialog(DIALOG_ID_SEARCH);
        }
        return super.onKeyDown(keyCode, event);
    }

}