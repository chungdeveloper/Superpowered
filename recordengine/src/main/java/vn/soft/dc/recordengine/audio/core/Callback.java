package vn.soft.dc.recordengine.audio.core;

public interface Callback {
    void onBufferAvailable(byte[] buffer);
}