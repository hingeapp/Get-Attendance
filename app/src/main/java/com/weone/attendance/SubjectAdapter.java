package com.weone.attendance;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ryanharter.android.tooltips.ToolTip;
import com.ryanharter.android.tooltips.ToolTipLayout;
import com.ryanharter.android.tooltips.ToolTip.Builder;
import java.util.ArrayList;

/**
 * Created by Aditya Shirole on 3/6/2015.
 */
public class SubjectAdapter extends ArrayAdapter<SubjectHolder>{

    protected Context mContext;
    protected ArrayList<SubjectHolder> subjects ;
    private static final int POINTER_SIZE = 15;

    public SubjectAdapter(Context context, int resource, ArrayList<SubjectHolder> subjectsList) {
        super(context, resource, subjectsList);
        mContext = context;
        subjects = subjectsList;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.subject_item,null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.attendedLabel = (TextView) convertView.findViewById(R.id.attendedLabel);
            holder.conductedLabel = (TextView) convertView.findViewById(R.id.conductedLabel);
            holder.percentLabel = (TextView) convertView.findViewById(R.id.percentLabel);
//            holder.tipContainer=(ToolTipLayout) convertView.findViewById(R.id.tooltip_container);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        SubjectHolder subject = subjects.get(position);
        holder.percentLabel.setTextColor(mContext.getResources().getColor(android.R.color.black));
//        holder.nameLabel.setText( subject.getSubjectName() );
        holder.nameLabel.setText(subject.getSubjectName());
        holder.attendedLabel.setText(subject.getAttendedLectures() + " / ");
        holder.conductedLabel.setText(subject.getConductedLectures());
        String percent = subject.getPercentAttendance();
//        holder.nameLabel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View contentView = createToolTipView(subjects.get(position).getFullSubjectName(),
//                        Color.RED, getContext().getResources().getColor(android.R.color.holo_orange_light));
//                contentView.setLayoutParams(new LayoutParams(
//                        LayoutParams.WRAP_CONTENT,
//                        LayoutParams.WRAP_CONTENT
//                ));
//
//
//                ToolTip t = new Builder(getContext().getApplicationContext())
//                        .anchor(holder.nameLabel)
//                        .color(getContext().getResources().getColor(android.R.color.holo_orange_light))
//                        .gravity(Gravity.BOTTOM)
//                        .pointerSize(POINTER_SIZE)
//                        .contentView(contentView)
//                        .build();
//                holder.tipContainer.addTooltip(t);
//            }
//        });
        if(percent.length() > 2) {
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
    public View createToolTipView(String text, int textColor, int bgColor) {
        float density = getContext().getResources().getDisplayMetrics().density;
        int padding = (int) (8 * density);
        TextView contentView = new TextView(getContext().getApplicationContext());
        contentView.setPadding(padding, padding, padding, padding);
        contentView.setText(text);
        contentView.setTextColor(textColor);
        contentView.setBackgroundColor(bgColor);
        return contentView;
    }

    public static class ViewHolder {
        TextView nameLabel;
        TextView conductedLabel;
        TextView attendedLabel;
        TextView percentLabel;
    }
}
