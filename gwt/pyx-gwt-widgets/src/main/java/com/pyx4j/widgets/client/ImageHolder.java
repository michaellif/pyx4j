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

import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.widgets.client.ImageHolder.ImageViewport.Scale;

public class ImageHolder extends LayoutPanel implements IWidget {

    public interface ImageDataProvider {

        List<String> getImageUrls();

        void editImage();

    }

    private Dimension imageSize;

    private final Slideshow slideshow;

    private final EditorControlPanel editControl;

    private boolean editable;

    private final ImageDataProvider imageList;

    public ImageHolder(Dimension dimension, ImageDataProvider imageList) {
        this.imageSize = dimension;
        this.imageList = imageList;
        this.editable = false;

        slideshow = new Slideshow(0, false);
        editControl = new EditorControlPanel();
        editControl.setVisible(false);

        sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);

        addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                editControl.setVisible(isEditable());
            }
        }, MouseOverEvent.getType());
        addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                editControl.setVisible(false);
            }
        }, MouseOutEvent.getType());

        add(slideshow);
        add(editControl);
        setWidgetBottomHeight(editControl, 20, Unit.PCT, 40, Unit.PX);

        setPixelSize(imageSize.width, imageSize.height);
    }

    public void onModelChange() {
        slideshow.removeAllItems();
        for (String url : imageList.getImageUrls()) {
            final ImageViewport imageViewport = new ImageViewport(imageSize, Scale.ScaleToFill);
            imageViewport.setUrl(url);
            slideshow.addItem(imageViewport);
        }
        slideshow.show(slideshow.getItemCount() - 1);
    }

    public void reset() {
        onModelChange();
    }

    public void setImageSize(int width, int height) {
        imageSize = new Dimension(width, height);
        setPixelSize(imageSize.width, imageSize.height);
        reset();
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
            getElement().getStyle().setProperty("color", "white");
            getElement().getStyle().setProperty("opacity", "0.8");
            getElement().getStyle().setProperty("cursor", "pointer");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    imageList.editImage();
                    setVisible(false);
                }
            });
        }
    }

    public static class ImageViewport extends LayoutPanel {

        public enum Scale {
            None, ScaleToFill, ScaleToFit;
        }

        private Image image;

        private final Dimension dimension;

        private final Scale scale;

        public ImageViewport(Dimension dimension, Scale scale) {
            this.dimension = dimension;
            this.scale = scale;
            setSize(dimension.width + "px", dimension.height + "px");
            setImage(new Image());
        }

        public void setImage(Image image) {
            if (image == null) {
                return;
            }
            this.image = image;
            image.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    scale();
                }
            });
            insert(image, 0);
        }

        public void setUrl(String url) {
            image.setUrl(url);
        }

        private void scale() {
            float frameRatio = (float) dimension.width / dimension.height;
            float imageRatio = (float) image.getWidth() / image.getHeight();
            switch (scale) {
            case ScaleToFill:
                if (imageRatio >= frameRatio) {
                    fitVertically();
                } else {
                    fitHorizontally();
                }
                break;
            case ScaleToFit:
                if (imageRatio >= frameRatio) {
                    fitHorizontally();
                } else {
                    fitVertically();
                }
                break;
            default:
            }
        }

        private void fitHorizontally() {
            // set left-right = 0
            setWidgetLeftRight(image, 0, Unit.PX, 0, Unit.PX);
            // calculate top-bottom offset
            int ver = (dimension.height - image.getHeight() * dimension.width / image.getWidth()) / 2;
            setWidgetTopBottom(image, ver, Unit.PX, ver, Unit.PX);
            image.setSize("100%", "auto");
        }

        private void fitVertically() {
            // set top-bottom = 0
            setWidgetTopBottom(image, 0, Unit.PX, 0, Unit.PX);
            // calculate horizontal offset
            int hor = (dimension.width - image.getWidth() * dimension.height / image.getHeight()) / 2;
            setWidgetLeftRight(image, hor, Unit.PX, hor, Unit.PX);
            image.setSize("auto", "100%");
        }
    }

}