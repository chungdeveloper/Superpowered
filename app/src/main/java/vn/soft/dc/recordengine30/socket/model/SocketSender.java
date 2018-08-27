package vn.soft.dc.recordengine30.socket.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Le Duc Chung on 2018-08-27.
 * on project 'recordenginev3'
 */
public class SocketSender {
    public static final String LOGIN = "login";
    public static final String JOIN = "join";
    public static final String SEEK = "seek";
    @SerializedName("cmd")
    private String cmd;
    @SerializedName("channel")
    private String channel;
    @SerializedName("message")
    private String message;
    @SerializedName("id")
    private String id;

    public SocketSender(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
