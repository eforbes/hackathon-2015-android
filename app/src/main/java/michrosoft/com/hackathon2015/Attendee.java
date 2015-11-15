package michrosoft.com.hackathon2015;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by davidalbers on 11/14/15.
 */
public class Attendee implements Parcelable {
    public String getName() {
        return name;
    }

    String name;

    public String getEmail() {
        return email;
    }

    String email;

    public Attendee(String name, String email,int id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    protected Attendee(Parcel in) {
        name = in.readString();
        email = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Attendee> CREATOR = new Parcelable.Creator<Attendee>() {
        @Override
        public Attendee createFromParcel(Parcel in) {
            return new Attendee(in);
        }

        @Override
        public Attendee[] newArray(int size) {
            return new Attendee[size];
        }
    };

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Attendee) {
            if (((Attendee) obj).getId() == id)
                return true;
            return false;
        }
        return false;
    }
}