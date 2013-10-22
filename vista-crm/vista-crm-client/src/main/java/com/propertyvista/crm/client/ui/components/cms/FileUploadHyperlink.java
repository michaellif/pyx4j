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

import com.pyx4j.forms.client.ui.CFile;

import com.propertyvista.crm.client.ui.components.media.MediaUploadDialog;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class FileUploadHyperlink extends CFile<MediaFile> {

    private final ImageTarget imageTarget;

    public FileUploadHyperlink(ImageTarget imageTarget, Command command) {
        super(command);
        this.imageTarget = imageTarget;
    }

    @Override
    public void showFileSelectionDialog() {
        new MediaUploadDialog() {

            @Override
            protected void onUploadComplete(MediaFile serverUploadResponse) {
                getValue().blobKey().setValue(serverUploadResponse.blobKey().getValue());
                getValue().fileName().setValue(serverUploadResponse.fileName().getValue());
                getValue().fileSize().setValue(serverUploadResponse.fileSize().getValue());
                getValue().timestamp().setValue(serverUploadResponse.timestamp().getValue());
                getValue().contentMimeType().setValue(serverUploadResponse.contentMimeType().getValue());

                FileUploadHyperlink.this.setValue(getValue());
            }

            @Override
            protected ImageTarget getImageTarget() {
                return imageTarget;
            }

        }.show();
    }
}
