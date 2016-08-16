package com.weone.attendance;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aditya Shirole on 3/6/2015.
 */

public class SubjectHolder implements Parcelable{
    public String subjectName;
    public String FullSubjectName;
    public String conductedLectures;
    public String attendedLectures;
    public String percentAttendance;


    public SubjectHolder() {
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getConductedLectures() {
        return conductedLectures;
    }

    public String getFullSubjectName() {
        return FullSubjectName;
    }
    public void setFullSubjectName(String fullSubjectName) {
        this.FullSubjectName = fullSubjectName;
    }

    public void setConductedLectures(String conductedLectures) {
        this.conductedLectures = conductedLectures;
    }

    public String getAttendedLectures() {
        return attendedLectures;
    }

    public void setAttendedLectures(String attendedLectures) {
        this.attendedLectures = attendedLectures;
    }

    public String getPercentAttendance() {
        return percentAttendance;
    }

    public void setPercentAttendance(String percentAttendance) {
        this.percentAttendance = percentAttendance;
    }

    protected SubjectHolder(Parcel in) {
        subjectName = in.readString();
        FullSubjectName=in.readString();
        conductedLectures = in.readString();
        attendedLectures = in.readString();
        percentAttendance = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subjectName);
        dest.writeString(FullSubjectName);
        dest.writeString(conductedLectures);
        dest.writeString(attendedLectures);
        dest.writeString(percentAttendance);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SubjectHolder> CREATOR = new Parcelable.Creator<SubjectHolder>() {
        @Override
        public SubjectHolder createFromParcel(Parcel in) {
            return new SubjectHolder(in);
        }

        @Override
        public SubjectHolder[] newArray(int size) {
            return new SubjectHolder[size];
        }
    };
}
