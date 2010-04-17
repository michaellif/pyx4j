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

    static {
        registerCallbacks();
    }

    public FlowplayerWidget(int width, int height) {
        super(GWT.getModuleBaseURL() + "flowplayer.swf", width, height);
        this.addParam("allowScriptAccess", "always");
    }

    @Override
    protected void onLoad() {
        instances.put(super.getSwfId(), this);
        super.onLoad();
    }

    @Override
    protected void onUnload() {
        if (isLoaded()) {
            player().close();
        }
        super.onUnload();
        instances.remove(super.getSwfId());
    }

    public void allowFullscreen() {
        this.addParam("allowfullscreen", "true");
    }

    private StringBuilder q(Object text) {
        config.append('"').append(text).append('"');
        return config;
    }

    public void addClipParam(String clipUrl) {
        if (config == null) {
            config = new StringBuilder();
        }
        config.append(',');
        q("clip").append(":{");

        q("url").append(":");
        q(clipUrl);

        config.append("}");
    }

    @Override
    protected void onBeforeSWFInjection() {
        this.addFlashVar("config", "{\"playerId\":\"" + super.getSwfId() + "\"" + ((config != null) ? config.toString() : "") + "}");
    }

    private static native void registerCallbacks()
    /*-{
         $wnd.flowplayer = function() {};
         $wnd.flowplayer.fireEvent = function(playerId, eventName, arg1, arg2, arg3) {
               @com.pyx4j.essentials.gwt2swf.FlowplayerWidget::flowplayerEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(playerId, eventName, arg1, arg2, arg3);
         };
    }-*/;

    @SuppressWarnings("unused")
    private static void flowplayerEvent(String playerApiId, String eventName, String arg1, String arg2, String arg3) {
        FlowplayerWidget p = instances.get(playerApiId);
        if (p != null) {
            if ("onLoad" == eventName) {
                if ("player" == arg1) {
                    p.onFlowplayerLoad();
                }
            } else {
                p.onFlowplayerEvent(eventName, arg1, arg2, arg3);
            }
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
            this.fp_addClip({url:videoUrl},index);
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
            log.debug("flowplayer {} ready", super.getSwfId());
            onReady();
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

    public void play() {
        player().play();
    }

    public void play(String videoUrl) {
        player().play(videoUrl);
    }

    public void setClip(String videoUrl) {
        player().addClip(videoUrl, 0);
    }
}
