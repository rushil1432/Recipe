package com.example.recipe;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipe.adapter.DrawerListAdapter;
import com.example.recipe.fragments.FavoriteFragment;
import com.example.recipe.fragments.FeedbackFragment;
import com.example.recipe.fragments.HomeFragment;
import com.example.recipe.fragments.MyRecipeFragment;
import com.example.recipe.fragments.ProfileFragment;
import com.example.recipe.helpers.AppHelper;
import com.example.recipe.helpers.BottomNavigationViewBehavior;
import com.example.recipe.helpers.BottomNavigationViewHelper;
import com.example.recipe.helpers.SharedPrefManager;
import com.example.recipe.model.NavItem;
import com.example.recipe.model.User;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = HomeActivity.class.getSimpleName();

    ListView mDrawerList;
    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem>  mNavItems;
    private CharSequence mTitle;
    User user;
    String name;
    TextView txtUsername,txtLogout;
    Fragment fragment = null;
    RelativeLayout mDrawerPane;
    FragmentManager fragmentManager;
    FloatingActionButton floatingActionButton;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_home);

        setupToolbar();
        setTitle("Home");

        init();

        if (savedInstanceState == null) {

            loadHomeFragment();
        }

        mNavItems = new ArrayList<NavItem>();
        user = SharedPrefManager.getInstance(HomeActivity.this).getUser();
        name=user.getEmail();
        if (name.equalsIgnoreCase("")){

            txtUsername.setText("Rushil Patel");
        }
        else {
            txtUsername.setText(name);
        }


        mNavItems.add(new NavItem("Home", R.drawable.home));
        mNavItems.add(new NavItem("Profile", R.drawable.profile));
        mNavItems.add(new NavItem("Feedback",R.drawable.ic_feedback));


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        txtLogout.setOnClickListener(this);

        setupBottomNavigationView();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.LaunchActivity(HomeActivity.this,AddRecipeActivity.class);
            }
        });



    }

    private void init() {


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navList);
        txtUsername=(TextView)findViewById(R.id.textLoguser_name);
        txtLogout=(TextView)findViewById(R.id.txtLogout);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.floatingActionButton);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.txtLogout:
                final Dialog mydialog = new Dialog(this);
                mydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mydialog.setContentView(R.layout.custom_dialog);
                Button dialog_ok = (Button) mydialog.findViewById(R.id.dialog_ok);
                Button dialog_cancel = (Button) mydialog.findViewById(R.id.dialog_cancel);
                mydialog.show();
                mydialog.setCancelable(false);

                dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPrefManager.getInstance(HomeActivity.this).logout();
                        HomeActivity.this.finish();

                    }
                });
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mydialog.dismiss();
                    }
                });



                break;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            selectItem(position);



        }

    }

    private void setupBottomNavigationView(){
        Log.d(TAG,"setupBottomNavigationView: setting up BottomNavigationView.");
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomNavigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.my_home:

                        fragment = new HomeFragment();
                        break;
                    case R.id.my_favorites:
                        fragment=new FavoriteFragment();
                        break;

                    case R.id.my_cooking:
                        fragment=new MyRecipeFragment();
                        break;

                    case R.id.my_profile:
                        fragment = new ProfileFragment();
                        break;





                }
                /*final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment).commit();*/

                getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack("Home").commit();
                return true;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

      /*  if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {


        switch (position) {
            case 0:
                fragment = new HomeFragment();
                bottomNavigationView.setSelectedItemId(R.id.my_home);
                break;
            case 1:
                fragment = new ProfileFragment();
                bottomNavigationView.setSelectedItemId(R.id.my_profile);
                break;
            case 2:
                fragment = new FeedbackFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack("Home").commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);

            mDrawerLayout.closeDrawer(mDrawerPane);

        } else {
            Log.e("HomeActivity", "Error in creating fragment");
        }
    }




    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }



    @Override
    public void onBackPressed() {

      /*  if (mDrawerLayout.isDrawerOpen(mDrawerPane)) {
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();

        } else {
            super.onBackPressed();

        }*/

        if (mDrawerLayout.isDrawerOpen(mDrawerPane)) {
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
        else {

            if (bottomNavigationView.getSelectedItemId() == R.id.my_home) {
                //super.onBackPressed();
                finish();

            } else {
                bottomNavigationView.setSelectedItemId(R.id.my_home);
            }
        }




    }

  /*  void whenBackPressed() {
        Fragment fr = getSupportFragmentManager().findFragmentByTag("Home");
        // First close the drawer if open

        if (mDrawerLayout.isDrawerOpen(mDrawerPane)) {
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
        // else replace the home fragment if TAG is null
       else {
            if (fr == null) {
                selectItem(0);
                bottomNavigationView.setSelectedItemId(R.id.my_home);
            }

            // finally finish activity
            else {
                finish();
            }   }  }*/




    private void loadHomeFragment() {

        fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment,"Home").commit();
    }
}
