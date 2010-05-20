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

import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.Tooltip;

public class PhotoAlbum extends DockPanel {

    private final ActionPanel actionPanel;

    private final FlowPanel photoPanel;

    private PhotoAlbumModel model;

    private Command addPhotoCommand;

    public PhotoAlbum() {
        actionPanel = new ActionPanel();
        add(actionPanel, DockPanel.NORTH);

        photoPanel = new FlowPanel();
        add(photoPanel, DockPanel.CENTER);

    }

    public void setPhotoAlbumModel(PhotoAlbumModel model) {
        this.model = model;
        model.setPhotoAlbum(this);
    }

    class PhotoHolder extends AbsolutePanel {

        private final Image image;

        private boolean showMenuHandler = false;

        private final MenuBar menuButtonBar;

        private final Tooltip tooltip;

        private final HTML caption;

        public PhotoHolder(final Photo photo) {
            setSize("220px", "220px");
            getElement().getStyle().setMargin(2, Unit.PX);
            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            image = new Image(photo.getThumbnailUrl());
            image.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            image.getElement().getStyle().setProperty("textAlign", "center");
            image.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    // TODO Auto-generated method stub

                }
            });

            DockPanel frame = new DockPanel();
            frame.setSize("200px", "200px");
            ImageResource background = ImageFactory.getImages().photoFrame();
            frame.getElement().getStyle().setProperty("background", "url(" + background.getURL() + ") no-repeat 100% 100%");

            frame.add(image, CENTER);
            frame.setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
            frame.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);

            caption = new HTML(photo.getCaption(), false);
            caption.getElement().getStyle().setCursor(Cursor.DEFAULT);
            caption.setHeight("1.2em");
            caption.setWidth("195px");
            caption.getElement().getStyle().setProperty("textAlign", "center");
            caption.getElement().getStyle().setProperty("overflow", "hidden");
            tooltip = Tooltip.tooltip(caption, photo.getCaption());
            frame.add(caption, SOUTH);
            add(frame, 10, 0);

            ImageResource viewMenu = ImageFactory.getImages().viewMenu();
            MenuBar actionsMenu = new ActionsMenu();
            MenuItem menuButtonItem = new MenuItem("<img src=" + viewMenu.getURL() + " ' alt=''>", true, actionsMenu);
            menuButtonItem.removeStyleName("gwt-MenuItem");
            menuButtonItem.getElement().getStyle().setCursor(Cursor.POINTER);

            menuButtonBar = new MenuBar();
            menuButtonBar.setVisible(false);
            menuButtonBar.addItem(menuButtonItem);

            add(menuButtonBar, 187, 5);

        }

        void setCaption(String captionText) {
            tooltip.setTooltipText(captionText);
            caption.setHTML(captionText);
        }

        class ActionsMenu extends MenuBar {
            public ActionsMenu() {
                super(true);
                setAutoOpen(true);
                MenuItem editCaptionMenu = new MenuItem("Edit Caption", true, new Command() {
                    @Override
                    public void execute() {
                        String newText = "EDITED" + System.currentTimeMillis();
                        PhotoAlbum.this.model.updateCaption(photoPanel.getWidgetIndex(PhotoHolder.this), newText);
                    }
                });
                MenuItem deletePhotoMenu = new MenuItem("Delete Photo", true, new Command() {

                    @Override
                    public void execute() {
                        model.removePhoto(photoPanel.getWidgetIndex(PhotoHolder.this));
                    }
                });
                addItem(editCaptionMenu);
                addItem(deletePhotoMenu);

                addCloseHandler(new CloseHandler<PopupPanel>() {

                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {
                        menuButtonBar.setVisible(showMenuHandler);
                    }
                });

                PhotoHolder.this.addDomHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        showMenuHandler = true;
                        menuButtonBar.setVisible(true);
                    }
                }, MouseOverEvent.getType());

                PhotoHolder.this.addDomHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        showMenuHandler = false;
                        if (!isAttached() || !isVisible()) {
                            menuButtonBar.setVisible(false);
                        }
                    }
                }, MouseOutEvent.getType());
            }

            @Override
            protected void onDetach() {
                super.onDetach();
                menuButtonBar.setVisible(showMenuHandler);
            }

        }

    }

    class ActionPanel extends HorizontalPanel {

        private final Button slideshowButton;

        private final Button addPhotoButton;

        public ActionPanel() {
            slideshowButton = new Button("Slideshow");
            slideshowButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Slideshow slideshow = new Slideshow(640, 510, null);
                    List<Photo> photoList = model.getPhotoList();
                    for (Photo photo : photoList) {
                        HorizontalPanel holder = new HorizontalPanel();
                        PhotoImage photoImage = new PhotoImage(photo.getPhotoUrl(), 600, 450);
                        photoImage.getElement().getStyle().setPadding(20, Unit.PX);
                        photoImage.getElement().getStyle().setPaddingBottom(40, Unit.PX);
                        holder.add(photoImage);
                        holder.setCellHorizontalAlignment(photoImage, ALIGN_CENTER);
                        holder.setCellVerticalAlignment(photoImage, ALIGN_MIDDLE);
                        slideshow.addItem(holder);
                    }
                    PopupPanel popup = new PopupPanel(true);
                    popup.getElement().getStyle().setBackgroundColor("#EDEDFF");
                    popup.getElement().getStyle().setBorderColor("gray");
                    popup.getElement().getStyle().setBorderWidth(1, Unit.PX);
                    popup.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
                    popup.getElement().getStyle().setProperty("WebkitBoxShadow", "10px 10px 5px #aaa");
                    popup.getElement().getStyle().setProperty("MozBoxShadow", "10px 10px 5px #aaa");
                    popup.add(slideshow);
                    popup.center();
                }
            });
            add(slideshowButton);

            addPhotoButton = new Button("Add photo");
            addPhotoButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    addPhotoCommand.execute();
                }
            });
            add(addPhotoButton);
        }
    }

    public void setAddPhotoCommand(Command command) {
        addPhotoCommand = command;
    }

    public void onPhotoAdded(Photo photo, int index) {
        PhotoHolder holder = new PhotoHolder(photo);
        photoPanel.add(holder);
    }

    public void onPhotoRemoved(int index) {
        photoPanel.remove(index);
    }

    public void onCaptionUpdated(String caption, int index) {
        PhotoHolder holder = (PhotoHolder) photoPanel.getWidget(index);
        holder.setCaption(caption);
    }

}
