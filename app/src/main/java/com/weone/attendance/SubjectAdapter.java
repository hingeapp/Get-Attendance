package com.weone.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Aditya Shirole on 3/6/2015.
 */
public class SubjectAdapter extends ArrayAdapter<SubjectHolder>{

    protected Context mContext;
    protected ArrayList<SubjectHolder> subjects ;

    public SubjectAdapter(Context context, int resource, ArrayList<SubjectHolder> subjectsList) {
        super(context, resource, subjectsList);
        mContext = context;
        subjects = subjectsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.subject_item,null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.attendedLabel = (TextView) convertView.findViewById(R.id.attendedLabel);
            holder.conductedLabel = (TextView) convertView.findViewById(R.id.conductedLabel);
            holder.percentLabel = (TextView) convertView.findViewById(R.id.percentLabel);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        SubjectHolder subject = subjects.get(position);

        holder.nameLabel.setText( subject.getSubjectName() );
        holder.attendedLabel.setText(subject.getAttendedLectures() + " / ");
        holder.conductedLabel.setText(subject.getConductedLectures());
        holder.percentLabel.setText(subject.getPercentAttendance().substring(0,2)+" %");
        return convertView;
    }


    public static class ViewHolder {
        TextView nameLabel;
        TextView conductedLabel;
        TextView attendedLabel;
        TextView percentLabel;
    }
}
