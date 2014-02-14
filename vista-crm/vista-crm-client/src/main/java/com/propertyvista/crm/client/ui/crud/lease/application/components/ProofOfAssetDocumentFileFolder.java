/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 17, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.lease.ProofOfAssetDocumentCrmUploadService;
import com.propertyvista.domain.media.ProofOfAssetDocumentFile;

public class ProofOfAssetDocumentFileFolder extends VistaBoxFolder<ProofOfAssetDocumentFile> {

    private static final I18n i18n = I18n.get(ProofOfAssetDocumentFileFolder.class);

    public ProofOfAssetDocumentFileFolder() {
        super(ProofOfAssetDocumentFile.class, i18n.tr("File"));
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ProofOfAssetDocumentFile) {
            return new DocumentEditor();
        }
        return super.create(member);
    }

    private class DocumentEditor extends CEntityForm<ProofOfAssetDocumentFile> {

        public DocumentEditor() {
            super(ProofOfAssetDocumentFile.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();
            int row = -1;

            CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(ProofOfAssetDocumentCrmUploadService.class), new VistaFileURLBuilder(
                    ProofOfAssetDocumentFile.class));

            main.setWidget(++row, 0, 1, inject(proto().file(), cfile));

            return main;
        }
    }
}
