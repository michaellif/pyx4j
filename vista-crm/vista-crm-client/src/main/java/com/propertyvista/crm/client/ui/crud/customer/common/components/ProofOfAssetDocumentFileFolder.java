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
package com.propertyvista.crm.client.ui.crud.customer.common.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.rpc.services.lease.ProofOfAssetDocumentCrmUploadService;
import com.propertyvista.domain.media.ProofOfAssetDocumentFile;

public class ProofOfAssetDocumentFileFolder extends VistaBoxFolder<ProofOfAssetDocumentFile> {

    private static final I18n i18n = I18n.get(ProofOfAssetDocumentFileFolder.class);

    public ProofOfAssetDocumentFileFolder() {
        super(ProofOfAssetDocumentFile.class, i18n.tr("File"));
    }

    @Override
    public VistaBoxFolderItemDecorator<ProofOfAssetDocumentFile> createItemDecorator() {
        VistaBoxFolderItemDecorator<ProofOfAssetDocumentFile> decor = super.createItemDecorator();
        decor.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected CForm<ProofOfAssetDocumentFile> createItemForm(IObject<?> member) {
        return new CForm<ProofOfAssetDocumentFile>(ProofOfAssetDocumentFile.class) {

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(ProofOfAssetDocumentCrmUploadService.class), new VistaFileURLBuilder(
                        ProofOfAssetDocumentFile.class));

                formPanel.append(Location.Dual, proto().file(), cfile).decorate();
                formPanel.append(Location.Dual, proto().description()).decorate();

                formPanel.h4(i18n.tr("Verification"));
                formPanel.append(Location.Left, proto().verified()).decorate();
                formPanel.append(Location.Right, proto().verifiedBy()).decorate();
                formPanel.append(Location.Right, proto().verifiedOn()).decorate();

                return formPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().verifiedBy()).setVisible(getValue().verified().getValue(false));
                get(proto().verifiedOn()).setVisible(getValue().verified().getValue(false));
            }
        };
    }
}
