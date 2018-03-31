package weatherapp.barant2003.com.weatherapplications;


import android.app.Activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by baran_000 on 7/28/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class FlightPlanner extends Fragment implements OnMapReadyCallback{
    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private FragmentActivity mContext;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.flight_planner, container, false);
        mGeoDataClient = Places.getGeoDataClient(getActivity() , null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

       SupportMapFragment mapFragments = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.maps);
       mapFragments.getMapAsync(this);





        return view;



    }

    @Override
    public void onAttach(Activity activity)
    {
        mContext = (FragmentActivity) activity;


        super.onAttach(activity);

    }
    public static FlightPlanner newInstance()
    {
      FlightPlanner newFragment = new FlightPlanner();
        return newFragment;
    }
    public void onResume()
    {
        super.onResume();


       // MainActivity getTitle = new MainActivity().setActionBarTitle("Flight Planner");
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;





    }
}
