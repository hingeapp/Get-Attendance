package com.weone.attendance;

import android.content.Context;
import android.util.Log;
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

        holder.percentLabel.setTextColor(mContext.getResources().getColor(android.R.color.black));
        holder.nameLabel.setText( subject.getSubjectName() );
        holder.attendedLabel.setText(subject.getAttendedLectures() + " / ");
        holder.conductedLabel.setText(subject.getConductedLectures());

        String percent = null;

        if(subject.getPercentAttendance().length() > 2) {
            percent = subject.getPercentAttendance().substring(0, 2);
        }


        if(percent == null){
            percent = "0";
        }
        else if(percent.equalsIgnoreCase("0"))
        {

        }
        else {
            if (subject.getPercentAttendance().length() == 2) {
                /**
                 * No decimal in attendance. eg- 78 or 88 & it is 2 digit
                 */
                if (Integer.parseInt(percent) < 75) {
                    holder.percentLabel.setTextColor(mContext.getResources().getColor(R.color.red));
                }
            } else if (subject.getPercentAttendance().length() > 2) {
                /**
                 * Length is more than 2. Could be 3 digit number or a 2 digit with decimal eg- 99.1 or 100
                 */
                if (subject.getPercentAttendance().charAt(2) == '0') {
                    percent = "100";
                } else if (subject.getPercentAttendance().charAt(2) == '.') {
                    /**
                     * Confirm if it is indeed a 2 digit number
                     */
                    if (Integer.parseInt(percent) < 75) {
                        holder.percentLabel.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                }
            } else if (subject.getPercentAttendance().charAt(1) == '.' || subject.getPercentAttendance().length() == 1) {
                /**
                 * Attendance is single digit. eg - 1.1 or 2
                 */
                holder.percentLabel.setTextColor(mContext.getResources().getColor(R.color.red));
            }
        }


        holder.percentLabel.setText(percent +" %");
        return convertView;
    }


    public static class ViewHolder {
        TextView nameLabel;
        TextView conductedLabel;
        TextView attendedLabel;
        TextView percentLabel;
    }
}
