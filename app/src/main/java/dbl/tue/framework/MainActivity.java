package dbl.tue.framework;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBEnvironment;
import com.quickblox.location.model.QBLocation;
import com.quickblox.users.model.QBUser;
import com.quickblox.users.model.QBUserWrap;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Location loc = null;
    PlayServiceHelper helper;
    static final String APP_ID = "37437";
    static final String AUTH_KEY = "5Ozb4CuDbuYWvfL";
    static final String AUTH_SECRET = "KkkSK8UF7OVUb8a";
    static final String ACCOUNT_KEY = "961";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        helper = new PlayServiceHelper(this);
        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
//                final Location loc2 = new Location(location);
//                QBLocations.getLocation(Globals.getInstance().getCurrentuser().getId(), new QBEntityCallback<QBLocation>() {
//                    @Override
//                    public void onSuccess(QBLocation qbLocation, Bundle args) {
//
//                        loc2.setLongitude(qbLocation.getLongitude());
//                        loc2.setLatitude(qbLocation.getLatitude());
//
//                        if (loc2.distanceTo(location) >= 100) {
//                            QBUserWrap userWrap = new QBUserWrap();
//                            userWrap.setUser(Globals.getInstance().currentuser);
//                            qbLocation.setUser(userWrap);
//                            QBLocations.createLocation(qbLocation, new QBEntityCallback<QBLocation>() {
//                                @Override
//                                public void onSuccess(QBLocation qbLocation, Bundle args) {
//                                    Toast.makeText(MainActivity.this, "updated location", Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onError(QBResponseException errors) {
//
//                                }
//                            }, "Somebody is near you.", QBEnvironment.DEVELOPMENT, 0.1f);
//
//
//                        }
//                    }
//
//                    @Override
//                    public void onError(QBResponseException errors) {
//
//                    }
//                });

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {


            }

            @Override
            public void onProviderDisabled(String provider) {


            }
        };
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);


        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        loc=getLastKnownLocation(mLocationManager);
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();


        final QBLocation qbLocation = new QBLocation(latitude, longitude);
        QBUserWrap userWrap = new QBUserWrap();
        userWrap.setUser(Globals.getInstance().currentuser);
        qbLocation.setUser(userWrap);
        QBLocations.createLocation(qbLocation, new QBEntityCallback<QBLocation>() {
            @Override
            public void onSuccess(QBLocation qbLocation, Bundle args) {

            }

            @Override
            public void onError(QBResponseException errors) {

            }
        }, "Somebody is near you.", QBEnvironment.DEVELOPMENT, 0.5f);


        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_item_inbox) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

//        final QBChatService chatService = QBChatService.getInstance();
//        QBAuth.createSession(Globals.getInstance().getCurrentuser(), new QBEntityCallback<QBSession>() {
//            @Override
//            public void onSuccess(QBSession session, Bundle params) {
//                // success, login to chat
//                chatService.login(Globals.getInstance().getCurrentuser(), new QBEntityCallback() {
//
//
//                    @Override
//                    public void onSuccess(Object o, Bundle bundle) {
//                        Toast.makeText(getApplicationContext(),"Woho",Toast.LENGTH_SHORT);
//                    }
//
//                    @Override
//                    public void onError(QBResponseException errors) {
//                        Toast.makeText(getApplicationContext(),"Dammit",Toast.LENGTH_SHORT);
//                    }
//                });
//            }
//
//            @Override
//            public void onError(QBResponseException errors) {
//                Toast.makeText(getApplicationContext(),errors.toString(),Toast.LENGTH_SHORT);
//            }
//        });

    }

    private Location getLastKnownLocation(LocationManager mLocationManager) {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}