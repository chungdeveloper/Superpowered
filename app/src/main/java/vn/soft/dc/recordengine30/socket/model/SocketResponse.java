package vn.soft.dc.recordengine30.socket.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Le Duc Chung on 2018-08-27.
 * on project 'recordenginev3'
 */
@SuppressWarnings("ALL")
public class SocketResponse implements Parcelable {
    @SerializedName("sender")
    @Expose
    private Object sender;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("emotion")
    @Expose
    private Object emotion;
    @SerializedName("date")
    @Expose
    private Object date;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("cmd")
    @Expose
    private String cmd;
    public final static Parcelable.Creator<SocketResponse> CREATOR = new Creator<SocketResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SocketResponse createFromParcel(Parcel in) {
            return new SocketResponse(in);
        }

        public SocketResponse[] newArray(int size) {
            return (new SocketResponse[size]);
        }

    };

    protected SocketResponse(Parcel in) {
        this.sender = ((Object) in.readValue((Object.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.emotion = ((Object) in.readValue((Object.class.getClassLoader())));
        this.date = ((Object) in.readValue((Object.class.getClassLoader())));
        this.status = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.cmd = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public SocketResponse() {
    }

    /**
     * @param message
     * @param sender
     * @param cmd
     * @param status
     * @param date
     * @param emotion
     */
    public SocketResponse(Object sender, String message, Object emotion, Object date, Integer status, String cmd) {
        super();
        this.sender = sender;
        this.message = message;
        this.emotion = emotion;
        this.date = date;
        this.status = status;
        this.cmd = cmd;
    }

    public Object getSender() {
        return sender;
    }

    public void setSender(Object sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getEmotion() {
        return emotion;
    }

    public void setEmotion(Object emotion) {
        this.emotion = emotion;
    }

    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(sender);
        dest.writeValue(message);
        dest.writeValue(emotion);
        dest.writeValue(date);
        dest.writeValue(status);
        dest.writeValue(cmd);
    }

    public int describeContents() {
        return 0;
    }

}
