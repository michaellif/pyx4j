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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;

public class ImageSlider extends LayoutPanel implements IWidget {

    public interface ImageSetDataProvider {

        List<String> getImageUrls();

        Image getPlaceholder();

        void editImageSet();

    }

    private Dimension imageSize;

    private final Slideshow slideshow;

    private final EditorControlPanel editControl;

    private boolean editable;

    private final ImageSetDataProvider imageList;

    public ImageSlider(Dimension dimension, ImageSetDataProvider imageList) {
        this.imageList = imageList;
        this.editable = false;

        slideshow = new Slideshow(0, false);
        editControl = new EditorControlPanel();

        add(slideshow);
        add(editControl);
        setWidgetBottomHeight(editControl, 20, Unit.PCT, 40, Unit.PX);

        setImageSize(dimension.width, dimension.width);
    }

    public void showEditControl(boolean flag) {
        editControl.setVisible(flag);
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
            super("Edit");
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
                    imageList.editImageSet();
                    setVisible(false);
                }
            });
        }
    }

}