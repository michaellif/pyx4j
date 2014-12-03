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
import com.propertyvista.crm.rpc.services.lease.IdentificationDocumentCrmUploadService;
import com.propertyvista.domain.media.IdentificationDocumentFile;

public class IdFileUploaderFolder extends VistaBoxFolder<IdentificationDocumentFile> {

    private static final I18n i18n = I18n.get(IdFileUploaderFolder.class);

    public IdFileUploaderFolder() {
        super(IdentificationDocumentFile.class, i18n.tr("File"));
    }

    @Override
    public VistaBoxFolderItemDecorator<IdentificationDocumentFile> createItemDecorator() {
        VistaBoxFolderItemDecorator<IdentificationDocumentFile> decor = super.createItemDecorator();
        decor.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected CForm<IdentificationDocumentFile> createItemForm(IObject<?> member) {
        return new DocumentEditor();
    }

    private class DocumentEditor extends CForm<IdentificationDocumentFile> {

        public DocumentEditor() {
            super(IdentificationDocumentFile.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(IdentificationDocumentCrmUploadService.class), new VistaFileURLBuilder(
                    IdentificationDocumentFile.class));

            formPanel.append(Location.Dual, proto().file(), cfile).decorate();
            formPanel.append(Location.Dual, proto().description()).decorate();

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().description()).setVisible(isEditable() || !getValue().description().isNull());
        }
    }
}
