/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-04-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.gwt2swf;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * YouTube player API
 * 
 * @see http://code.google.com/apis/youtube/js_api_reference.html
 */
public class YouTubePlayer extends JavaScriptObject {

    //getPlayerState unstarted (-1), ended (0), playing (1), paused (2), buffering (3), video cued (5).
    public static enum PlayerState {

        UNSTARTED,

        STOPPED,

        PLAYING,

        PAUSED,

        BUFFERING,

        CUED;

        private static PlayerState getPlayerState(int nativeValue) {
            switch (nativeValue) {
            case -1:
                return UNSTARTED;
            case 0:
                return STOPPED;
            case 1:
                return PLAYING;
            case 2:
                return PAUSED;
            case 3:
                return BUFFERING;
            case 5:
                return CUED;
            default:
                return UNSTARTED;
            }
        }
    }

    protected YouTubePlayer() {
    }

    public static final native YouTubePlayer create(String playerId)
    /*-{
        return $wnd.document.getElementById(playerId);
    }-*/;

    public final native void loadVideoById(String videoId)
    /*-{
        this.loadVideoById(videoId);
    }-*/;

    public final native void loadVideoById(String videoId, int startSeconds)
    /*-{
        this.loadVideoById(videoId, startSeconds);
    }-*/;

    public final native void play()
    /*-{
        this.playVideo();
    }-*/;

    public final native void pause()
    /*-{
        this.pauseVideo();
    }-*/;

    public final native void stop()
    /*-{
        this.stopVideo();
    }-*/;

    public final native void clear()
    /*-{
        this.clearVideo();
    }-*/;

    public final native void setSize(int width, int height)
    /*-{
        this.setSize(width, height);
    }-*/;

    public final native void setVolume(int volume)
    /*-{
        this.setVolume(volume);
    }-*/;

    public final native double getVolume()
    /*-{
        return this.getVolume();
    }-*/;

    public final native void mute()
    /*-{
        this.mute();
    }-*/;

    public final native void unMute()
    /*-{
        this.unMute();
    }-*/;

    public final native boolean isMuted()
    /*-{
        return this.isMuted();
    }-*/;

    public final native double getCurrentTime()
    /*-{
        return this.getCurrentTime();
    }-*/;

    public final native void seekTo(double seconds, boolean allowSeekAhead)
    /*-{
        this.seekTo(seconds, allowSeekAhead);
    }-*/;

    private final native int getPlayerStateInt()
    /*-{
        return this.getCurrentTime();
    }-*/;

    public final PlayerState getPlayerState() {
        return PlayerState.getPlayerState(getPlayerStateInt());
    }
}
