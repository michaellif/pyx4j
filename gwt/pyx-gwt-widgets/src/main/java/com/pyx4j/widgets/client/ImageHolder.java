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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ImageHolder extends FlowPanel implements IWidget {

    public enum Type {
        single, multiple
    }

    public interface ImageDataProvider {

        List<String> getImageUrls();

        void editImage();

    }

    final Slideshow slideshow = new Slideshow(250, 250);

    private final Type type;

    private final EditorControlPanel editControls = new EditorControlPanel();

    private boolean editable;

    private final ImageDataProvider imageList;

    public ImageHolder(Type type, ImageDataProvider imageList) {
        this.type = type;
        this.imageList = imageList;
        getElement().getStyle().setProperty("display", "inline-block");
        getElement().getStyle().setProperty("padding", "5px");
        getElement().getStyle().setProperty("border", "1px solid #999");
        // image
        add(slideshow);

    }

    public void onModelChange() {
        for (String url : imageList.getImageUrls()) {
            Image image = new Image(url);
            //TODO see when to add image on load
            if (false) {
                image.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                    }
                });
            }
            scaleToFit(image);
            HorizontalPanel imageHolder = new HorizontalPanel();
            imageHolder.add(image);
            imageHolder.setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
            imageHolder.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);
            slideshow.addItem(imageHolder);
        }
    }

    public void reset() {
        onModelChange();
    }

    private static void scaleToFit(Image image) {
        if (1.0 * image.getWidth() / image.getHeight() > 1) {
            image.setSize("auto", "100%");
        } else {
            image.setSize("100%", "auto");
        }
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
        if (isEditable()) {
            add(editControls);
        } else {
            remove(editControls);
        }
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    class EditorControlPanel extends HorizontalPanel {

        private final Label label;

        public EditorControlPanel() {
            label = new Label("Click to edit");
            label.getElement().getStyle().setProperty("width", "100%");
            label.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    imageList.editImage();
                }
            });
            add(label);
        }
    }

}