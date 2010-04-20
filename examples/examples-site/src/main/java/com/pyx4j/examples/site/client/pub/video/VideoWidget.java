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
 * Created on Apr 15, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.pub.video;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.essentials.gwt2swf.FlowplayerWidget;
import com.pyx4j.site.client.InlineWidget;

public class VideoWidget extends VerticalPanel implements InlineWidget {

    private static final Logger log = LoggerFactory.getLogger(VideoWidget.class);

    PlayVideoDialog dialog;

    String videoURL0 = "http://danila.skarzhevskyy.com/Danila%202010-03-16%20Pechataem.flv";

    String videoURL1 = "http://danila.skarzhevskyy.com/Danila%202010-02-04%20Pischim.flv";

    String videoURL2 = "http://danila.skarzhevskyy.com/Danila%202010-03-27%20Voice.flv";

    public VideoWidget() {
        dialog = new PlayVideoDialog();

        HorizontalPanel contectPanel = new HorizontalPanel();
        this.add(contectPanel);

        VerticalPanel ytPanel = new VerticalPanel();
        contectPanel.add(ytPanel);

        addYTVideo(ytPanel, "Typing", "R7nxlT7Zuzc");
        addYTVideo(ytPanel, "Multik", "qy_Ka5VKZnE");

        contectPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));

        contectPanel.add(addFlowplayer());
    }

    private void addYTVideo(VerticalPanel panel, final String name, final String videoId) {
        Anchor anchor = new Anchor(name);
        anchor.getElement().getStyle().setFontSize(1.2, Unit.EM);
        anchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dialog.title = name;
                dialog.videoId = videoId;
                dialog.show();
            }
        });
        DOM.appendChild(anchor.getElement(), new HTML("<p/>").getElement());
        Image img = new Image("http://i1.ytimg.com/vi/" + videoId + "/default.jpg");
        img.setSize("120px", "90px");
        DOM.appendChild(anchor.getElement(), img.getElement());
        panel.add(anchor);
        panel.add(new HTML("<br/>"));
    }

    private VerticalPanel addFlowplayer() {
        VerticalPanel videoPanel = new VerticalPanel();
        Style videoPanelStyle = videoPanel.getElement().getStyle();
        videoPanelStyle.setProperty("backgroundColor", "#F6F9FF");
        videoPanelStyle.setProperty("padding", "3px");
        videoPanelStyle.setProperty("border", "solid 1px");
        videoPanelStyle.setProperty("borderColor", "#E5ECF9");

        final FlowplayerWidget video = new FlowplayerWidget((300 - 25) * 16 / 9, 300) {

            @Override
            protected void onReady() {
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        setClip(videoURL0);
                    }
                });
            }

            @Override
            protected void onFlowplayerEvent(String eventName, String arg1, String arg2, String arg3) {
                log.debug("flowplayer event [{}] {}", eventName, arg1);
            }
        };
        video.allowFullscreen();
        video.addParam("wmode", "transparent");
        video.addClipParam(null, false, false);

        videoPanel.add(video);

        HorizontalPanel videoButtonsPanel = new HorizontalPanel();
        videoPanel.add(videoButtonsPanel);

        final Anchor load1 = new Anchor("&nbsp;Load Video 1&nbsp;", true);
        videoButtonsPanel.add(load1);
        load1.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                video.stop();
                video.player().close();
                video.play(videoURL1);
            }
        });

        videoButtonsPanel.add(new HTML("|"));

        final Anchor load2 = new Anchor("&nbsp;Load Video 2&nbsp;", true);
        videoButtonsPanel.add(load2);
        load2.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                video.stop();
                video.player().close();
                video.play(videoURL2);
            }
        });

        videoButtonsPanel.add(new HTML("|"));

        final Anchor play = new Anchor("&nbsp;Play&nbsp;", true);
        videoButtonsPanel.add(play);
        play.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                video.play();
            }
        });

        return videoPanel;
    }

    @Override
    public void populate(Map<String, String> args) {

    }

}
