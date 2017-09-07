package gci16.gci16mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import gci16.gci16mobile.Assignment;
import gci16.gci16mobile.R;

/**
 * Created by Riccardo on 07/09/2017.
 */

public class AssignmentListAdapter extends ArrayAdapter<Assignment> {
    public AssignmentListAdapter(android.content.Context context, ArrayList<Assignment> list) {
        super(context, R.layout.list_text_view, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Context context = getContext();
        LayoutParams params;
        LinearLayout row = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.assignment_table_row, parent, false);
        Assignment a = getItem(position);
        TextView text = new TextView(context);
        params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
        text.setLayoutParams(params);
        text.setBackgroundResource(R.drawable.border);
        text.setText(Integer.toString(a.getMeterId()));
        row.addView(text);
        text = new TextView(context);
        params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 5f);
        text.setLayoutParams(params);
        text.setBackgroundResource(R.drawable.border);
        text.setText(a.getAddress());
        row.addView(text);
        text = new TextView(context);
        params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 3f);
        text.setLayoutParams(params);
        text.setBackgroundResource(R.drawable.border);
        text.setText(a.getCustomer());
        row.addView(text);
        return row;
    }

}