package weatherapp.barant2003.com.weatherapplications;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.service.voice.VoiceInteractionSession;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.text.Text;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final float DEFAULT_ZOOM = 11;
    private static final String TAG = "Location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;
    GoogleMap maps;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private GoogleApiClient mGoogleApiClient;
    private CameraPosition mCameraPosition;
    private LatLng mDefaultLocation;
    private LocationManager mLocationManager;
    private TextView city;
    private TextView temps;
    private HttpURLConnection con;
    private BufferedReader reader;
    private String jSonWeather;
    private InputStream is;
    private InputStream in;
    private String IMG_URL = "http://openweathermap.org/img/w/";
    private String JsonWeather = "http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&units=imperial&appid=f0c8fd49bf50b32e87bf36220a670ad4";
    private ImageView nav_image;
   Bitmap imgur;
    private double latitude;
    private double longitude;
    WebView browser;
    Handler mHandler;
    Runnable refresh;
    String cityin;
    String state;
    ValueAnimator vAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buildGoogleAPi();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        city = (TextView) view.findViewById(R.id.city);
        nav_image = (ImageView) view.findViewById(R.id.nav_weather_icon);
        temps = (TextView) view.findViewById(R.id.temp);
    }

//        mHandler = new Handler();
//        refresh = new Runnable() {
//            @Override
//            public void run() {
//              mHandler.postDelayed(refresh, 20000);
//                Toast.makeText(MainActivity.this, "Refreshing", Toast.LENGTH_SHORT).show();
//               updateLocationUI();
//            }
//        };
//        mHandler.post(refresh);
//    }


    private void buildGoogleAPi() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                 .addConnectionCallbacks(this)
                 .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        maps = googleMap;


       maps.setMapType(GoogleMap.MAP_TYPE_HYBRID);







        updateLocationUI();
        getDeviceLocation();
    }
    private void updateLocationUI() {
        if (maps == null) {
            return;
        }

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            maps.setMyLocationEnabled(true);
            maps.getUiSettings().setMyLocationButtonEnabled(true);
            mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = mLocationManager.getBestProvider(criteria, true);

          mLastKnownLocation = mLocationManager.getLastKnownLocation(provider);

            if(mLastKnownLocation != null)
            {
                longitude = mLastKnownLocation.getLongitude();
                latitude = mLastKnownLocation.getLatitude();
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());


                List<Address> addresses = null;
                Address address;


                try {
                    addresses = geocoder.getFromLocation(latitude,longitude, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(addresses == null || addresses.isEmpty())
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                address = addresses.get(0);
                String state = address.getAdminArea();


                mDefaultLocation = new LatLng(latitude, longitude);


                //f0c8fd49bf50b32e87bf36220a670ad4 Openweather APPID //
                try
                {
                   con = (HttpURLConnection)(new URL("http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&units=imperial&appid=f0c8fd49bf50b32e87bf36220a670ad4")).openConnection();
                    con.setRequestMethod("GET");
                    con.connect();

                     is = con.getInputStream();

                    StringBuffer buffer = new StringBuffer();
                    if(is == null)
                    {
                        // Do Nothing //
                        return;
                    }


                    reader = new BufferedReader(new InputStreamReader(is));

                    String line;
                     while((line = reader.readLine()) != null)
                     {
                         buffer.append(line + "\n");
                     }
                     if(buffer.length() == 0)
                     {
                         return;
                     }
                     jSonWeather = buffer.toString();

                    JSONObject jsonObj = new JSONObject(jSonWeather);
                    JSONObject jsonWeatherInfo = jsonObj.getJSONArray("weather").getJSONObject(0);
                    JSONObject temp = jsonObj.getJSONObject("main");
                    String citys = jsonObj.getString("name");
                    String weahterIcon = jsonWeatherInfo.getString("icon");
                    Double temperature = temp.getDouble("temp");

                    getImage(weahterIcon);

                    RadarRequest(state, citys);

                    city.setText(citys + "," + state);
                    temps.setText(String.format("%.1f", + temperature)+ "F°");

                    mHandler = new Handler();
        refresh = new Runnable() {
            @Override
           public void run() {
             mHandler.postDelayed(refresh, 11000);
                Toast.makeText(MainActivity.this, "Refreshing", Toast.LENGTH_SHORT).show();
                    GroundOverlayOptions newarkMap = new GroundOverlayOptions();
                    newarkMap.image(BitmapDescriptorFactory.fromBitmap(imgur));
                    newarkMap.position(mDefaultLocation, 140000f, 140000f);
                    GroundOverlay imageOverlay =  maps.addGroundOverlay(newarkMap);
                  //  OverLay(imageOverlay);
//               updateLocationUI();
           }
        };
        mHandler.post(refresh);

                  //  animator.

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } else {
            maps.setMyLocationEnabled(false);
            maps.getUiSettings().setMyLocationButtonEnabled(false);

            mLastKnownLocation = null;

        }
    }
    public void OverLay(final GroundOverlay groundOverlay){
        vAnimator = ValueAnimator.ofInt(0, 2000);
        int r=99999;
        vAnimator.setRepeatCount(r);
        //vAnimator.setIntValues(0, 500);
        vAnimator.setDuration(12000);
       // vAnimator.setEvaluator(new IntEvaluator());
      //  vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                Integer i = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(i);
            }
        });
        vAnimator.start();
    }
    public String RadarRequest(String state, String city) {


        try {
            con = (HttpURLConnection) (new URL("http://api.wunderground.com/api/56815a4f15520bbc/animatedradar/q/"+state+"/"+city+".gif?num=6&delay=50&interval=30")).openConnection();

            con.setRequestMethod("GET");

            // con.setRequestProperty("AuthToken" , token);
            con.connect();

            InputStream ins = con.getInputStream();

            imgur = BitmapFactory.decodeStream(ins);


        // Add an overlay to the map, retaining a handle to the GroundOverlay object.

            StringBuffer buffer = new StringBuffer();
            if (ins == null) {
                Log.d("Error!", "Inputstream is null!");
                return null;
            }


            reader = new BufferedReader(new InputStreamReader(ins));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            jSonWeather = buffer.toString();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public byte[] getImage(String code)
    {
        HttpURLConnection con = null;
        is = null;


        try
        {
            con = (HttpURLConnection)(new URL(IMG_URL + code + ".png")).openConnection();
            con.setDoInput(true);
            con.setRequestMethod("GET");

            con.connect();


            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            Bitmap img = BitmapFactory.decodeStream(is);
            nav_image.setImageBitmap(img);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while(is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            {
                try{is.close();}catch (Throwable T){}
                try{con.disconnect();}catch(Throwable t){}

            }
            return null;
        }
    }
    private void getDeviceLocation() {
    /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */

        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.

        if (mCameraPosition != null) {
            maps.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");

            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            maps.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }



    @Override
    public void onConnected(Bundle connectionHint) {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
