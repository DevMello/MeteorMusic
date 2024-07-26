package com.devmello.music.util;

public interface Texture {
    boolean hasAlpha();

    String getTextureRef();

    void bind();

    int getImageHeight();

    int getImageWidth();

    float getHeight();

    float getWidth();

    int getTextureHeight();

    int getTextureWidth();

    void release();

    int getTextureID();

    byte[] getTextureData();

    void setTextureFilter(int var1);
}
