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

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.client.upload.FileUploadDialog;
import com.pyx4j.gwt.client.upload.FileUploadReciver;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageHolder;

public class NImage<T extends IFile> extends NField<List<T>, ImageHolder, CImage<T>, ImageHolder> implements ImageHolder.ImageDataProvider {

    private static final I18n i18n = I18n.get(NImage.class);

    private final List<IFile> imageFiles;

    private final List<String> imageUrls;

    private ImageHolder widget;

    public NImage(CImage<T> cComponent) {
        super(cComponent);
        imageFiles = new ArrayList<IFile>();
        imageUrls = new ArrayList<String>();
    }

    @Override
    public void setNativeValue(List<T> values) {
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
    public List<T> getNativeValue() {
        List<T> value = new ArrayList<T>();
        for (IFile file : imageFiles) {
            value.add(getCComponent().getNewValue(file));
        }
        return value;
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
            widget = new ImageHolder(new Dimension(250, 250), this);
        }
        return widget;
    }

    @Override
    public void editImage() {
        //TODO What is the best way customize title
        new FileUploadDialog<T>(i18n.tr("Upload Image File"), null, getCComponent().getUploadService(), new FileUploadReciver<T>() {

            @Override
            public void onUploadComplete(T uploadResponse) {
                //TODO What is the best way to add to native value
                List<T> value = new ArrayList<T>();
                value.addAll(getNativeValue());
                value.add(uploadResponse);
                getCComponent().setValue(value);
            }
        }).show();

    }
}