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
 * Created on 2012-12-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.client.upload;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

public class FileUploadDialog<U extends IEntity, E extends IFile> extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static final I18n i18n = I18n.get(FileUploadDialog.class);

    private final UploadPanel<U, E> uploadPanel;

    protected final Dialog dialog;

    protected U uploadData;

    public FileUploadDialog(String dialogTitle, U uploadData, UploadService<U, E> service, final UploadReceiver<E> uploadReceiver) {

        dialog = new Dialog(dialogTitle, this, null);
        dialog.setDialogPixelWidth(500);

        this.uploadData = uploadData;

        UploadReceiver<E> receiver = new UploadReceiver<E>() {

            @Override
            public void onUploadComplete(E uploadResponse) {
                dialog.hide(false);
                uploadReceiver.onUploadComplete(uploadResponse);
            }

        };

        uploadPanel = new UploadPanel<U, E>(service, receiver) {

            @Override
            protected void onUploadSubmit() {
                dialog.getOkButton().setEnabled(false);
            }

            @Override
            protected void onUploadError(UploadError error, String args) {
                super.onUploadError(error, args);
                dialog.getOkButton().setEnabled(true);
                uploadPanel.reset();
            }

            @Override
            protected U getUploadData() {
                return FileUploadDialog.this.getUploadData();
            }

        };

        uploadPanel.getElement().getStyle().setMarginTop(50, Style.Unit.PX);
        uploadPanel.getElement().getStyle().setPadding(20, Style.Unit.PX);

    }

    protected U getUploadData() {
        return uploadData;
    }

    protected IsWidget createContent(UploadPanel<U, E> uploadPanel) {
        return uploadPanel;
    }

    public void show() {
        dialog.setBody(createContent(uploadPanel));
        dialog.show();
    }

    @Override
    public boolean onClickOk() {
        uploadPanel.uploadSubmit();
        return false;
    }

    @Override
    public String optionTextOk() {
        return i18n.tr("Upload");
    }

    @Override
    public boolean onClickCancel() {
        uploadPanel.uploadCancel();
        return true;
    }

}
