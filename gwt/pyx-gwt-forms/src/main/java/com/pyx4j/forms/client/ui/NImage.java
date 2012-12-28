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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.forms.client.ui.CImage.Type;

public class NImage<T extends IFile> extends NComponent<List<T>, ImageHolder, CImage<T>, ImageHolder> implements ImageHolder.ImageDataProvider {

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
        if (values == null) {
            return;
        }
        imageUrls.clear();
        for (T value : values) {
            imageFiles.add(value);
            imageUrls.add(getCComponent().getImageUrl(value));
        }
        createWidget().reset();
    }

    @Override
    public List<T> getNativeValue() throws ParseException {
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
            widget = new ImageHolder(Type.multiple, this);
        }
        return widget;
    }
}