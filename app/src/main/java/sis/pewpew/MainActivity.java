package sis.pewpew;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sis.pewpew.connections.AuthActivity;
import sis.pewpew.fragments.AboutFragment;
import sis.pewpew.fragments.AchievementsFragment;
import sis.pewpew.fragments.ConsoleFragment;
import sis.pewpew.fragments.EventsFragment;
import sis.pewpew.fragments.FeedbackFragment;
import sis.pewpew.fragments.GratitudeFragment;
import sis.pewpew.fragments.MapFragment;
import sis.pewpew.fragments.NewsFragment;
import sis.pewpew.fragments.ProfileFragment;
import sis.pewpew.fragments.ProgressFragment;
import sis.pewpew.fragments.RatingFragment;
import sis.pewpew.fragments.SettingsFragment;
import sis.pewpew.fragments.ShareFragment;
import sis.pewpew.fragments.TrainingFragment;
import sis.pewpew.utils.NetIntegrationActivity;

public class MainActivity extends NetIntegrationActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public boolean mLocationPermissionGranted;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    AchievementsFragment achievementsFragment;
    FeedbackFragment feedbackFragment;
    GratitudeFragment gratitudeFragment;
    MapFragment mapFragment;
    EventsFragment eventsFragment;
    ProgressFragment progressFragment;
    RatingFragment ratingFragment;
    ProfileFragment profileFragment;
    ConsoleFragment consoleFragment;
    NewsFragment newsFragment;
    SettingsFragment settingsFragment;
    ShareFragment shareFragment;
    TrainingFragment trainingFragment;
    AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (user == null) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            int PERMISSION_REQUEST_CODE = 5;
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }

        aboutFragment = new AboutFragment();
        achievementsFragment = new AchievementsFragment();
        feedbackFragment = new FeedbackFragment();
        gratitudeFragment = new GratitudeFragment();
        mapFragment = new MapFragment();
        profileFragment = new ProfileFragment();
        progressFragment = new ProgressFragment();
        ratingFragment = new RatingFragment();
        consoleFragment = new ConsoleFragment();
        newsFragment = new NewsFragment();
        settingsFragment = new SettingsFragment();
        shareFragment = new ShareFragment();
        trainingFragment = new TrainingFragment();
        eventsFragment = new EventsFragment();

        android.app.FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.container, progressFragment);
        fragmentTransaction1.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_progress) {
            fragmentTransaction.replace(R.id.container, progressFragment);
        } else if (id == R.id.nav_map) {
            fragmentTransaction.replace(R.id.container, mapFragment);
        } else if (id == R.id.nav_rating) {
            fragmentTransaction.replace(R.id.container, ratingFragment);
        } else if (id == R.id.nav_profile) {
            fragmentTransaction.replace(R.id.container, profileFragment);
        } else if (id == R.id.nav_achievements) {
            fragmentTransaction.replace(R.id.container, achievementsFragment);
        } else if (id == R.id.nav_events) {
            fragmentTransaction.replace(R.id.container, eventsFragment);
        } else if (id == R.id.nav_training) {
            fragmentTransaction.replace(R.id.container, trainingFragment);
        } else if (id == R.id.nav_console) {
            fragmentTransaction.replace(R.id.container, consoleFragment);
        } else if (id == R.id.nav_news) {
            fragmentTransaction.replace(R.id.container, newsFragment);
        } else if (id == R.id.nav_settings) {
            fragmentTransaction.replace(R.id.container, settingsFragment);
        } else if (id == R.id.nav_share) {
            fragmentTransaction.replace(R.id.container, shareFragment);
        } else if (id == R.id.nav_feedback) {
            fragmentTransaction.replace(R.id.container, feedbackFragment);
        } else if (id == R.id.nav_about) {
            fragmentTransaction.replace(R.id.container, aboutFragment);
        } else if (id == R.id.nav_gratitude) {
            fragmentTransaction.replace(R.id.container, gratitudeFragment);
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
