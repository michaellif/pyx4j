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

import com.pyx4j.gwt.client.upload.UploadResponseReciver;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.dto.DownloadableUploadResponseDTO;

public class UploadResponseDownloadableReciver<R extends DownloadableUploadResponseDTO> implements UploadResponseReciver<R> {

    private static final I18n i18n = I18n.get(UploadResponseDownloadableReciver.class);

    private final String processNameCaption;

    public UploadResponseDownloadableReciver(String processNameCaption) {
        this.processNameCaption = processNameCaption;
    }

    @Override
    public void onUploadComplete(UploadResponse<R> serverUploadResponse) {
        if (serverUploadResponse.data.resultUrl().isNull()) {
            if (serverUploadResponse.data.success().getValue()) {
                MessageDialog.info(i18n.tr("{0} Complete", processNameCaption), serverUploadResponse.message);
            } else {
                MessageDialog.error(i18n.tr("{0} Error", processNameCaption), serverUploadResponse.message);
            }
        } else {
            if (serverUploadResponse.data.success().getValue()) {
                DownloadLinkDialog d = new DownloadLinkDialog(i18n.tr("{0} Complete", processNameCaption));
                d.show(serverUploadResponse.message, i18n.tr("Download processing results"), serverUploadResponse.data.resultUrl().getValue());
            } else {
                DownloadLinkDialog d = new DownloadLinkDialog(i18n.tr("{0} Error", processNameCaption));
                d.show(serverUploadResponse.message, i18n.tr("Download messages"), serverUploadResponse.data.resultUrl().getValue());
            }
        }
    }

}
