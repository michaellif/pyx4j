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
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IHasFile;
import com.pyx4j.entity.shared.IListWrapper;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.gwt.client.upload.FileUploadDialog;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.Image;
import com.pyx4j.gwt.commons.ui.ScrollPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageSlider;
import com.pyx4j.widgets.client.ImageSlider.ImageSliderDataProvider;
import com.pyx4j.widgets.client.ImageSlider.ImageSliderType;
import com.pyx4j.widgets.client.ImageViewport;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Custom2Option;
import com.pyx4j.widgets.client.dialog.Dialog;

public class NImageSlider<E extends IHasFile<?>> extends NField<IList<E>, ImageSlider, CImageSlider<E>, ImageSlider> {

    private static final I18n i18n = I18n.get(NImageSlider.class);

    private final List<E> imageFiles;

    private final List<String> imageUrls;

    private final ImageSlider imageSlider;

    private int organizerWidth;

    public NImageSlider(CImageSlider<E> cComponent) {
        super(cComponent);
        imageFiles = new ArrayList<E>();
        imageUrls = new ArrayList<String>();

        imageSlider = new ImageSlider(getCComponent().getImageSize(), new ImageSliderDataProvider() {

            @Override
            public List<String> getImageUrls() {
                return imageUrls;
            }

            @Override
            public Image getPlaceholder() {
                return getCComponent().getThumbnailPlaceholder();
            }

            @Override
            public ImageSliderType getImageSliderType() {
                return ImageSliderType.multiple;
            }
        });

        imageSlider.getEditButton().setCommand(new Command() {

            @Override
            public void execute() {
                new ImageOrganizer(getCComponent().getImgClass(), getCComponent().getFolderIcons()).show();
            }
        });
    }

    @Override
    public void setNativeValue(IList<E> values) {
        imageFiles.clear();
        imageUrls.clear();
        if (values != null) {
            for (E value : values) {
                imageFiles.add(value);
                imageUrls.add(getCComponent().getImageUrl(value));
            }
        }
        reset();
    }

    public void setScaleMode(ScaleMode scaleMode) {
        imageSlider.setScaleMode(scaleMode);
    }

    public void reset() {
        imageSlider.reset();
    }

    @Override
    public IList<E> getNativeValue() {
        return null;
    }

    @Override
    protected ImageSlider createEditor() {
        return imageSlider;
    }

    @Override
    protected void onEditorInit() {
        imageSlider.setEditable(true);
        super.onEditorInit();
    }

    @Override
    protected ImageSlider createViewer() {
        return imageSlider;
    }

    @Override
    protected void onViewerInit() {
        imageSlider.setEditable(false);
        super.onViewerInit();
    }

    @Override
    public void setNavigationCommand(Command navigationCommand) {
        if (navigationCommand != null) {
            super.setNavigationCommand(navigationCommand);
        }
    }

    public void resizeToFit() {
        Dimension imageSize = getCComponent().getImageSize();
        imageSlider.setImageSize(imageSize.width, imageSize.height);
    }

    public void setOrganizerWidth(int width) {
        organizerWidth = width;
    }

    class ImageOrganizer extends CForm<IListWrapper> {

        private final ImageOrganizerFolder imgFolder;

        public ImageOrganizer(Class<E> imgClass, FolderImages folderIcons) {
            super(IListWrapper.class);
            imgFolder = new ImageOrganizerFolder(imgClass, folderIcons);
            init();
        }

        @Override
        protected IsWidget createContent() {
            inject(proto().items(), imgFolder);
            return imgFolder.asWidget();
        }

        public void show() {
            imgFolder.populate(getCComponent().getValue());
            ((Dialog) imgFolder.getDecorator()).show();
        }

    }

    class ImageOrganizerFolder extends CFolder<E> {

        private final Class<E> imgClass;

        private final FolderImages folderIcons;

        public ImageOrganizerFolder(Class<E> imgClass, FolderImages folderIcons) {
            super(imgClass);
            this.imgClass = imgClass;
            this.folderIcons = folderIcons;
        }

        public void addNewImage() {
            addItem();
        }

        @Override
        protected CForm<E> createItemForm(IObject<?> member) {
            return new CForm<E>(imgClass) {
                private final ImageViewport thumb = new ImageViewport(getCComponent().getThumbSize(), ScaleMode.Contain);

                @Override
                protected IsWidget createContent() {
                    FlowPanel content = new FlowPanel();

                    thumb.setImage(getCComponent().getThumbnailPlaceholder());
                    thumb.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
                    thumb.getElement().getStyle().setProperty("margin", "2px 5px");
                    content.add(thumb);
                    IsWidget infoPanel = getCComponent().getImageEntryView(this);
                    infoPanel.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                    content.add(infoPanel);

                    return content;
                }

                @Override
                protected void onValueSet(boolean populate) {
                    super.onValueSet(populate);
                    if (getValue() != null) {
                        thumb.setImage(new Image(getCComponent().getImageUrl(getValue())));
                    }
                }
            };
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected void createNewEntity(final AsyncCallback<E> callback) {
            UploadService<?, ?> service = getCComponent().getUploadService();
            new FileUploadDialog(i18n.tr("Upload Image File"), null, service, new UploadReceiver() {
                @Override
                public void onUploadComplete(IFile<?> uploadResponse) {
                    E t = EntityFactory.create(imgClass);
                    t.file().set(uploadResponse);
                    callback.onSuccess(t);
                }
            }).show();
        }

        @Override
        protected IFolderItemDecorator<E> createItemDecorator() {
            return new BoxFolderItemDecorator<E>(folderIcons);
        }

        @Override
        protected IFolderDecorator<E> createFolderDecorator() {
            return new Decorator();
        }

        class Decorator extends Dialog implements IFolderDecorator<E>, Custom1Option, Custom2Option, CancelOption {
            ScrollPanel scrollPanel = new ScrollPanel();

            public Decorator() {
                super(i18n.tr("Image Organizer"));
                setDialogOptions(this);
                if (organizerWidth > 0) {
                    setDialogPixelWidth(organizerWidth);
                }
                setBody(scrollPanel);
            }

            @Override
            public void onSetDebugId(IDebugId parentDebugId) {
                // TODO Auto-generated method stub
            }

            @Override
            public void layout() {
                super.layout();
                // adjust scroll panel accordingly
                if (scrollPanel != null) {
                    scrollPanel.getStyle().setProperty("maxHeight", getContentMaxHeight() + "px");
                }
            }

            @Override
            public void onValueChange(ValueChangeEvent<IList<E>> event) {
                layout();
            }

            @Override
            public void setItemAddCommand(Command command) {
                // TODO Auto-generated method stub
            }

            @Override
            public void setAddButtonVisible(boolean show) {
                // TODO Auto-generated method stub
            }

            @Override
            public void init(CFolder<E> folder) {
            }

            @Override
            public void setContent(IsWidget content) {
                scrollPanel.setWidget(content);
            }

            @Override
            public boolean onClickCancel() {
                if (getCComponent().getValue() != null) {
                    getCComponent().getValue().clear();
                    getCComponent().getValue().addAll(imageFiles);
                }
                return true;
            }

            @Override
            public String custom2Text() {
                return i18n.tr("Save");
            }

            @Override
            public boolean onClickCustom2() {
                ImageOrganizerFolder.this.setVisitedRecursive();
                if (ImageOrganizerFolder.this.isValid()) {
                    NImageSlider.this.setNativeValue(getValue());
                    createViewer().reset();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public IDebugId getCustom2DebugID() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String custom1Text() {
                return i18n.tr("Add Image");
            }

            @Override
            public boolean onClickCustom1() {
                addNewImage();
                return false;
            }

            @Override
            public IDebugId getCustom1DebugID() {
                // TODO Auto-generated method stub
                return null;
            }

        }
    }
}