/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.editors;

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
import com.propertyvista.domain.media.ProofOfEmploymentDocumentFile;
import com.propertyvista.portal.rpc.portal.prospect.services.ProofOfEmploymentDocumentProspectUploadService;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class ProofOfEmploymentDocumentFileFolder extends PortalBoxFolder<ProofOfEmploymentDocumentFile> {

    private static final I18n i18n = I18n.get(ProofOfEmploymentDocumentFileFolder.class);

    public ProofOfEmploymentDocumentFileFolder() {
        super(ProofOfEmploymentDocumentFile.class, i18n.tr("Document"));
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ProofOfEmploymentDocumentFile) {
            return new DocumentEditor();
        }
        return super.create(member);
    }

    private class DocumentEditor extends CEntityForm<ProofOfEmploymentDocumentFile> {

        public DocumentEditor() {
            super(ProofOfEmploymentDocumentFile.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();
            int row = -1;

            CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(ProofOfEmploymentDocumentProspectUploadService.class), new VistaFileURLBuilder(
                    ProofOfEmploymentDocumentFile.class));

            main.setWidget(++row, 0, 1, inject(proto().file(), cfile));

            return main;
        }
    }
}
