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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.client.upload.FileUploadDialog;
import com.pyx4j.gwt.client.upload.FileUploadReciver;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.ImageSlider;
import com.pyx4j.widgets.client.ImageSlider.ImageSliderType;

public class NImage<T extends IFile> extends NField<T, ImageSlider, CImage<T>, ImageSlider> implements ImageSlider.ImageSliderDataProvider {

    private static final I18n i18n = I18n.get(NImage.class);

    private String imageUrl;

    private final ImageSlider imageSlider;

    protected IEditableComponentFactory factory = new EntityFormComponentFactory();

    public NImage(CImage<T> cComponent) {
        super(cComponent);

        imageSlider = new ImageSlider(getCComponent().getImageSize(), this);
    }

    @Override
    public void setNativeValue(T value) {
        imageUrl = getCComponent().getImageUrl(value);
        reset();
    }

    public void reset() {
        imageSlider.reset();
    }

    @Override
    public T getNativeValue() {
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

    @Override
    public List<String> getImageUrls() {
        ArrayList<String> list = new ArrayList<String>();
        if (imageUrl != null) {
            list.add(imageUrl);
        }
        return list;
    }

    public void resizeToFit() {
        Dimension imageSize = getCComponent().getImageSize();
        imageSlider.setImageSize(imageSize.width, imageSize.height);
    }

    @Override
    public void editImageSet() {
        if (getCComponent().getValue() == null) {
            showUploadFileDialog();
        } else {
            new EditMenu().showRelativeTo(imageSlider.getEditControl());
        }
    }

    @Override
    public Image getPlaceholder() {
        return getCComponent().getThumbnailPlaceholder();
    }

    @Override
    public ImageSliderType getImageSliderType() {
        return ImageSliderType.single;
    }

    private void showUploadFileDialog() {
        new FileUploadDialog<T>(i18n.tr("Upload Image File"), null, getCComponent().getUploadService(), new FileUploadReciver<T>() {
            @Override
            public void onUploadComplete(T uploadResponse) {
                getCComponent().setValue(uploadResponse);
            }
        }).show();
    }

    class EditMenu extends DropDownPanel {

        public EditMenu() {
            super();
            FlowPanel mainPanel = new FlowPanel();
            mainPanel.add(new Button(i18n.tr("Remove"), new Command() {

                @Override
                public void execute() {
                    EditMenu.this.hide();
                    getCComponent().setValue(null);
                }
            }));
            mainPanel.add(new Button(i18n.tr("Upload fromComputer"), new Command() {

                @Override
                public void execute() {
                    EditMenu.this.hide();
                    showUploadFileDialog();
                }
            }));
            setWidget(mainPanel);
        }
    }
}