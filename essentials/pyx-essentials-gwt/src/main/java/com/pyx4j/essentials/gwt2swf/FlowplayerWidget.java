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
 * Created on 2010-04-15
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.gwt2swf;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public class FlowplayerWidget extends ExtSWFWidget {

    private static final Logger log = LoggerFactory.getLogger(FlowplayerWidget.class);

    private static final Map<String, FlowplayerWidget> instances = new HashMap<String, FlowplayerWidget>();

    private Player player;

    private StringBuilder config;

    private boolean autoPlay;

    static {
        registerCallbacks();
    }

    public FlowplayerWidget(int width, int height) {
        this(GWT.getModuleBaseURL() + "flowplayer.swf", width, height);
    }

    /**
     * 
     * @param src
     *            "http://releases.flowplayer.org/swf/flowplayer-3.1.5.swf"
     * @param width
     * @param height
     */
    public FlowplayerWidget(String src, int width, int height) {
        super(src, width, height);
        allowScriptAccess();
    }

    @Override
    protected void onLoad() {
        log.debug("flowplayer onLoad");
        instances.put(super.getSwfId(), this);
        super.onLoad();
    }

    @Override
    protected void onUnload() {
        log.debug("flowplayer onUnload");
        if (isLoaded()) {
            player().close();
            player = null;
        }
        super.onUnload();
        instances.remove(super.getSwfId());
    }

    private StringBuilder q(Object text) {
        config.append('\'').append(text).append('\'');
        return config;
    }

    public void addClipParam(String clipUrl) {
        if (config == null) {
            config = new StringBuilder();
        }
        config.append(',');
        q("clip").append(":{");

        q("url").append(':');
        q(clipUrl);

        config.append('}');
    }

    public void addClipParam(String clipUrl, boolean autoPlay, boolean autoBuffering) {
        addClipParam(clipUrl, autoPlay, autoBuffering, "orig");
    }

    public void addClipParam(String clipUrl, boolean autoPlay, boolean autoBuffering, String scaling) {
        if (config == null) {
            config = new StringBuilder();
        }
        config.append(',');
        q("clip").append(":{");

        if (clipUrl != null) {
            q("url").append(":");
            q(clipUrl);
            config.append(',');
        }

        q("autoPlay").append(":");
        q(autoPlay);
        config.append(',');

        q("autoBuffering").append(":");
        q(autoBuffering);

        if (scaling != null) {
            config.append(',');

            q("scaling").append(":");
            q(scaling);
        }

        config.append("}");

    }

    @Override
    protected void onBeforeSWFInjection() {
        this.addFlashVar("config", "{'playerId':'" + super.getSwfId() + "'" + ((config != null) ? config.toString() : "") + "}");
    }

    private static native void registerCallbacks()
    /*-{
         $wnd.flowplayer = function() {};
         $wnd.flowplayer.fireEvent = function(playerId, eventName, arg1, arg2, arg3) {
               @com.pyx4j.essentials.gwt2swf.FlowplayerWidget::flowplayerEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(playerId, eventName, arg1, arg2, arg3);
         };
    }-*/;

    private static void flowplayerEvent(String playerApiId, String eventName, String arg1, String arg2, String arg3) {
        try {
            FlowplayerWidget p = instances.get(playerApiId);
            if (p != null) {
                if ("onLoad" == eventName) {
                    log.debug("flowplayer load event [{}] {}", eventName, arg1);
                    if ("player" == arg1) {
                        p.onFlowplayerLoad();
                    }
                } else if ("onError" == eventName) {
                    p.onFlowplayerError(arg1, arg2);
                } else {
                    p.onFlowplayerEvent(eventName, arg1, arg2, arg3);
                }
            } else {
                log.warn("unbound flowplayer event [{}] {}", playerApiId, eventName);
            }
        } catch (Throwable t) {
            log.error("handler error", t);
        }
    }

    public static class Player extends JavaScriptObject {

        protected Player() {
        }

        public static final native Player create(String playerId)
        /*-{
            return $wnd.document.getElementById(playerId);
        }-*/;

        public final native void play(String videoUrl)
        /*-{
            this.fp_play(videoUrl);
        }-*/;

        public final native void play()
        /*-{
            this.fp_play();
        }-*/;

        public final native void stop()
        /*-{
            this.fp_stop();
        }-*/;

        public final native void pause()
        /*-{
            this.fp_pause();
        }-*/;

        public final native void close()
        /*-{
            this.fp_close();
        }-*/;

        public final native boolean isFullscreen()
        /*-{
            return this.fp_isFullscreen();
        }-*/;

        public final native void addClip(String videoUrl, int index)
        /*-{
            this.fp_addClip(videoUrl, index);
        }-*/;

        /**
         * TODO This does not work!
         * 
         * @param videoUrl
         */
        public final native void setPlaylist(String videoUrl)
        /*-{
            var pl = { url: videoUrl, autoPlay: false, autoBuffering: false };
            this.fp_setPlaylist([pl]);
        }-*/;
    }

    public Player player() {
        if (player == null) {
            player = Player.create(super.getSwfId());
        }
        if (player == null) {
            throw new RuntimeException("Flowplayer not loaded");
        }
        return player;
    }

    private void onFlowplayerLoad() {
        if (player == null) {
            player = Player.create(super.getSwfId());
            if (player != null) {
                log.debug("flowplayer {} ready", super.getSwfId());
                onReady();
            }
        }
    }

    public boolean isLoaded() {
        return (player != null);
    }

    protected void onReady() {

    }

    /**
     * @param eventName
     *            [onConnect, onBegin, onStart, onBufferFull, onLastSecond,
     *            onBeforeFinish, onFinish, onStop, onFullscreenExit]
     */
    protected void onFlowplayerEvent(String eventName, String arg1, String arg2, String arg3) {

    }

    protected void onFlowplayerError(String code, String errorMessage) {
        log.error("flowplayer error [{}] {}", code, errorMessage);
    }

    public void play() {
        player().play();
    }

    public void play(String videoUrl) {
        player().play(videoUrl);
    }

    public void stop() {
        player().stop();
    }

    public void pause() {
        player().pause();
    }

    public void setClip(String videoUrl) {
        log.debug("flowplayer setClip {}", videoUrl);
        if (autoPlay) {
            player().play(videoUrl);
            autoPlay = false;
        } else {
            player().addClip(videoUrl, -1);
        }
    }

    /**
     * Would be reset on next setClip
     */
    public void setAutoPlay() {
        autoPlay = true;
    }
}
