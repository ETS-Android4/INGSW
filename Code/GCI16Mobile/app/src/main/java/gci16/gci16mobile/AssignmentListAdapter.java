package gci16.gci16mobile;

import android.content.Context;
import android.view.Gravity;
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
        super(context, R.layout.list_row_item, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Context context = getContext();
        LinearLayout row = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.assignment_table_row, parent, false);
        Assignment a = getItem(position);
        LayoutParams cellParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2.5f);
        cellParams.gravity = Gravity.CENTER_VERTICAL;
        cellParams.leftMargin = 8;
        TextView text = new TextView(context);
        text.setText(Integer.toString(a.getMeterId()));
        text.setLayoutParams(cellParams);
        row.addView(text);
        text = new TextView(context);
        cellParams.weight = 5.5f;
        text.setText(a.getAddress());
        text.setLayoutParams(cellParams);
        row.addView(text);
        text = new TextView(context);
        cellParams.weight = 4f;
        text.setText(a.getCustomer());
        text.setLayoutParams(cellParams);
        row.addView(text);
        return row;
    }

}