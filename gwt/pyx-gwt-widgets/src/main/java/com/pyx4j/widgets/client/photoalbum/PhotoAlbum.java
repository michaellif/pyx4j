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

    public PhotoAlbum() {
        actionPanel = new ActionPanel();
        add(actionPanel, DockPanel.NORTH);

        photoPanel = new FlowPanel();
        add(photoPanel, DockPanel.CENTER);

    }

    public void addPhoto(String thumbnailUrl, String photoUrl, String caption) {
        Photo photo = new Photo(thumbnailUrl, photoUrl, caption);
        PhotoHolder holder = new PhotoHolder(photo);
        photoPanel.add(holder);
    }

    public void removePhoto(int index) {
        photoPanel.remove(index);
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

            image = new Image(photo.thumbnailUrl);
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

            caption = new HTML(photo.caption, false);
            caption.getElement().getStyle().setCursor(Cursor.DEFAULT);
            caption.setHeight("1.2em");
            caption.setWidth("195px");
            caption.getElement().getStyle().setProperty("textAlign", "center");
            caption.getElement().getStyle().setProperty("overflow", "hidden");
            tooltip = Tooltip.tooltip(caption, photo.caption);
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

        class ActionsMenu extends MenuBar {
            public ActionsMenu() {
                super(true);
                setAutoOpen(true);
                MenuItem editCaptionMenu = new MenuItem("Edit Caption", true, new Command() {
                    @Override
                    public void execute() {
                        String newText = "EDITED" + System.currentTimeMillis();
                        tooltip.setTooltipText(newText);
                        caption.setHTML(newText);
                    }
                });
                MenuItem deletePhotoMenu = new MenuItem("Delete Photo", true, new Command() {

                    @Override
                    public void execute() {
                        removePhoto(photoPanel.getWidgetIndex(PhotoHolder.this));
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
            add(slideshowButton);
            addPhotoButton = new Button("Add photo");
            addPhotoButton.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    addPhoto("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG", "", "Photo#");
                }
            });
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
