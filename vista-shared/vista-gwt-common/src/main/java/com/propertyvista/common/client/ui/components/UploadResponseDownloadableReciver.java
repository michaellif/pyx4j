/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.dto.DownloadableUploadResponseDTO;

public class UploadResponseDownloadableReciver implements UploadReceiver {

    private static final I18n i18n = I18n.get(UploadResponseDownloadableReciver.class);

    private String downloadServletPath;

    private final String processNameCaption;

    public UploadResponseDownloadableReciver(String processNameCaption) {
        this.processNameCaption = processNameCaption;
    }

    public void setDownloadServletPath(String path) {
        downloadServletPath = path;
    }

    @Override
    public void onUploadComplete(IFile<?> uploadResponse) {
        DownloadableUploadResponseDTO serverUploadResponse = (DownloadableUploadResponseDTO) uploadResponse;
        if (serverUploadResponse.resultUrl().isNull()) {
            if (serverUploadResponse.success().getValue()) {
                MessageDialog.info(i18n.tr("{0} Complete", processNameCaption), serverUploadResponse.message().getValue());
            } else {
                MessageDialog.error(i18n.tr("{0} Error", processNameCaption), serverUploadResponse.message().getValue());
            }
        } else {
            if (serverUploadResponse.success().getValue()) {
                DownloadLinkDialog d = new DownloadLinkDialog(i18n.tr("{0} Complete", processNameCaption));
                if (downloadServletPath != null) {
                    d.setDownloadServletPath(downloadServletPath);
                }
                d.show(serverUploadResponse.message().getValue(), i18n.tr("Download processing results"), serverUploadResponse.resultUrl().getValue());
            } else {
                DownloadLinkDialog d = new DownloadLinkDialog(i18n.tr("{0} Error", processNameCaption));
                if (downloadServletPath != null) {
                    d.setDownloadServletPath(downloadServletPath);
                }
                d.show(serverUploadResponse.message().getValue(), i18n.tr("Download messages"), serverUploadResponse.resultUrl().getValue());
            }
        }
    }

}
