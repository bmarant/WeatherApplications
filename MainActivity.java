package weatherapp.barant2003.com.weatherapplications;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
import android.service.voice.VoiceInteractionSession;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aerisweather.aeris.communication.Aeris;
import com.aerisweather.aeris.communication.AerisCallback;
import com.aerisweather.aeris.communication.AerisEngine;
import com.aerisweather.aeris.communication.EndpointType;
import com.aerisweather.aeris.communication.loaders.ObservationsTaskCallback;
import com.aerisweather.aeris.location.LocationHelper;
import com.aerisweather.aeris.maps.AerisMapContainerView;
import com.aerisweather.aeris.maps.AerisMapOptions;
import com.aerisweather.aeris.maps.AerisMapView;
import com.aerisweather.aeris.maps.MapOptionsActivity;
import com.aerisweather.aeris.maps.MapOptionsActivityBuilder;
import com.aerisweather.aeris.model.AerisError;
import com.aerisweather.aeris.model.AerisPermissions;
import com.aerisweather.aeris.model.AerisResponse;
import com.aerisweather.aeris.response.ObservationResponse;
import com.aerisweather.aeris.tiles.AerisAmp;
import com.aerisweather.aeris.tiles.AerisAmpGetLayersTask;
import com.aerisweather.aeris.tiles.AerisAmpLayer;
import com.aerisweather.aeris.tiles.AerisAmpOnGetLayersTaskCompleted;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.formats.NativeAd;
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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aerisweather.aeris.maps.MapOptionsActivity.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnTouchListener, GoogleMap.OnCameraMoveListener {

    private static final float DEFAULT_ZOOM = 11;
    private static final String TAG = "Location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;
    private static final int REQUEST_PERMISSIONS = 0;
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
    private String urls;
    private InputStream in;
    private Menu menuItems;
    private MenuItem weatherItems;
    private String IMG_URL = "http://openweathermap.org/img/w/";
    private String JsonWeather = "http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&units=imperial&appid=f0c8fd49bf50b32e87bf36220a670ad4";
    private ImageView nav_image;
    Bitmap imgur;
    Context mContext = MainActivity.this;
    private double latitude;
    private double longitude;
    WebView browser;
    Handler mHandler;
    Runnable refresh;
    String cityin;
    String state;
    List<Address> addresses = null;
    Address address;
    ValueAnimator vAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buildGoogleAPi();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        city = (TextView) view.findViewById(R.id.cityAndState);
        nav_image = (ImageView) view.findViewById(R.id.nav_weather_icon);
        temps = (TextView) view.findViewById(R.id.temp);
        menuItems = navigationView.getMenu();

        updateLocationUI();

    }


    public void updateLocationUI() {

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {

            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = mLocationManager.getBestProvider(criteria, true);

            mLastKnownLocation = mLocationManager.getLastKnownLocation(provider);

            if (mLastKnownLocation != null) {
                longitude = mLastKnownLocation.getLongitude();
                latitude = mLastKnownLocation.getLatitude();
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());


                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses == null || addresses.isEmpty())
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                address = addresses.get(0);
                state = address.getAdminArea();


                mDefaultLocation = new LatLng(latitude, longitude);

                HttpConnection(latitude, longitude);
            }
        }


    }

    public void HttpConnection(double lats, double longs) {
        // latitude = lats;
        //longitude = longs;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lats, longs, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.isEmpty())
            try {
                addresses = geocoder.getFromLocation(lats, longs, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }

        address = addresses.get(0);
        String states = address.getAdminArea();

        //  longs = Double.valueOf(laong);
        // System.out.println(laong);
        //f0c8fd49bf50b32e87bf36220a670ad4 Openweather APPID //
        try {
            con = (HttpURLConnection) (new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lats + "&lon=" + longs + "&units=imperial&appid=f0c8fd49bf50b32e87bf36220a670ad4")).openConnection();
            con.setRequestMethod("GET");
            con.connect();

            is = con.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (is == null) {
                // Do Nothing //
                return;
            }


            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return;
            }
            jSonWeather = buffer.toString();

            JSONObject jsonObj = new JSONObject(jSonWeather);
            JSONObject jsonWeatherInfo = jsonObj.getJSONArray("weather").getJSONObject(0);
            JSONObject temp = jsonObj.getJSONObject("main");
            String citys = jsonObj.getString("name");
            String weahterIcon = jsonWeatherInfo.getString("icon");
            Double temperature = temp.getDouble("temp");
            String baramoter =  temp.getString("pressure");
            Double high = temp.getDouble("temp_max");
            Double low = temp.getDouble("temp_min");
            JSONObject wind = jsonObj.getJSONObject("wind");
            String winds = wind.getString("speed");
            String deg = wind.getString("deg");


            city.setText(citys + "," + states);
            temps.setText(String.format("%.1f", +temperature) + "F°");

            getImage(weahterIcon);


            weatherItems = menuItems.findItem(R.id.barometer);
            weatherItems.setTitle("Barometer: " +baramoter + " hPa");
            MenuItem speed = menuItems.findItem(R.id.windSpeed);
            speed.setTitle("Wind Speed: " + winds + " Mph");
            MenuItem degrees = menuItems.findItem(R.id.windDirection);
            degrees.setTitle("Wind Direction: " + deg +"°");
            MenuItem highs = menuItems.findItem(R.id.locationhl);
            highs.setTitle("Your Location " +"H: "+ String.format("%.1f", +high) + "F° " +"L: "+ String.format("%.1f", +low) + "F°");


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public MainActivity newQuery(double lats, double longs) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lats, longs, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.isEmpty())
            try {
                addresses = geocoder.getFromLocation(lats, longs, 10);

            } catch (IOException e) {
                FragmentManager fm = getFragmentManager();


                Map_Fragment fragment = (Map_Fragment)fm.findFragmentById(R.id.frag_container);

                fragment.setTemp("Null");
                fragment.setText("Null" + "Null");
                e.printStackTrace();
            }

            else {


            address = addresses.get(0);
        }








        //  longs = Double.valueOf(laong);
        // System.out.println(laong);
        //f0c8fd49bf50b32e87bf36220a670ad4 Openweather APPID //
        try {
            con = (HttpURLConnection) (new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lats + "&lon=" + longs + "&units=imperial&appid=f0c8fd49bf50b32e87bf36220a670ad4")).openConnection();
            con.setRequestMethod("GET");
            con.connect();

            is = con.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (is == null) {
                // Do Nothing //
                return null;
            }


            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            jSonWeather = buffer.toString();

            JSONObject jsonObj = new JSONObject(jSonWeather);
            JSONObject jsonWeatherInfo = jsonObj.getJSONArray("weather").getJSONObject(0);
            JSONObject temp = jsonObj.getJSONObject("main");
            String citys = jsonObj.getString("name");
            String weahterIcon = jsonWeatherInfo.getString("icon");
            Double temperature = temp.getDouble("temp");
            String baramoter =  temp.getString("pressure");
            Double high = temp.getDouble("temp_max");
            Double low = temp.getDouble("temp_min");
            JSONObject wind = jsonObj.getJSONObject("wind");
            String winds = wind.getString("speed");
            String deg = wind.getString("deg");

            getImage(weahterIcon);

            weatherItems = menuItems.findItem(R.id.barometer);
            weatherItems.setTitle("Barometer" +baramoter);

            weatherItems = menuItems.findItem(R.id.barometer);
            weatherItems.setTitle("Barometer: " +baramoter + " hPa");
            MenuItem speed = menuItems.findItem(R.id.windSpeed);
            speed.setTitle("Wind Speed: " + winds + " Mph");
            MenuItem degrees = menuItems.findItem(R.id.windDirection);
            degrees.setTitle("Wind Direction " + deg +"°");
            MenuItem highs = menuItems.findItem(R.id.locationhl);
            highs.setTitle("Your Location " +"H: "+ String.format("%.1f", +high) + "F°" +"L: "+ String.format("%.1f", +low) + "F°");

            FragmentManager fm = getFragmentManager();

//if you added fragment via layout xml
            Map_Fragment fragment = (Map_Fragment)fm.findFragmentById(R.id.frag_container);

            fragment.setTemp(String.format("%.1f", + temperature)+ "F°");
            String states = address.getAdminArea();
            fragment.setText(citys +"," + states);



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
    public void onConnected(Bundle connectionHint) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fraggy = new Map_Fragment();
        fragmentTransaction.add(R.id.frag_container, fraggy);
        fragmentTransaction.commit();


        //  SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        //  mapFragment.getMapAsync(this);





    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x =(int)event.getX();
        int y = (int)event.getY();


        Log.d("Lat/Long:", " Long:" +String.valueOf(x) +"Lat" + String.valueOf(y));
        return false;
    }

    @Override
    public void onCameraMove() {
        System.out.println("Hello");
    }
}
