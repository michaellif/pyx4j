/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.cms;

import com.google.gwt.user.client.Command;

import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.forms.client.ui.CFile;

import com.propertyvista.crm.client.ui.components.media.MediaUploadDialog;
import com.propertyvista.domain.File;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class FileUploadHyperlink extends CFile<File> {

    private final ImageTarget imageTarget;

    public FileUploadHyperlink(ImageTarget imageTarget, Command command) {
        super(command);
        this.imageTarget = imageTarget;
    }

    @Override
    public void showFileSelectionDialog() {
        new MediaUploadDialog() {

            @Override
            protected void onUploadComplete(UploadResponse serverUploadResponse) {
                getValue().blobKey().setValue(serverUploadResponse.uploadKey);
                getValue().fileName().setValue(serverUploadResponse.fileName);
                getValue().fileSize().setValue(serverUploadResponse.fileSize);
                getValue().timestamp().setValue(serverUploadResponse.timestamp);
                getValue().contentMimeType().setValue(serverUploadResponse.fileContentType);
                setNativeValue(getValue());
            }

            @Override
            protected ImageTarget getImageTarget() {
                return imageTarget;
            }

        }.show();
    }
}
