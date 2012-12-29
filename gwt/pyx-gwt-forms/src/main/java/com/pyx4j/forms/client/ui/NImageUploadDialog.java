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
package com.pyx4j.forms.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

public class NImageUploadDialog<E extends IFile> extends VerticalPanel implements OkCancelOption, OkOptionText {

    private static final I18n i18n = I18n.get(NImageUploadDialog.class);

    private final UploadPanel<E, E> uploadPanel;

    private final Dialog dialog;

    public NImageUploadDialog(UploadService<E, E> service) {
        dialog = new Dialog(i18n.tr("Upload Image File"), this, null);

        uploadPanel = new UploadPanel<E, E>(service) {

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
            protected void onUploadComplete(UploadResponse<E> serverUploadResponse) {
                dialog.hide();
                NImageUploadDialog.this.onUploadComplete(serverUploadResponse);
            }

            @Override
            protected E getUploadData() {
//                MediaUploadDTO dto = EntityFactory.create(MediaUploadDTO.class);
//                dto.target().setValue(getImageTarget());
                return null;
            }

        };
        //uploadPanel.setSupportedExtensions(MediaUploadService.supportedFormats);
        //uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);

        uploadPanel.setSize("400px", "60px");
        uploadPanel.getElement().getStyle().setMarginTop(50, Style.Unit.PX);
        uploadPanel.getElement().getStyle().setPaddingLeft(35, Style.Unit.PX);

        dialog.setBody(uploadPanel);
        dialog.setPixelSize(460, 150);
    }

    public void show() {
        dialog.show();
    }

    protected void onUploadComplete(UploadResponse<E> serverUploadResponse) {

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
