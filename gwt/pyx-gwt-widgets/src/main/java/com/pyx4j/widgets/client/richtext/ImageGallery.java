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
package com.pyx4j.widgets.client.richtext;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;

public class ImageGallery extends Dialog implements CancelOption, RichTextImageProvider {

    private static final Logger log = LoggerFactory.getLogger(ImageGallery.class);

    private final FlowPanel mainPanel;

    private final boolean editable;

    private AsyncCallback<String> selectionCallback;

    public ImageGallery() {
        this(true);
    }

    public ImageGallery(boolean editable) {
        super("Image Picker");
        setDialogOptions(this);

        this.editable = editable;

        mainPanel = new FlowPanel();
        mainPanel.getElement().getStyle().setPadding(4, Unit.PX);

        ScrollPanel scroller = new ScrollPanel();
        scroller.setSize("700px", "500px");
        scroller.setWidget(mainPanel);

        setBody(scroller);
    }

    public void setImages(List<Image> list) {
        for (Image img : list) {
            addImage(img);
        }
    }

    class ImageFrame extends DockPanel {
        Double imgSize = 150.0;

        Double captionHeight = 0.1 * imgSize;

        Double captionWidth = 0.9 * imgSize;

        Double controlsHeight = editable ? 0.1 * imgSize : 0;

        Double frameHeight = 1.1 * (imgSize + captionHeight + controlsHeight);

        Double frameWidth = 1.1 * imgSize;

        public ImageFrame(final Image image) {
            setSize(frameWidth + "px", frameHeight + "px");
            setStyleName("ImageGallery-ImageFrame");
            getElement().getStyle().setMargin(2, Unit.PX);
            getElement().getStyle().setFloat(Style.Float.LEFT);

            if (1.0 * image.getWidth() / image.getHeight() > 1) {
                image.setWidth(imgSize + "px");
            } else {
                image.setHeight(imgSize + "px");
            }
            image.getElement().getStyle().setCursor(Cursor.POINTER);
            image.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectionCallback.onSuccess(image.getUrl());
                    ImageGallery.this.hide();
                }
            }, ClickEvent.getType());

            add(image, CENTER);
            setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
            setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);

            HTML caption = new HTML(image.getTitle(), false);
            caption.setStyleName("ImageGallery-ImageFrameCaption");
            caption.setHeight(captionHeight + "px");
            caption.setWidth(captionWidth + "px");
            caption.getElement().getStyle().setProperty("textAlign", "center");
            caption.getElement().getStyle().setProperty("overflow", "hidden");
            caption.setTitle(image.getTitle());
            add(caption, SOUTH);

            if (editable) {
                final FlowPanel controls = new FlowPanel();
                controls.setHeight(controlsHeight + "px");
                controls.getElement().getStyle().setProperty("visibility", "hidden");
                add(controls, NORTH);

                final Image delButt = new Image(ImageFactory.getImages().del());
                delButt.getElement().getStyle().setCursor(Cursor.POINTER);
                delButt.getElement().getStyle().setFloat(Style.Float.RIGHT);

                delButt.addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        ImageGallery.this.removeImage(mainPanel.getWidgetIndex(ImageFrame.this));
                    }
                }, ClickEvent.getType());

                controls.add(delButt);

                addDomHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        controls.getElement().getStyle().setProperty("visibility", "visible");
                    }
                }, MouseOverEvent.getType());

                addDomHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        controls.getElement().getStyle().setProperty("visibility", "hidden");
                    }
                }, MouseOutEvent.getType());
            }
        }
    }

    public void addImage(final Image image) {
        mainPanel.add(new ImageFrame(image));
    }

    public void removeImage(int index) {
        mainPanel.remove(index);
    }

    @Override
    public void selectImage(AsyncCallback<String> callback) {
        selectionCallback = callback;
        center();
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }
}
