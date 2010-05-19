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
 * Created on May 19, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.photoalbum;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.widgets.client.HasTooltipMouseHandlers;
import com.pyx4j.widgets.client.Tooltip;

public class PhotoAlbum extends DockPanel {

    private final ActionPanel actionPanel;

    private final FlowPanel photoPanel;

    private final List<Photo> photoList = new ArrayList<Photo>();

    public PhotoAlbum() {
        actionPanel = new ActionPanel();
        add(actionPanel, DockPanel.NORTH);

        photoPanel = new FlowPanel();
        add(photoPanel, DockPanel.CENTER);

        photoPanel.add(new PhotoHolder());
        photoPanel.add(new PhotoHolder());
        photoPanel.add(new PhotoHolder());
        photoPanel.add(new PhotoHolder());
        photoPanel.add(new PhotoHolder());
    }

    public void addPhoto() {

    }

    class PhotoHolder extends AbsolutePanel {

        private final Image image;

        public PhotoHolder() {
            setSize("200px", "200px");
            getElement().getStyle().setMargin(2, Unit.PX);
            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            image = new Image("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG");
            image.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            image.getElement().getStyle().setProperty("textAlign", "center");
            image.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    // TODO Auto-generated method stub

                }
            });

            PhotoFrame frame = new PhotoFrame();
            Tooltip.tooltip(frame, "Tooltip");
            frame.getElement().getStyle().setBackgroundColor("lightgray");
            frame.setSize("180px", "180px");
            frame.add(image);
            frame.setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
            frame.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);

            add(frame, 10, 0);
        }

        class PhotoFrame extends HorizontalPanel implements HasTooltipMouseHandlers {

            @Override
            public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
                return addDomHandler(handler, MouseOutEvent.getType());
            }

            @Override
            public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
                return addDomHandler(handler, MouseOverEvent.getType());
            }

            @Override
            public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
                return addDomHandler(handler, MouseMoveEvent.getType());
            }

        }
    }

    class ActionPanel extends HorizontalPanel {

        private final Button slideshowButton;

        private final Button addPhotoButton;

        public ActionPanel() {
            slideshowButton = new Button("Slideshow");
            add(slideshowButton);
            addPhotoButton = new Button("Add photo");
            add(addPhotoButton);
        }
    }

    class Photo {

        private final String thumbnailUrl;

        private final String photoUrl;

        private final String caption;

        public Photo(String thumbnailUrl, String photoUrl, String caption) {
            super();
            this.thumbnailUrl = thumbnailUrl;
            this.photoUrl = photoUrl;
            this.caption = caption;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public String getCaption() {
            return caption;
        }

    }
}
