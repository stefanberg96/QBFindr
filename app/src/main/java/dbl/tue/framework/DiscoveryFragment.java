package dbl.tue.framework;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.qb.gson.Gson;
import com.qb.gson.GsonBuilder;
import com.qb.gson.reflect.TypeToken;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.server.BaseService;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;
import com.quickblox.location.request.SortField;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DiscoveryFragment extends Fragment {
    DiscoveryFragment instance;
    PeopleInVicinity[] text = null;
    Gson gson = new GsonBuilder().serializeNulls().create();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        LocationManager mLocationManager = (LocationManager) instance.getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        final QBChatService chatService = QBChatService.getInstance();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        if(savedInstanceState!= null){
            text=(PeopleInVicinity[]) savedInstanceState.getSerializable("discoverylist_people");

        }


        if (text == null) {
            QBLocationRequestBuilder getLocationsBuilder = new QBLocationRequestBuilder();
            getLocationsBuilder.setLastOnly();
            final Location location = getLastKnownLocation(mLocationManager);
            getLocationsBuilder.setRadius(location.getLatitude(), location.getLongitude(), 0.5f);
            getLocationsBuilder.setSort(SortField.DISTANCE);
            QBLocations.getLocations(getLocationsBuilder, new QBEntityCallback<ArrayList<QBLocation>>() {
                @Override
                public void onSuccess(ArrayList<QBLocation> locations, Bundle params) {

                    text = new PeopleInVicinity[locations.size() - 1];
                    for (int i = 0; i < locations.size(); i++) {
                        if (!Globals.getInstance().getCurrentuser().getId().equals(locations.get(i).getUser().getId())) {
                            Location loc = new Location("");
                            loc.setLatitude(locations.get(i).getLatitude());
                            loc.setLongitude(locations.get(i).getLongitude());


                            double distance = loc.distanceTo(location);
                            PeopleInVicinity person = new PeopleInVicinity(locations.get(i).getUser(), distance);
                            text[i] = person;
                        }
                    }
                    ListView list;
                    final discoveryList adapter = new discoveryList(instance.getActivity(), text);
                    list = (ListView) instance.getActivity().findViewById(R.id.discoveryList);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            startChatactivity(adapter.getItems()[position].user);
                        }
                    });
                    adapter.notifyDataSetChanged(text);
                }

                @Override
                public void onError(QBResponseException errors) {

                }
            });
        }

        return inflater.inflate(R.layout.discovery_fragment, null);


    }

    private void startChatactivity(final QBUser opponent) {
        Intent intent = new Intent(this.getActivity(), ChatActivity.class);
        intent.putExtra("opponent", opponent);
        startActivity(intent);
    }


/** Code could be used to create private chat but wasn't necessarry
 * for a private chat you can just start sending messages
  */
//        QBUser currentuser=Globals.getInstance().getCurrentuser();
//        try {
//            currentuser.setPassword(BaseService.getBaseService().getToken());
//        } catch (BaseServiceException e) {
//            e.printStackTrace();
//        }
//        QBUser user=Globals.getInstance().getCurrentuser();
//        user.setPassword(Profile.getCurrentProfile().getId());
//        QBAuth.createSession(user,new QBEntityCallback<QBSession>() {
//            @Override
//            public void onSuccess(QBSession session, Bundle params) {
//                Log.v("LOGGEDIN", session.toString());
//
//                QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), null, new QBEntityCallback<QBUser>() {
//                    @Override
//                    public void onSuccess(QBUser user, Bundle bundle) {
//                        // success
//                        user.setPassword(Profile.getCurrentProfile().getId());
//                        QBChatService.getInstance().login(user, new QBEntityCallback<Void>() {
//                            @Override
//                            public void onSuccess(Void result, Bundle bundle) {
//                                Log.v("TEST: ", "Yippy");
//                                final QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
//                                privateChatManager.createDialog(opponent.getId(), new QBEntityCallback<QBDialog>() {
//                                    @Override
//                                    public void onSuccess(QBDialog dialog, Bundle args) {
//                                        Log.v("TEST: ", dialog.toString());
//                                    }
//
//                                    @Override
//                                    public void onError(QBResponseException errors) {
//                                        Toast.makeText(getContext(), errors.toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onError(QBResponseException errors) {
//
//
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onError(QBResponseException e) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onError(QBResponseException error) {
//                // errors
//            }
//        });
//    }





    //Use onSaveInstanceState(Bundle) and onRestoreInstanceState

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(text!= null) {
            savedInstanceState.putSerializable("discoverylist_people", text);
        }


    }


    private Location getLastKnownLocation(LocationManager mLocationManager) {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    public class test{
        int k;
        test j;
        test(int k, test j){
            this.k=k;
            this.j=j;
        }
    }
}