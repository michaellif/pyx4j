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

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
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
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.style.CSSClass;

public abstract class PhotoAlbum extends DockPanel {

    private static final Logger log = LoggerFactory.getLogger(PhotoAlbum.class);

    private final ActionPanel actionPanel;

    private final FlowPanel photoPanel;

    private PhotoAlbumModel model;

    private final boolean editable;

    private final HashMap<Photo, PhotoImage> slidesCache = new HashMap<Photo, PhotoImage>();

    public PhotoAlbum() {
        this(true);
    }

    public PhotoAlbum(boolean editable) {

        this.editable = editable;

        actionPanel = new ActionPanel();
        add(actionPanel, DockPanel.NORTH);

        photoPanel = new FlowPanel();
        add(photoPanel, DockPanel.CENTER);

    }

    public PhotoAlbumModel getPhotoAlbumModel() {
        return model;
    }

    public void setPhotoAlbumModel(PhotoAlbumModel model) {
        this.model = model;
        model.setPhotoAlbum(this);
    }

    class PhotoHolder extends AbsolutePanel {

        private final Image image;

        private boolean showMenuHandler = false;

        private MenuBar menuButtonBar;

        private final HTML caption;

        public PhotoHolder(final Photo photo) {
            setSize("220px", "220px");
            getElement().getStyle().setMargin(2, Unit.PX);
            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            image = new Image(photo.getThumbnailUrl());
            image.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            image.getElement().getStyle().setProperty("textAlign", "center");

            DockPanel frame = new DockPanel();
            frame.setSize("200px", "210px");
            frame.setStyleName(CSSClass.pyx4j_Photoalbom_Thumbnail.name());

            frame.add(image, CENTER);
            frame.setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
            frame.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);

            caption = new HTML(photo.getCaption(), false);
            caption.setStyleName(CSSClass.pyx4j_Photoalbom_Caption.name());
            caption.setHeight("1.2em");
            caption.setWidth("195px");
            caption.getElement().getStyle().setProperty("textAlign", "center");
            caption.getElement().getStyle().setProperty("overflow", "hidden");
            caption.setTitle(photo.getCaption());
            frame.add(caption, SOUTH);
            frame.setCellHeight(caption, "1.6em");
            add(frame, 10, 0);

            PhotoHolder.this.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    slideshow(photoPanel.getWidgetIndex(PhotoHolder.this), false);
                }
            }, ClickEvent.getType());

            PhotoHolder.this.getElement().getStyle().setCursor(Cursor.POINTER);

            if (editable) {
                ImageResource viewMenu = ImageFactory.getImages().viewMenu();
                ActionsMenu actionsMenu = new ActionsMenu();
                MenuItem menuButtonItem = new MenuItem("<img src=" + viewMenu.getURL() + " ' alt=''>", true, actionsMenu);
                menuButtonItem.removeStyleName("gwt-MenuItem");
                menuButtonItem.getElement().getStyle().setCursor(Cursor.POINTER);

                menuButtonBar = new MenuBar() {
                    @Override
                    public void onBrowserEvent(Event event) {
                        if (event.getTypeInt() == Event.ONCLICK) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                        super.onBrowserEvent(event);
                    }
                };
                menuButtonBar.setVisible(false);
                menuButtonBar.addItem(menuButtonItem);

                add(menuButtonBar, 187, 5);
            }
        }

        void setCaption(String captionText) {
            caption.setTitle(captionText);
            caption.setHTML(captionText);
        }

        class ActionsMenu extends MenuBar {
            public ActionsMenu() {
                super(true);
                setAutoOpen(true);
                MenuItem editCaptionMenu = new MenuItem("Edit Caption", true, new Command() {
                    @Override
                    public void execute() {
                        updateCaptionCommand(photoPanel.getWidgetIndex(PhotoHolder.this));
                    }
                });
                addItem(editCaptionMenu);

                MenuItem deletePhotoMenu = new MenuItem("Delete Photo", true, new Command() {

                    @Override
                    public void execute() {
                        model.removePhoto(photoPanel.getWidgetIndex(PhotoHolder.this));
                    }
                });
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

        private Button addPhotoButton;

        public ActionPanel() {
            slideshowButton = new Button("Slideshow");
            slideshowButton.getElement().getStyle().setMargin(12, Unit.PX);
            slideshowButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    slideshow(0, true);
                }
            });
            add(slideshowButton);

            if (editable) {
                addPhotoButton = new Button("Add photo");
                addPhotoButton.getElement().getStyle().setMargin(12, Unit.PX);
                addPhotoButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        addPhotoCommand();
                    }
                });
                add(addPhotoButton);
            }
        }
    }

    protected void slideshow(int startFrom, boolean run) {
        List<Photo> photoList = model.getPhotoList();
        if (photoList.size() == 0) {
            MessageDialog.warn("Photoalbom is empty", "Photoalbom is empty. Add photo first.");
        } else {
            Slideshow slideshow = new Slideshow(640, 510, startFrom, run);
            for (Photo photo : photoList) {
                HorizontalPanel holder = new HorizontalPanel();
                PhotoImage photoImage = slidesCache.get(photo);
                if (photoImage == null) {
                    photoImage = new PhotoImage(photo.getPhotoUrl(), 600, 450);
                    slidesCache.put(photo, photoImage);
                }
                photoImage.getElement().getStyle().setPadding(20, Unit.PX);
                photoImage.getElement().getStyle().setPaddingBottom(40, Unit.PX);
                holder.add(photoImage);
                holder.setCellHorizontalAlignment(photoImage, ALIGN_CENTER);
                holder.setCellVerticalAlignment(photoImage, ALIGN_MIDDLE);
                slideshow.addItem(holder);
            }
            PopupPanel popup = new PopupPanel(true);
            popup.setStyleName(CSSClass.pyx4j_SlideshowPopup.name());
            popup.add(slideshow);

            popup.center();
        }
    }

    public abstract void addPhotoCommand();

    public abstract void updateCaptionCommand(int index);

    public void onPhotoAdded(final Photo photo, int index) {
        PhotoHolder holder = new PhotoHolder(photo);
        photoPanel.add(holder);

        DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
            @Override
            public void execute() {
                PhotoImage photoImage = new PhotoImage(photo.getPhotoUrl(), 600, 450);
                slidesCache.put(photo, photoImage);
            }
        });

    }

    public void onPhotoRemoved(int index) {
        photoPanel.remove(index);
    }

    public void onCaptionUpdated(String caption, int index) {
        PhotoHolder holder = (PhotoHolder) photoPanel.getWidget(index);
        holder.setCaption(caption);
    }

    public void onClear() {
        photoPanel.clear();
        slidesCache.clear();
    }

}
