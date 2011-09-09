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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.Command;

import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.forms.client.ui.CAbstractHyperlink;
import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.crm.client.ui.components.media.MediaUploadDialog;
import com.propertyvista.domain.File;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class FileUploadHyperlink extends CAbstractHyperlink<File> {

    protected static I18n i18n = I18nFactory.getI18n(FileUploadHyperlink.class);

    public FileUploadHyperlink(final boolean editable, final ImageTarget imageTarget) {
        super((String) null);

        setCommand(new Command() {
            @Override
            public void execute() {

                new MediaUploadDialog() {

                    @Override
                    protected void onUploadComplete(UploadResponse serverUploadResponse) {
                        getValue().blobKey().setValue(serverUploadResponse.uploadKey);
                        getValue().filename().setValue(serverUploadResponse.fileName);
                        getValue().fileSize().setValue(serverUploadResponse.fileSize);
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
                        return i18n.tr("Upload image file");
                    } else {
                        return value.filename().getStringView() + i18n.tr("; Upload new file");
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
