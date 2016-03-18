package dbl.tue.framework;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;
import com.quickblox.location.request.SortField;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;


public class DiscoveryFragment extends Fragment {
    DiscoveryFragment instance;
    String[] text=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        LocationManager mLocationManager = (LocationManager) instance.getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        if(savedInstanceState!= null){
            text=savedInstanceState.getStringArray("discoverylist_text");
        }
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, true));
        if(text==null) {
            QBLocationRequestBuilder getLocationsBuilder = new QBLocationRequestBuilder();
            getLocationsBuilder.setLastOnly();
            if (location == null) {
                location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), false));
            }
            getLocationsBuilder.setRadius(location.getLatitude(), location.getLongitude(), 1);
            getLocationsBuilder.setSort(SortField.DISTANCE);
            QBLocations.getLocations(getLocationsBuilder, new QBEntityCallback<ArrayList<QBLocation>>() {
                @Override
                public void onSuccess(ArrayList<QBLocation> locations, Bundle params) {

                    text = new String[locations.size() - 1];
                    for (int i = 0; i < locations.size(); i++) {
                        if (!Globals.getInstance().getCurrentuser().getId().equals(locations.get(i).getUser().getId())) {
                            text[i] = locations.get(i).getUser().getFullName();
                        }
                    }
                    ListView list;
                    discoveryList adapter = new discoveryList(instance.getActivity(), text);
                    list = (ListView) instance.getActivity().findViewById(R.id.discoveryList);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Toast.makeText(instance.getActivity(), "You Clicked at " + text[+position], Toast.LENGTH_SHORT).show();
                            
                        }
                    });

                }

                @Override
                public void onError(QBResponseException errors) {

                }
            });
        }

        return inflater.inflate(R.layout.discovery_fragment,null);



    }

    //Use onSaveInstanceState(Bundle) and onRestoreInstanceState

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putStringArray("discoverylist_text", text);
        savedInstanceState.putDouble("myDouble", 1.9);
        // etc.
        super.onSaveInstanceState(savedInstanceState);
    }

}