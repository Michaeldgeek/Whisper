package challenge.magnet.android.whisper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import net.yanzm.mth.MaterialTabHost;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import challenge.magnet.android.whisper.activities.FindFriendsSetup;
import challenge.magnet.android.whisper.activities.LoginActivity;
import challenge.magnet.android.whisper.activities.ShowFriends;
import challenge.magnet.android.whisper.adapters.RecentChatAdapterDir.MyListCursorAdapter;
import challenge.magnet.android.whisper.databases.TablesDefn.RecentChatTableDefnDB;
import challenge.magnet.android.whisper.databases.TablesDefn.UserTableDefn;
import challenge.magnet.android.whisper.databases.TablesDefn.WhisperDB;
import challenge.magnet.android.whisper.models.FbUser;

/**
 * According to facebook documentation,
 * the facebook api has to be initialized first before call to setContentView.
 * I called the initFacebook() method which I defined in the MainActivity class.
 * In the initFacebook() method, I configured the third-party library which
 * wraps up the Facebook sdk. Check link to know more about this library
 *https://github.com/sromku/android-simple-facebook/wiki/Configuration
 * And take a look at the Facebook api documentation(not compulsory since we are using a third-party)
 * https://developers.facebook.com/docs/android
 *
 */
public class MainActivity extends AppCompatActivity  {

    private SimpleFacebook mSimpleFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebook();
        setContentView(R.layout.activity_main);
        // setup facebook api
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        /**
         * Used a third-party library for the tabs. You can check the
         * documentation here. Based on the instruction there I wrote
         * the remaining codes. https://github.com/yanzm/MaterialTabHost
         */
        MaterialTabHost tabHost = (MaterialTabHost) findViewById(android.R.id.tabhost);
        tabHost.setType(MaterialTabHost.Type.FullScreenWidth);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(pagerAdapter.getPageTitle(i));
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(tabHost);
        viewPager.setCurrentItem(0); // Added this line myself so that the tab is set to chat tab when run.

        tabHost.setOnTabChangeListener(new MaterialTabHost.OnTabChangeListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_new_chat :
                startNewChat();
                break;
            case R.id.action_settings :
                //this.startActivity(new Intent(this,SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startNewChat() {
        startActivity(new Intent(this, ShowFriends.class));
    }

    public void initFacebook() {
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.app_id))
                .setNamespace(Constants.APP_NS)
                .setPermissions(Constants.permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_whisperers).toUpperCase(l);
                 case 1:
                    return getString(R.string.title_find_friends).toUpperCase(l);
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment implements
            LoaderManager.LoaderCallbacks<Cursor> {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final int URL_LOADER = 0;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private Cursor cursor;
        RecyclerView chatRv;
        RecyclerView whispererRv;
        MyListCursorAdapter cursorAdapter;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getLoaderManager().initLoader(URL_LOADER, savedInstanceState, this);
        }

        private  String getUsername() {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.user_login_details), Context.MODE_PRIVATE);
            String username = sharedPref.getString(getString(R.string.username), "notfound");
            if(username.contains("notfound")){
                Toast.makeText(getActivity(),"Login credentials expired",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return "";
            }
            else {
                return username;
            }
        }



        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
             if (getArguments().getInt(ARG_SECTION_NUMBER)== 1) {
                // Chat Fragment
                rootView = inflater.inflate(R.layout.pager_chats, container, false);
                chatRv = (RecyclerView) rootView.findViewById(R.id.chat_list);
                cursorAdapter = new MyListCursorAdapter(getActivity(),cursor);
                chatRv.setAdapter(cursorAdapter);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                chatRv.setLayoutManager(layoutManager);
                return rootView;

            }
            else {
                rootView = inflater.inflate(R.layout.pager_find_friends, container, false);
                if(rootView != null){
                    Button fb = (Button) rootView.findViewById(R.id.fb);
                    fb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(),FindFriendsSetup.class);
                            intent.putExtra("fb","1");
                            getActivity().startActivity(intent);
                        }
                    });
                    Button insta = (Button) rootView.findViewById(R.id.instagram);
                    insta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(),FindFriendsSetup.class);
                            intent.putExtra("insta","2");
                            getActivity().startActivity(intent);
                        }
                    });
                }
                return rootView;
            }
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case URL_LOADER:
                    // Returns a new CursorLoader
                    return new CursorLoader(
                            getActivity(),   // Parent activity context
                            Uri.parse(Constants.RECENT_CHAT_PROVIDER_AUTORITY +"/" + RecentChatTableDefnDB.RecentChatTableDfn.TABLE_NAME),        // Table to query
                            new String[]{RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_USER
                                    , RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_DATE
                                    ,RecentChatTableDefnDB.RecentChatTableDfn.COLUMN_NAME_PROFILE_PIC
                                    , RecentChatTableDefnDB.RecentChatTableDfn._ID},     // Projection to return
                            null,            // No selection clause
                            null,            // No selection arguments
                            null             // Default sort order
                    );
                default:
                    // An invalid id was passed in
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(data != null) {
                cursorAdapter.changeCursor(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
           cursorAdapter.changeCursor(null);
        }
    }
}
