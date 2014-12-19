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
 */
package com.propertyvista.crm.client.ui.crud.customer.common.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
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

public class IdentificationDocumentFileFolder extends VistaBoxFolder<IdentificationDocumentFile> {

    private static final I18n i18n = I18n.get(IdentificationDocumentFileFolder.class);

    public IdentificationDocumentFileFolder() {
        super(IdentificationDocumentFile.class, i18n.tr("File"));
    }

    @Override
    public VistaBoxFolderItemDecorator<IdentificationDocumentFile> createItemDecorator() {
        VistaBoxFolderItemDecorator<IdentificationDocumentFile> decor = super.createItemDecorator();
        decor.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        decor.setExpended(false);
        decor.setCaptionFormatter(new IFormatter<IdentificationDocumentFile, SafeHtml>() {
            @Override
            public SafeHtml format(IdentificationDocumentFile value) {
                if (value != null) {
                    return SafeHtmlUtils.fromString(value.file().getStringView() //
                            + (value.description().isNull() ? "" : " (" + value.description().getStringView() + ")") //
                            + (value.verified().getValue(false) ? " - " + i18n.tr("Verified!") : ""));
                }
                return null;
            }
        });
        return decor;
    }

    @Override
    protected CForm<IdentificationDocumentFile> createItemForm(IObject<?> member) {
        return new CForm<IdentificationDocumentFile>(IdentificationDocumentFile.class) {

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(IdentificationDocumentCrmUploadService.class), new VistaFileURLBuilder(
                        IdentificationDocumentFile.class));

                formPanel.append(Location.Dual, proto().file(), cfile).decorate();
                formPanel.append(Location.Dual, proto().description()).decorate();

                formPanel.h4(i18n.tr("Verification:"));
                formPanel.append(Location.Left, proto().verified()).decorate();
                formPanel.append(Location.Left, proto().notes()).decorate();

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
