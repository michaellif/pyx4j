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
package com.propertyvista.portal.prospect.ui.application.editors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.domain.media.ProofOfAssetDocumentFile;
import com.propertyvista.portal.rpc.portal.prospect.services.ProofOfAssetDocumentProspectUploadService;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class ProofOfAssetDocumentFileFolder extends PortalBoxFolder<ProofOfAssetDocumentFile> {

    private static final I18n i18n = I18n.get(ProofOfAssetDocumentFileFolder.class);

    public ProofOfAssetDocumentFileFolder() {
        super(ProofOfAssetDocumentFile.class, i18n.tr("File"));
    }

    @Override
    protected CForm<ProofOfAssetDocumentFile> createItemForm(IObject<?> member) {
        return new DocumentEditor();
    }

    private class DocumentEditor extends CForm<ProofOfAssetDocumentFile> {

        public DocumentEditor() {
            super(ProofOfAssetDocumentFile.class);
        }

        @Override
        protected IsWidget createContent() {
            PortalFormPanel formPanel = new PortalFormPanel(this);

            CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(ProofOfAssetDocumentProspectUploadService.class), new VistaFileURLBuilder(
                    ProofOfAssetDocumentFile.class));

            formPanel.append(Location.Left, proto().file(), cfile).decorate();
            formPanel.append(Location.Left, proto().description()).decorate();

            return formPanel;
        }
    }
}
