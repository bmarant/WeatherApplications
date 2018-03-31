package weatherapp.barant2003.com.weatherapplications;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by baran_000 on 7/28/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class FlightHistory extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.flight_history, container, false);

        return view;



    }

    public static FlightHistory newInstance()
    {
        FlightHistory newFragment = new FlightHistory();
        return newFragment;
    }
    public void onResume()
    {
        super.onResume();


        MainActivity getTitle = ((MainActivity)getActivity()).setActionBarTitle("Flight History Data");
    }


}
