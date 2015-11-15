package michrosoft.com.hackathon2015;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by davidalbers on 11/14/15.
 */
public class Invitation implements Parcelable {

    public ArrayList<Attendee> getAttendees() {
        return attendees;
    }

    private ArrayList<Attendee> attendees = new ArrayList<Attendee>();

    public boolean isAccepted() {
        return accepted;
    }

    public String getEvent_id() {
        return event_id;
    }
    public String event_id;

    public String getCondition_id() {
        return condition_id;
    }
    public String condition_id;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int status;

    public String getOwner_id() {
        return owner_id;
    }
    public String owner_id;

    public String getTitle() {
        return title;
    }
    public String title;

    public String getDescription() {
        return description;
    }
    public String description;

    public String getLocation() {
        return location;
    }
    public String location;

    public String getStart_time() {
        return start_time;
    }
    public String start_time;

    public String getResponse_deadline() {
        return response_deadline;
    }
    public String response_deadline;

    public String getMinimum_attendance() {
        return minimum_attendance;
    }

    public String minimum_attendance;
    public String getEvent_status() {
        return event_status;
    }

    public String event_status;
    public String getOwner_name() {
        return owner_name;
    }

    public String owner_name;
    public String getOwner_email() {
        return owner_email;
    }

    public String owner_email;
    public String getOwner_image() {
        return owner_image;
    }

    public String owner_image;
    public String getNumber_invited() {
        return number_invited;
    }

    public String number_invited;
    public String getNumber_attending() {
        return number_attending;
    }


    public String number_attending;

    private boolean accepted = false;
    public Invitation() {
        attendees.add(new Attendee("David Albers", "davidgeorgea@gmail.com"));
        attendees.add(new Attendee("Will Smith", "mink.cv@gmail.com"));
    }

    public Invitation(JSONObject jsObj) {
        try {
            event_id = jsObj.getString("event_id");
            condition_id = jsObj.getString("condition_id");
            status = Integer.parseInt(jsObj.getString("status"));
            owner_id = jsObj.getString("owner_id");
            title = jsObj.getString("title");
            description = jsObj.getString("description");
            location = jsObj.getString("location");
            start_time = parseWeirdServerTime(jsObj.getString("start_time"));
            response_deadline = parseWeirdServerTime(jsObj.getString("response_deadline"));
            minimum_attendance = jsObj.getString("minimum_attendance");
            event_status = jsObj.getString("event_status");
            owner_name = jsObj.getString("owner_name");
            owner_email = jsObj.getString("owner_email");
            owner_image = jsObj.getString("owner_image");
            number_invited = jsObj.getString("number_invited");
            number_attending = jsObj.getString("number_attending");
        }catch (Exception ex) {
            Log.e("Invitation", "error parsing json to invitation");
        }
    }

    public static String parseWeirdServerTime(String time) {
        time = time.substring(time.indexOf('T') + 1);
        if(time.charAt(0) == '0')
            time = time.substring(1);
        time = time.substring(0, time.indexOf('.')-3);
        Log.d("tag","got time " + time);
        return time;
    }

    protected Invitation(Parcel in) {
        if (in.readByte() == 0x01) {
            attendees = new ArrayList<Attendee>();
            in.readList(attendees, Attendee.class.getClassLoader());
        } else {
            attendees = null;
        }
        event_id = in.readString();
        condition_id = in.readString();
        status = in.readInt();
        owner_id = in.readString();
        title = in.readString();
        description = in.readString();
        location = in.readString();
        start_time = in.readString();
        response_deadline = in.readString();
        minimum_attendance = in.readString();
        event_status = in.readString();
        owner_name = in.readString();
        owner_email = in.readString();
        owner_image = in.readString();
        number_invited = in.readString();
        number_attending = in.readString();
        accepted = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (attendees == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(attendees);
        }
        dest.writeString(event_id);
        dest.writeString(condition_id);
        dest.writeInt(status);
        dest.writeString(owner_id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeString(start_time);
        dest.writeString(response_deadline);
        dest.writeString(minimum_attendance);
        dest.writeString(event_status);
        dest.writeString(owner_name);
        dest.writeString(owner_email);
        dest.writeString(owner_image);
        dest.writeString(number_invited);
        dest.writeString(number_attending);
        dest.writeByte((byte) (accepted ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Invitation> CREATOR = new Parcelable.Creator<Invitation>() {
        @Override
        public Invitation createFromParcel(Parcel in) {
            return new Invitation(in);
        }

        @Override
        public Invitation[] newArray(int size) {
            return new Invitation[size];
        }
    };
}