package weatherapp.barant2003.com.weatherapplications;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import weatherapp.barant2003.com.weatherapplications.MainActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aerisweather.aeris.communication.Aeris;
import com.aerisweather.aeris.communication.AerisCallback;
import com.aerisweather.aeris.communication.AerisEngine;
import com.aerisweather.aeris.communication.EndpointType;
import com.aerisweather.aeris.communication.fields.Fields;
import com.aerisweather.aeris.communication.fields.ObservationFields;
import com.aerisweather.aeris.communication.loaders.ObservationsTask;
import com.aerisweather.aeris.communication.loaders.ObservationsTaskCallback;
import com.aerisweather.aeris.communication.parameter.ParameterBuilder;
import com.aerisweather.aeris.communication.parameter.PlaceParameter;
import com.aerisweather.aeris.location.LocationHelper;
import com.aerisweather.aeris.maps.AerisMapContainerView;
import com.aerisweather.aeris.maps.AerisMapOptions;
import com.aerisweather.aeris.maps.AerisMapView;
import com.aerisweather.aeris.maps.interfaces.OnAerisMapLongClickListener;
import com.aerisweather.aeris.maps.interfaces.OnAerisMarkerInfoWindowClickListener;
import com.aerisweather.aeris.maps.markers.AerisMarker;
import com.aerisweather.aeris.model.AerisError;
import com.aerisweather.aeris.model.AerisPermissions;
import com.aerisweather.aeris.model.AerisResponse;
import com.aerisweather.aeris.model.Observation;
import com.aerisweather.aeris.model.RelativeTo;
import com.aerisweather.aeris.response.EarthquakesResponse;
import com.aerisweather.aeris.response.FiresResponse;
import com.aerisweather.aeris.response.ObservationResponse;
import com.aerisweather.aeris.response.RecordsResponse;
import com.aerisweather.aeris.response.StormCellResponse;
import com.aerisweather.aeris.response.StormReportsResponse;
import com.aerisweather.aeris.tiles.AerisAmp;
import com.aerisweather.aeris.tiles.AerisAmpAnimationInfo;
import com.aerisweather.aeris.tiles.AerisAmpGetLayersTask;
import com.aerisweather.aeris.tiles.AerisAmpLayer;
import com.aerisweather.aeris.tiles.AerisAmpOnGetLayersTaskCompleted;
import com.aerisweather.aeris.tiles.AerisPointData;
import com.aerisweather.aeris.tiles.AerisPolygonData;

import com.aerisweather.aeris.tiles.AerisTile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;
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

import static android.R.attr.x;
import static android.content.Context.LOCATION_SERVICE;


public class Map_Fragment extends Fragment implements
        OnAerisMapLongClickListener, AerisCallback, ObservationsTaskCallback,
        OnAerisMarkerInfoWindowClickListener, RefreshInterface, OnMapReadyCallback,  AerisMapContainerView.OnTouchListener, GoogleMap.OnCameraMoveListener {
    private LocationHelper m_locHelper;
    private Marker m_marker;
    //private TemperatureWindowAdapter m_infoAdapter;

    LayoutInflater m_inflater;
    ViewGroup m_container;
    Bundle m_savedInstanceState;
    GoogleMap m_googleMap;
    protected AerisMapView m_aerisMapView;
    private AerisMapOptions m_mapOptions = null;
    private AerisAmp m_aerisAmp;
    private boolean m_isMapReady = false;
    private boolean m_isAmpReady = false;
    private static final float DEFAULT_ZOOM = 11;
    private static final String TAG = "Location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;
    private static final int REQUEST_PERMISSIONS = 0 ;
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
    private String IMG_URL = "http://openweathermap.org/img/w/";
    private String JsonWeather = "http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&units=imperial&appid=f0c8fd49bf50b32e87bf36220a670ad4";
    private ImageView nav_image;
    Bitmap imgur;
    private double latitude;
    private double longitude;
    ImageView cross;
    BottomNavigationView nav;
    boolean switchIcon = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_inflater = inflater;
        m_container = container;
        m_savedInstanceState = savedInstanceState;
        AerisEngine.initWithKeys(this.getActivity().getString(R.string.aerisapi_client_id), this.getString(R.string.aerisapi_client_secret), getActivity());

        View view = inflater.inflate(R.layout.map_fragment, container, false);
        AerisMapContainerView mapContainer = (AerisMapContainerView) view.findViewById(R.id.maps);
        cross = (ImageView)view.findViewById(R.id.imageButton);

        nav = (BottomNavigationView)view.findViewById(R.id.navigation);



        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.navigation_home:
                       getDeviceLocation();
                        click();
                        break;
                    case R.id.play_anim:
                        switchIcon =! switchIcon;
                        if(switchIcon) {
                            item.setIcon(R.drawable.ic_pause_black_24dp);
                            m_aerisMapView.onPlayPressed();



                        }
                        else
                        {
                            item.setIcon(R.drawable.ic_play_arrow_blue_600_24dp);
                            m_aerisMapView.onPausePressed();



                        }





                }
                return true;
            }
        });


        cross.setOnTouchListener(this);


        m_aerisMapView = mapContainer.getAerisMapView();

        m_aerisMapView.onCreate(savedInstanceState);

        //create an instance of the AerisAMP class
        m_aerisAmp = new AerisAmp(getString(R.string.aerisapi_client_id), getString(R.string.aerisapi_client_secret));
buildGoogleAPi();
        //start the task to get the AMP layers
        try {
            //get all the possible layers, then get permissions from the API and generate a list of permissible layers
            new AerisAmpGetLayersTask(new GetLayersTaskCallback(), m_aerisAmp).execute().get();
        } catch (Exception ex) {
            String s = ex.getMessage();
            //if the task fails, keep going without AMP layers
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_isMapReady = true;
        m_googleMap = googleMap;

        m_aerisMapView.init(googleMap);

        if (m_isAmpReady) {
            initMap();




        }
    }

    private void updateLocationUI() {
        if (m_googleMap == null) {
            return;
        }

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
          m_googleMap.setMyLocationEnabled(true);
            m_aerisMapView.setMyLocationButtonEnabled(true);



            mLocationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = mLocationManager.getBestProvider(criteria, true);

            mLastKnownLocation = mLocationManager.getLastKnownLocation(provider);

            if(mLastKnownLocation != null) {
                longitude = mLastKnownLocation.getLongitude();
                latitude = mLastKnownLocation.getLatitude();
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());


                List<Address> addresses = null;
                Address address;


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
                String state = address.getAdminArea();
                String citys = address.getLocality();


                mDefaultLocation = new LatLng(latitude, longitude);


            }
        } else {
            m_googleMap.setMyLocationEnabled(false);
            m_googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            mLastKnownLocation = null;

        }
    }

    public void  click() {

     Point p = new Point();
        LatLng geo = m_googleMap.getProjection().fromScreenLocation(p);
        System.out.println(geo.toString());


        MainActivity newActivity = ((MainActivity)getActivity()).newQuery(geo.latitude, geo.longitude);




    }
    public void setTemp(String temps)
    {
        TextView tempView = (TextView)getActivity().findViewById(R.id.temp);
        tempView.setText(temps);
    }


    public void setText(String text)
   {
       TextView setText = (TextView)getActivity().findViewById(R.id.cityAndState);
       setText.setText(text);
   }
    private void buildGoogleAPi() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())


                .addApi(LocationServices.API)

                .build();
        mGoogleApiClient.connect();
      //  Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
    }

    public void getDeviceLocation() {
    /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */

        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            m_googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            m_googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");

            m_googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            m_googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        }
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            click();
            return true;
        }
        return false;
    }


    public class GetLayersTaskCallback implements AerisAmpOnGetLayersTaskCompleted {
        public GetLayersTaskCallback() {
        }

        public void onAerisAmpGetLayersTaskCompleted(ArrayList<AerisAmpLayer> permissibleLayers,
                                                     AerisPermissions permissions) {
            m_isAmpReady = true;

            if (m_isMapReady) {

                initMap();
            }
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //check for permissions
        if ((ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestMultiplePermissions(m_inflater, m_container, savedInstanceState);
        } else {
            m_aerisMapView.getMapAsync(this);
        }
    }

    private void requestMultiplePermissions(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        String readExternalPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int hasLocPermission = ContextCompat.checkSelfPermission(getActivity(), locationPermission);
        int hasReadPermission = ContextCompat.checkSelfPermission(getActivity(), readExternalPermission);
        List<String> permissions = new ArrayList<String>();

        if (hasLocPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(locationPermission);
        }

        if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(readExternalPermission);
        }

        if (!permissions.isEmpty()) {
            String[] params = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(getActivity(), params, REQUEST_PERMISSIONS);
        } else {
            // We already have permission, so handle as normal
            m_aerisMapView.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    m_aerisMapView.getMapAsync(this);
                } else {
                    Toast.makeText(getActivity(), R.string.permissions_verbiage,
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
     @Override
    public void onCameraMove() {
        cross.setOnTouchListener(this);
        click();
    }

    /**
     * Initializes the map with specific settings
     */
    private void initMap() {



        m_aerisMapView.setUseMapOptions(true);

        setHasOptionsMenu(true);

        //create a new MapOptions obj
        m_mapOptions = new AerisMapOptions();
        m_aerisAmp.getPermissibleLayers(false);
//        m_aerisMapView.onCameraMove();
        //set the mapOptions class's AerisAMP obj
        m_mapOptions.setAerisAMP(m_aerisAmp);

        if (!m_mapOptions.getMapPreferences(getActivity())) {
            //set default layers/data
            m_mapOptions.setDefaultAmpLayers();
            m_mapOptions.setPointData(AerisPointData.NONE);
            m_mapOptions.setPolygonData(AerisPolygonData.NONE);

            //save the map options
            m_mapOptions.saveMapPreferences(getActivity());
        }

        m_aerisMapView.getMap().setMapType(m_mapOptions.getMapType());



        AerisAmp aerisAmp = m_mapOptions.getAerisAMP();
        if (aerisAmp.getActiveMapLayers().size() < 1) {
          //  aerisAmp.setDefaultLayers();
           // aerisAmp.setLayerFromName("satellite-vis");
        }
        m_aerisMapView.addLayer(aerisAmp);

       m_googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //point data
        m_aerisMapView.addLayer(m_mapOptions.getPointData());

        //polygons
        m_aerisMapView.addLayer(m_mapOptions.getPolygon());
     // onCameraMove();


        //  m_googleMap.setOnCameraMoveCanceledListener(this);
        m_aerisMapView.hideAnimationButton();

        updateLocationUI();
        getDeviceLocation();

    }

    @Override
    public void onResume() {
        super.onResume();

        //we are resuming the map view, so check for updated options
        if (m_aerisMapView != null) {
            if (m_mapOptions != null) {
                m_mapOptions.getMapPreferences(getActivity());

                m_aerisMapView.getMap().setMapType(m_mapOptions.getMapType());

                m_aerisMapView.addLayer(m_mapOptions.getAerisAMP());
                m_aerisMapView.addLayer(m_mapOptions.getPointData());
                m_aerisMapView.addLayer(m_mapOptions.getPolygon());
            }

            //tell the map to redraw itself
            m_aerisMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        m_aerisMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        m_aerisMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        m_aerisMapView.onLowMemory();
    }


    @Override
    public void onMapLongClick(double lat, double longitude) {
        ParameterBuilder builder = new ParameterBuilder().withFields(
                ObservationFields.ICON, ObservationFields.TEMP_C,
                ObservationFields.TEMP_F, Fields.RELATIVE_TO);
        ObservationsTask task = new ObservationsTask(getActivity(), this);
        task.requestClosest(new PlaceParameter(lat, longitude), builder.build());
    }

    @Override
    public void onResult(EndpointType type, AerisResponse response) {
        if (type == EndpointType.OBSERVATIONS) {
            if (response.isSuccessfulWithResponses()) {
                ObservationResponse obResponse = new ObservationResponse(response.getFirstResponse());
                Observation ob = obResponse.getObservation();
                RelativeTo relativeTo = obResponse.getRelativeTo();
                if (m_marker != null) {
                    m_marker.remove();
                }


            }
        }
    }

    @Override
    public void earthquakeWindowPressed(EarthquakesResponse response, AerisMarker marker) {
        // do something with the response data.
        Toast.makeText(getActivity(), "Earthquake pressed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stormReportsWindowPressed(StormReportsResponse response, AerisMarker marker) {
        // do something with the response data.
        Toast.makeText(getActivity(), "Storm Report pressed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stormCellsWindowPressed(StormCellResponse response, AerisMarker marker) {
        // do something with the response data.
        Toast.makeText(getActivity(), "Storm Cell pressed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void wildfireWindowPressed(FiresResponse response, AerisMarker marker) {
        // do something with the response data.
        Toast.makeText(getActivity(), "Wildfire pressed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void recordsWindowPressed(RecordsResponse response, AerisMarker marker) {
        // do something with the response data.
        Toast.makeText(getActivity(), "Daily Record pressed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onObservationsFailed(AerisError arg0) {
        // do something with the response
        Toast.makeText(getActivity(), "Failed to load observation at that point", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onObservationsLoaded(List<ObservationResponse> responses) {
        ObservationResponse obResponse = responses.get(0);
        Observation ob = obResponse.getObservation();
        RelativeTo relativeTo = obResponse.getRelativeTo();

        if (m_marker != null) {
            m_marker.remove();
        }


    }



}