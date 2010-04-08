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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YouTubePlayerWidget extends ExtSWFWidget {

    private static final Logger log = LoggerFactory.getLogger(YouTubePlayerWidget.class);

    private static final Map<String, YouTubePlayerWidget> instances = new HashMap<String, YouTubePlayerWidget>();

    private final String playerApiId;

    private YouTubePlayer control;

    static {
        registerCallbacks();
    }

    private static native void registerCallbacks()
    /*-{
         $wnd.onYouTubePlayerReady = function(playerApiId) {
          @com.pyx4j.essentials.gwt2swf.YouTubePlayerWidget::onReady(Ljava/lang/String;)(playerApiId);
         }
    }-*/;

    public YouTubePlayerWidget(String name, int width, int height) {
        super("http://www.youtube.com/apiplayer?enablejsapi=1&playerapiid=" + name, width, height);
        instances.put(name, this);
        this.addParam("allowScriptAccess", "always");
        playerApiId = name;
        log.debug("PlayerWidget {} created", playerApiId);
    }

    public String getPlayerApiId() {
        return playerApiId;
    }

    public YouTubePlayer getControl() {
        return control;
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        instances.remove(playerApiId);
    }

    @SuppressWarnings("unused")
    private static void onReady(String playerApiId) {
        YouTubePlayerWidget p = instances.get(playerApiId);
        if (p != null) {
            p.onReady();
        }
    }

    public boolean isReady() {
        return control != null;
    }

    protected void onReady() {
        control = YouTubePlayer.create(getSwfId());
        log.debug("PlayerWidget {} ready", playerApiId);
    }
}
