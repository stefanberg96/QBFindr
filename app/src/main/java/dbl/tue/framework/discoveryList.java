package dbl.tue.framework;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class discoveryList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] text;
   // private final Integer[] imageId; //images are integer;
    public discoveryList(Activity context,
                      String[] text) {
        super(context, R.layout.list_single_discovery, text);
        this.context = context;
        this.text = text;



    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single_discovery, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(text[position]);

        //imageView.setImageResource(imageId[position]);
        return rowView;
    }
}