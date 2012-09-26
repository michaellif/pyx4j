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
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.components.media.MediaUploadDialog;
import com.propertyvista.domain.File;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class FileUploadHyperlink extends CHyperlink<File> {

    private static final I18n i18n = I18n.get(FileUploadHyperlink.class);

    public FileUploadHyperlink(final boolean editable, final ImageTarget imageTarget) {
        super((String) null);

        setCommand(new Command() {
            @Override
            public void execute() {

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
        });

        this.setFormat(new IFormat<File>() {
            @Override
            public String format(File value) {
                if (editable) {
                    if (value.blobKey().isNull()) {
                        return i18n.tr("Upload Image File");
                    } else {
                        return i18n.tr("{0}; Upload new file", value.fileName().getStringView());
                    }
                } else {
                    return null;
                }
            }

            @Override
            public File parse(String string) {
                return getValue();
            }
        });
    }
}
