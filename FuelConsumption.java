package weatherapp.barant2003.com.weatherapplications;

import android.app.ActionBar;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

/**
 * Created by baran_000 on 7/27/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class FuelConsumption extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View view = inflater.inflate(R.layout.fuel_consumption, container, false);

        return view;



    }

    public static FuelConsumption newInstance()
    {
        FuelConsumption newFragment = new FuelConsumption();
        return newFragment;
    }
    public void onResume()
    {
        super.onResume();


        MainActivity getTitle = ((MainActivity)getActivity()).setActionBarTitle("Fuel Consumption");
    }

}
