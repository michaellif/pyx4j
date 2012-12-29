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
package com.pyx4j.forms.client.ui;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.forms.client.ui.CImage.Type;
import com.pyx4j.widgets.client.IWidget;
import com.pyx4j.widgets.client.ImageFactory;

public class ImageHolder extends DockPanel implements IWidget {

    interface ImageDataProvider {

        List<String> getImageUrls();

        void editImage();

    }

    final Image image = new Image();

    private final CImage.Type type;

    private final ViewerControlPanel viewControls = new ViewerControlPanel();

    private final EditorControlPanel editControls = new EditorControlPanel();

    private boolean editable;

    private final ImageDataProvider imageList;

    private int curIdx = -1;

    public ImageHolder(CImage.Type type, ImageDataProvider imageList) {
        this.type = type;
        this.imageList = imageList;
        setSize("150px", "150px");
        getElement().getStyle().setProperty("padding", "5px");
        getElement().getStyle().setProperty("border", "1px solid #999");
        // image
        add(image, CENTER);
        setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
        setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);
        // view controls (slideshowLeft, slideshowRight)
        if (type == Type.single) {
            viewControls.setVisible(false);
        }
    }

    public void reset() {
        curIdx = -1;
        onModelChange();
    }

    public void onModelChange() {
        if (curIdx >= 0) {
            setUrl(imageList.getImageUrls().get(curIdx));
        } else {
            setUrl("");
        }
        viewControls.syncState();
    }

    private void setUrl(String url) {
        image.setUrl(url);
        if (image.getWidth() > 0 && image.getHeight() > 0) {
            scaleToFit();
        } else {
            image.setVisible(false);
            image.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    scaleToFit();
                    image.setVisible(true);
                }
            });
        }
    }

    private void scaleToFit() {
        if (1.0 * image.getWidth() / image.getHeight() > 1) {
            image.setSize("100%", "auto");
        } else {
            image.setSize("auto", "100%");
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
        remove(isEditable() ? viewControls : editControls);
        this.editable = editable;
        add(isEditable() ? editControls : viewControls, SOUTH);
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

    class ViewerControlPanel extends HorizontalPanel implements ClickHandler {

        private final HTML label;

        private final Image left;

        private final Image right;

        public ViewerControlPanel() {
            label = new HTML();
            label.getElement().getStyle().setProperty("textAlign", "center");

            left = new Image(ImageFactory.getImages().slideshowLeft());
            left.addClickHandler(this);

            right = new Image(ImageFactory.getImages().slideshowRight());
            right.addClickHandler(this);

            add(left);
            add(label);
            add(right);

            setCellHorizontalAlignment(left, HorizontalPanel.ALIGN_LEFT);
            setCellHorizontalAlignment(right, HorizontalPanel.ALIGN_RIGHT);
            setCellHorizontalAlignment(label, HorizontalPanel.ALIGN_CENTER);
            setCellWidth(label, "100%");
            setWidth("100%");
        }

        public void syncState() {
            label.setHTML((curIdx + 1) + " of " + imageList.getImageUrls().size());
        }

        @Override
        public void onClick(ClickEvent event) {
            if (event.getSource() == right && curIdx < imageList.getImageUrls().size() - 1) {
                curIdx++;
                onModelChange();
            } else if (event.getSource() == left && curIdx > 0) {
                curIdx--;
                onModelChange();
            }
        }
    }
}