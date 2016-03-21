package dbl.tue.framework;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.net.URISyntaxException;


public class RecentChatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll= (LinearLayout) inflater.inflate(R.layout.recent_chats_fragment,null);


        Button button=(Button)ll.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getApplicationContext();
                Intent intent= new Intent(getActivity().getApplicationContext(),ChatActivity.class);
                startActivity(intent);
            }
        });
        return ll;
    }

}