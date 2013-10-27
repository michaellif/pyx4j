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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.gwt.client.upload.FileUploadDialog;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

public class NFile<E extends IFile> extends NField<E, Anchor, CFile<E>, Anchor> {

    private static final I18n i18n = I18n.get(NFile.class);

    private final Anchor fileNameAnchor;

    public NFile(final CFile<E> file) {
        super(file);

        fileNameAnchor = new Anchor("", new Command() {

            @Override
            public void execute() {
                Window.open(getCComponent().getImageUrl(), "_blank", null);
            }
        });

        Button triggerButton = new Button(ImageFactory.getImages().triggerDown(), new Command() {

            @Override
            public void execute() {
                showUploadFileDialog();
            }
        });
        setTriggerButton(triggerButton);

        Button clearButton = new Button(ImageFactory.getImages().clear(), new Command() {

            @Override
            public void execute() {
                getCComponent().setValue(null);
            }
        });
        setClearButton(clearButton);

    }

    private void showUploadFileDialog() {
        @SuppressWarnings("unchecked")
        UploadService<IEntity, E> service = (UploadService<IEntity, E>) getCComponent().getUploadService();
        new FileUploadDialog<IEntity, E>(i18n.tr("Upload Image File"), null, service, new UploadReceiver<E>() {
            @Override
            public void onUploadComplete(E uploadResponse) {
                getCComponent().setValue(uploadResponse);
            }
        }).show();
    }

    @Override
    public void setNativeValue(E value) {
        String text = "";
        CFile<E> comp = getCComponent();
        if (value != null) {
            if (comp.getFormat() != null) {
                text = comp.getFormat().format(value);
            } else {
                text = value.toString();
            }
        }

        fileNameAnchor.setText(text);
    }

    @Override
    public E getNativeValue() throws ParseException {
        assert false : "getNativeValue() shouldn't be called on Hyperlink";
        return null;
    }

    @Override
    protected Anchor createEditor() {
        return fileNameAnchor;
    }

    @Override
    protected Anchor createViewer() {
        return fileNameAnchor;
    }

}
