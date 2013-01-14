/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2012-12-27
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.shared.Dimension;

public class ImageHolder extends SimplePanel implements IWidget {

    public enum Type {
        single, multiple
    }

    public interface ImageDataProvider {

        List<String> getImageUrls();

        void editImage();

    }

    private final Dimension dimension;

    private final Slideshow slideshow;

    private final Type type;

    private final EditorControlPanel editControl;

    private boolean editable;

    private final ImageDataProvider imageList;

    public ImageHolder(Dimension dimension, Type type, ImageDataProvider imageList) {
        this.dimension = dimension;
        this.type = type;
        this.imageList = imageList;

        LayoutPanel contentPanel = new LayoutPanel();

        contentPanel.setPixelSize(dimension.width, dimension.height);

        getElement().getStyle().setProperty("display", "inline-block");
        getElement().getStyle().setProperty("padding", "5px");
        getElement().getStyle().setProperty("border", "1px solid #999");

        slideshow = new Slideshow();
        editControl = new EditorControlPanel();
        editControl.setVisible(false);

        sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);

        addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                editControl.setVisible(true);
            }
        }, MouseOverEvent.getType());
        addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                editControl.setVisible(false);
            }
        }, MouseOutEvent.getType());

        contentPanel.add(slideshow);
        contentPanel.add(editControl);
        contentPanel.setWidgetTopHeight(editControl, 10, Unit.PCT, 40, Unit.PX);

        setWidget(contentPanel);
    }

    public void onModelChange() {
        for (String url : imageList.getImageUrls()) {
            final Image image = new Image(url);
            final ImageViewport imageViewport = new ImageViewport();
            image.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    imageViewport.scaleToFit();
                }
            });
            imageViewport.setImage(image);
            slideshow.addItem(imageViewport);
        }
    }

    public void reset() {
        onModelChange();
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    class EditorControlPanel extends Label {

        public EditorControlPanel() {
            super("Click to edit");
            getElement().getStyle().setProperty("width", "100%");
            getElement().getStyle().setProperty("lineHeight", "40px");
            getElement().getStyle().setProperty("textAlign", "center");
            getElement().getStyle().setProperty("background", "gray");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    imageList.editImage();
                    setVisible(false);
                }
            });
        }
    }

    class ImageViewport extends LayoutPanel {

        private Image image;

        void setImage(Image image) {
            this.image = image;
            add(image);
        }

        private void scaleToFit() {
            float frameRatio = (float) dimension.width / dimension.height;
            float imageRatio = (float) image.getWidth() / image.getHeight();
            if (imageRatio >= frameRatio) {
                setWidgetLeftRight(image, (dimension.width - image.getWidth() * dimension.height / image.getHeight()) / 2, Unit.PX, 0, Unit.PX);
                image.setSize("auto", "100%");
            } else {
                setWidgetTopBottom(image, (dimension.height - image.getHeight() * dimension.width / image.getWidth()) / 2, Unit.PX, 0, Unit.PX);
                image.setSize("100%", "auto");
            }
        }
    }

}