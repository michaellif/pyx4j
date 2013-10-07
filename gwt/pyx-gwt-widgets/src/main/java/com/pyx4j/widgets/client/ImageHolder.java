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
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.widgets.client.ImageHolder.ImageViewport.ScaleMode;

public class ImageHolder extends LayoutPanel implements IWidget {

    public interface ImageDataProvider {

        List<String> getImageUrls();

        Image getPlaceholder();

        void editImage();

    }

    private Dimension imageSize;

    private ImageViewport placeholder;

    private final Slideshow slideshow;

    private final EditorControlPanel editControl;

    private boolean editable;

    private final ImageDataProvider imageList;

    public ImageHolder(Dimension dimension, ImageDataProvider imageList) {
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

        setImageSize(dimension.width, dimension.width);
    }

    public void onModelChange() {
        slideshow.removeAllItems();
        if (imageList.getImageUrls().size() == 0) {
            // set placeholder image
            final ImageViewport imageViewport = new ImageViewport(imageSize, ScaleMode.ScaleToFill);
            imageViewport.setImage(imageList.getPlaceholder());
            slideshow.addItem(imageViewport);
        } else {
            for (String url : imageList.getImageUrls()) {
                final ImageViewport imageViewport = new ImageViewport(imageSize, ScaleMode.ScaleToFill);
                imageViewport.setImage(new Image(url));
                slideshow.addItem(imageViewport);
            }
        }
        slideshow.show(slideshow.getItemCount() - 1, false);
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

        public enum ScaleMode {
            None, ScaleToFill, ScaleToFit;
        }

        private final Dimension dimension;

        private final ScaleMode scaleMode;

        public ImageViewport(Dimension dimension, ScaleMode scaleMode) {
            this.dimension = dimension;
            this.scaleMode = scaleMode;
            setPixelSize(dimension.width, dimension.height);
        }

        public void setImage(final Image img) {
            if (img == null) {
                return;
            }

            clear();
            add(img);
            img.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    adoptImage(img);
                }
            });
            img.addAttachHandler(new AttachEvent.Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    if (event.isAttached() && img.getWidth() > 0) {
                        adoptImage(img);
                    }
                }
            });

            // if widget has already been attached and image is loaded, call adopt() right away
            if (isAttached() && img.getWidth() > 0) {
                adoptImage(img);
            }
        }

        private static void adoptImage(Image img) {
            Widget parent = img.getParent();
            if (parent instanceof ImageViewport) {
                ((ImageViewport) parent).scale(img);
            }
        }

        private void scale(Image image) {
            if (image == null) {
                return;
            }

            float frameRatio = (float) dimension.width / dimension.height;
            float imageRatio = (float) image.getWidth() / image.getHeight();
            switch (scaleMode) {
            case ScaleToFill:
                if (imageRatio >= frameRatio) {
                    fitVertically(image);
                } else {
                    fitHorizontally(image);
                }
                break;
            case ScaleToFit:
                if (imageRatio >= frameRatio) {
                    fitHorizontally(image);
                } else {
                    fitVertically(image);
                }
                break;
            default:
                // no-op
            }
        }

        private void fitHorizontally(Image image) {
            // calculate top-bottom offset
            int ver = (dimension.height - image.getHeight() * dimension.width / image.getWidth()) / 2;
            setWidgetTopBottom(image, ver, Unit.PX, ver, Unit.PX);
            // scale to fill container's width
            image.getElement().getStyle().setProperty("width", "100%");
            image.getElement().getStyle().setProperty("backgroundSize", "100% auto");
        }

        private void fitVertically(Image image) {
            // calculate left-right offset
            int hor = (dimension.width - image.getWidth() * dimension.height / image.getHeight()) / 2;
            setWidgetLeftRight(image, hor, Unit.PX, hor, Unit.PX);
            // scale to fill container's height
            image.getElement().getStyle().setProperty("height", "100%");
            image.getElement().getStyle().setProperty("backgroundSize", "auto 100%");
        }
    }

}