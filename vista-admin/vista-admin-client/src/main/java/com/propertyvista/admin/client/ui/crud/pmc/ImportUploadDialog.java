/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.upload.UploadPanel;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.ImportUploadService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class ImportUploadDialog extends VerticalPanel implements OkCancelOption, OkOptionText {

    private final static Logger log = LoggerFactory.getLogger(ImportUploadDialog.class);

    private static I18n i18n = I18nFactory.getI18n(ImportUploadDialog.class);

    private final UploadPanel uploadPanel;

    private final Dialog dialog;

    private ImportUploadDialog(final PmcDTO pmc) {
        dialog = new Dialog(i18n.tr("Upload Import.xml"), this);

        uploadPanel = new UploadPanel((UploadService) GWT.create(ImportUploadService.class)) {
            @Override
            protected IEntity getUploadData() {
                return pmc;
            }

            @Override
            protected void onUploadComplete(String id) {
                dialog.hide();
            }

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

        };
        uploadPanel.setSupportedExtensions("xml");
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.uploadServletMapping);
        uploadPanel.setSize("400px", "100px");
        uploadPanel.getElement().getStyle().setMarginTop(50, Style.Unit.PX);
        uploadPanel.getElement().getStyle().setPaddingLeft(35, Style.Unit.PX);

        dialog.setBody(uploadPanel);
        dialog.setPixelSize(460, 150);
    }

    public static void show(PmcDTO pmc) {
        new ImportUploadDialog(pmc).dialog.show();
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
