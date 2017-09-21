package gci16.gci16mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;


/**
 * Created by Riccardo on 07/09/2017.
 */

public class AssignmentListAdapter extends ArrayAdapter<Assignment> {
    public AssignmentListAdapter(android.content.Context context, List<Assignment> list) {
        super(context, R.layout.list_row_item, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Context context = getContext();
        View row = LayoutInflater.from(context).inflate(R.layout.assignment_table_row, parent, false);
        Assignment a = getItem(position);
        ((TextView) row.findViewById(R.id.meterID)).setText(Integer.toString(a.getMeterId()));
        ((TextView) row.findViewById(R.id.address)).setText(a.getAddress());
        ((TextView) row.findViewById(R.id.customer)).setText(a.getCustomer());
        return row;
    }
}