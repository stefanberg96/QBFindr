package dbl.tue.framework;

import android.app.Activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.Profile;


public class discoveryList extends ArrayAdapter<String>{

    private final Activity context;
    private PeopleInVicinity[] items;
   // private final Integer[] imageId; //images are integer;
    public discoveryList(Activity context,
                      PeopleInVicinity[] items) {
        super(context, R.layout.list_single_discovery);
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount(){
        return items.length;
    }

    public PeopleInVicinity[] getItems() {
        return items;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single_discovery, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView subtxt= (TextView) rowView.findViewById(R.id.txtlower);
       // ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        Log.v("TESTING:", "I am HERE");
        txtTitle.setText(items[position].user.getFullName());
        subtxt.setText((int) items[position].distance+" m");

        //imageView.setImageResource(imageId[position]);
        return rowView;
    }


    public void notifyDataSetChanged(PeopleInVicinity[] items){
        this.items=items;
    }
}