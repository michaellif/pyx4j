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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.client.upload.FileUploadDialog;
import com.pyx4j.gwt.client.upload.UploadReceiver;
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

public class NImageSlider<T extends IFile> extends NField<IList<T>, ImageSlider, CImageSlider<T>, ImageSlider> {

    private static final I18n i18n = I18n.get(NImageSlider.class);

    private final List<T> imageFiles;

    private final List<String> imageUrls;

    private final ImageSlider imageSlider;

    protected IEditableComponentFactory factory = new EntityFormComponentFactory();

    public NImageSlider(CImageSlider<T> cComponent) {
        super(cComponent);
        imageFiles = new ArrayList<T>();
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
    public void setNativeValue(IList<T> values) {
        imageFiles.clear();
        imageUrls.clear();
        if (values != null) {
            for (T value : values) {
                imageFiles.add(value);
                imageUrls.add(getCComponent().getImageUrl(value));
            }
        }
        reset();
    }

    public void reset() {
        imageSlider.reset();
    }

    @Override
    public IList<T> getNativeValue() {
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

    class ImageOrganizer extends CEntityFolder<T> {

        private final Class<T> imgClass;

        private final EntityFolderImages folderIcons;

        public ImageOrganizer(Class<T> imgClass, EntityFolderImages folderIcons) {
            super(imgClass);
            this.imgClass = imgClass;
            this.folderIcons = folderIcons;
            initContent();
        }

        public void addNewImage() {
            addItem();
        }

        @SuppressWarnings("unchecked")
        public void clear() {
            for (CComponent<?> item : new ArrayList<CComponent<?>>(getComponents())) {
                removeItem((CEntityFolderItem<T>) item);
            }
        }

        public void show() {
            setValue(getCComponent().getValue());
            ((Dialog) getDecorator()).show();
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member.getObjectClass().equals(imgClass)) {
                return new CEntityForm<T>(imgClass) {
                    private final ImageViewport thumb = new ImageViewport(getCComponent().getThumbSize(), ScaleMode.ScaleToFit);

                    @Override
                    public IsWidget createContent() {
                        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

                        thumb.setImage(getCComponent().getThumbnailPlaceholder());
                        content.setWidget(0, 0, thumb);
                        content.setWidget(0, 1, getCComponent().getImageEntryView(this));

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
            } else {
                return factory.create(member);
            }
        }

        @Override
        protected void createNewEntity(final AsyncCallback<T> callback) {
            @SuppressWarnings("unchecked")
            UploadService<IEntity, T> service = (UploadService<IEntity, T>) getCComponent().getUploadService();
            new FileUploadDialog<IEntity, T>(i18n.tr("Upload Image File"), null, service, new UploadReceiver<T>() {
                @Override
                public void onUploadComplete(T uploadResponse) {
                    callback.onSuccess(uploadResponse);
                }
            }).show();
        }

        @Override
        protected IFolderItemDecorator<T> createItemDecorator() {
            return new BoxFolderItemDecorator<T>(folderIcons);
        }

        @Override
        protected IFolderDecorator<T> createFolderDecorator() {
            return new Decorator();
        }

        class Decorator extends Dialog implements IFolderDecorator<T>, Custom1Option, Custom2Option, CancelOption {

            public Decorator() {
                super(i18n.tr("Image Organizer"));
                setDialogOptions(this);
            }

            @Override
            public void onSetDebugId(IDebugId parentDebugId) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onValueChange(ValueChangeEvent<IList<T>> event) {
                // TODO Auto-generated method stub
            }

            @Override
            public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setAddButtonVisible(boolean show) {
                // TODO Auto-generated method stub
            }

            @Override
            public void setComponent(CEntityFolder<T> folder) {
                ScrollPanel panel = new ScrollPanel();
                panel.add(folder.createContent());
                panel.getElement().getStyle().setProperty("maxHeight", "500px");
                setBody(panel);
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
                NImageSlider.this.setNativeValue(getValue());
                createViewer().reset();
                return true;
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