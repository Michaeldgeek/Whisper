package challenge.magnet.android.whisper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import challenge.magnet.android.whisper.R;
import challenge.magnet.android.whisper.models.User;

public class SettingsActivity extends ActionBarActivity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    String[] arrays = {"Help","Profile","Account","Notifications","Chats"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        listView =  (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.settings_list,R.id.settings_text,arrays);
        listView.setAdapter(arrayAdapter);
        setupActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {
                NavUtils.navigateUpTo(this, upIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        if(this.getSupportActionBar() != null){
            ActionBar actionBar = this.getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.left);
        }
    }
}
