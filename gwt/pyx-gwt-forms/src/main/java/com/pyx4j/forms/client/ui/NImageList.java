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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.gwt.client.upload.FileUploadDialog;
import com.pyx4j.gwt.client.upload.FileUploadReciver;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageHolder;
import com.pyx4j.widgets.client.ImageHolder.ImageViewport;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Custom2Option;
import com.pyx4j.widgets.client.dialog.Dialog;

public class NImageList<T extends IFile> extends NField<IList<T>, ImageHolder, CImageList<T>, ImageHolder> implements ImageHolder.ImageDataProvider {

    private static final I18n i18n = I18n.get(NImageList.class);

    private final List<T> imageFiles;

    private final List<String> imageUrls;

    private ImageHolder widget;

    private ImageOrganizer organizer;

    protected IEditableComponentFactory factory = new EntityFormComponentFactory();

    public NImageList(CImageList<T> cComponent) {
        super(cComponent);
        imageFiles = new ArrayList<T>();
        imageUrls = new ArrayList<String>();
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
        createWidget().reset();
    }

    @Override
    public IList<T> getNativeValue() {
        return null;
    }

    @Override
    protected ImageHolder createEditor() {
        return createWidget();
    }

    @Override
    protected void onEditorInit() {
        super.onEditorInit();
        widget.setEditable(true);
    }

    @Override
    protected ImageHolder createViewer() {
        return createWidget();
    }

    @Override
    protected void onViewerInit() {
        super.onViewerInit();
        widget.setEditable(false);
    }

    @Override
    public List<String> getImageUrls() {
        return imageUrls;
    }

    private ImageHolder createWidget() {
        if (widget == null) {
            widget = new ImageHolder(new Dimension(250, 250), ImageHolder.Type.multiple, this);
            organizer = new ImageOrganizer(getCComponent().getImgClass(), getCComponent().getFolderIcons());
            organizer.initContent();
        }
        return widget;
    }

    @Override
    public void editImage() {
        organizer.show();
    }

    class ImageOrganizer extends CEntityFolder<T> {

        private final Class<T> imgClass;

        private final EntityFolderImages folderIcons;

        public ImageOrganizer(Class<T> imgClass, EntityFolderImages folderIcons) {
            super(imgClass);
            this.imgClass = imgClass;
            this.folderIcons = folderIcons;
        }

        public void addNewImage() {
            addItem();
        }

        public void show() {
            setValue(getCComponent().getValue());
            ((Dialog) getDecorator()).show();
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member.getObjectClass().equals(imgClass)) {
                return new CEntityForm<T>(imgClass) {
                    private final ImageViewport thumb = new ImageViewport(new Dimension(70, 70));

                    @Override
                    public IsWidget createContent() {
                        HorizontalPanel content = new HorizontalPanel();
                        content.add(thumb);

                        content.add(getCComponent().getImageEntryView(this));

                        return content;
                    }

                    @Override
                    protected void onValueSet(boolean populate) {
                        super.onValueSet(populate);
                        if (getValue() != null) {
                            thumb.setUrl(getCComponent().getImageUrl(getValue()));
                        }
                    }

                };
            } else {
                return factory.create(member);
            }
        }

        @Override
        protected void createNewEntity(final AsyncCallback<T> callback) {
            new FileUploadDialog<T>("Upload Image File", null, getCComponent().getUploadService(), new FileUploadReciver<T>() {
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
            private CEntityFolder<T> folder;

            public Decorator() {
                super("Image Organizer");
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
                this.folder = folder;
                setBody(folder.getContainer());
            }

            @Override
            public boolean onClickCancel() {
                getCComponent().getValue().clear();
                getCComponent().getValue().addAll(imageFiles);
                return true;
            }

            @Override
            public String custom2Text() {
                return "Save";
            }

            @Override
            public boolean onClickCustom2() {
                NImageList.this.setNativeValue(organizer.getValue());
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
                return "Add New Image";
            }

            @Override
            public boolean onClickCustom1() {
                organizer.addNewImage();
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