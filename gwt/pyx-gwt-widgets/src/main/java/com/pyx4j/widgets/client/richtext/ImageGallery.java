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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.ImageFactory;

public abstract class ImageGallery implements IsWidget {
    private final ScrollPanel contentPanel;

    private final FlowPanel mainPanel;

    private final boolean editable;

    public ImageGallery() {
        this(true);
    }

    public ImageGallery(boolean editable) {

        this.editable = editable;

        mainPanel = new FlowPanel();
        mainPanel.getElement().getStyle().setPadding(4, Unit.PX);

        contentPanel = new ScrollPanel();
        contentPanel.setSize("700px", "500px");
        contentPanel.setWidget(mainPanel);
    }

    class ImageFrame extends DockPanel {
        Double imgSize = 150.0;

        Double captionHeight = 0.1 * imgSize;

        Double captionWidth = 0.9 * imgSize;

        Double controlsHeight = editable ? 0.1 * imgSize : 0;

        Double frameHeight = 1.1 * (imgSize + captionHeight + controlsHeight);

        Double frameWidth = 1.1 * imgSize;

        public ImageFrame(final Image image, String title) {
            setSize(frameWidth + "px", frameHeight + "px");
            setStyleName("ImageGallery-ImageFrame");
            getElement().getStyle().setMargin(2, Unit.PX);
            getElement().getStyle().setFloat(Style.Float.LEFT);
            getElement().getStyle().setProperty("border", "1px solid #ccc");

            if (image.getWidth() > 0 && image.getHeight() > 0) {
                scaleToFit(image);
            } else {
                image.setVisible(false);
                image.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                        scaleToFit(image);
                        image.setVisible(true);
                    }
                });
            }

            image.getElement().getStyle().setCursor(Cursor.POINTER);
            image.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    onSelectImage(image);
                }
            }, ClickEvent.getType());

            add(image, CENTER);
            setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
            setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);

            Label caption = new Label(title, false);
            caption.setStyleName("ImageGallery-ImageFrameCaption");
            caption.setHeight(captionHeight + "px");
            caption.setWidth(captionWidth + "px");
            caption.getElement().getStyle().setProperty("overflow", "hidden");
            add(caption, SOUTH);
            setCellHorizontalAlignment(caption, HorizontalPanel.ALIGN_CENTER);

            if (editable) {
                final FlowPanel controls = new FlowPanel();
                controls.setHeight(controlsHeight + "px");
                controls.getElement().getStyle().setProperty("visibility", "hidden");
                add(controls, NORTH);

                final Image delButt = new Image(ImageFactory.getImages().delButton().regular());
                delButt.getElement().getStyle().setCursor(Cursor.POINTER);
                delButt.getElement().getStyle().setFloat(Style.Float.RIGHT);

                delButt.addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        onRemoveImage(image);
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

        private void scaleToFit(Image image) {
            if (1.0 * image.getWidth() / image.getHeight() > 1) {
                image.setWidth(imgSize + "px");
            } else {
                image.setHeight(imgSize + "px");
            }
        }
    }

    public void addImage(String url, String title) {
        final Image image = new Image(url);
        mainPanel.add(new ImageFrame(image, title));
    }

    public void removeImage(Image image) {
        mainPanel.remove(image.getParent());
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    protected abstract void onSelectImage(Image image);

    protected abstract void onRemoveImage(Image image);
}
