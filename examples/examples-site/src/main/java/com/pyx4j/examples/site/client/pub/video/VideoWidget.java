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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pyx4j.essentials.gwt2swf.FlowplayerWidget;
import com.pyx4j.site.client.InlineWidget;

public class VideoWidget extends VerticalPanel implements InlineWidget {

    PlayVideoDialog dialog;

    String videoURL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

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

        final FlowplayerWidget video = new FlowplayerWidget(267, 200);
        video.allowFullscreen();
        videoPanel.add(video);

        HorizontalPanel videoButtonsPanel = new HorizontalPanel();
        videoPanel.add(videoButtonsPanel);

        final Anchor play = new Anchor("&nbsp;Load&nbsp;", true);
        videoButtonsPanel.add(play);
        play.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //video.player().setPlaylist(videoURL);
                video.player().addClip(videoURL, 0);
            }
        });

        final Anchor play2 = new Anchor("&nbsp;Play&nbsp;", true);
        videoButtonsPanel.add(play2);
        play2.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                video.play(videoURL);
            }
        });
        return videoPanel;
    }

    @Override
    public void populate(Map<String, String> args) {

    }

}
