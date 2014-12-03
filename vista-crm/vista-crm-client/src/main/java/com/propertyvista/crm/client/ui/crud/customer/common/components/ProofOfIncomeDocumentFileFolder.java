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
import com.propertyvista.crm.rpc.services.lease.ProofOfIncomeDocumentCrmUploadService;
import com.propertyvista.domain.media.ProofOfIncomeDocumentFile;

public class ProofOfIncomeDocumentFileFolder extends VistaBoxFolder<ProofOfIncomeDocumentFile> {

    private static final I18n i18n = I18n.get(ProofOfIncomeDocumentFileFolder.class);

    public ProofOfIncomeDocumentFileFolder() {
        super(ProofOfIncomeDocumentFile.class, i18n.tr("File"));
    }

    @Override
    public VistaBoxFolderItemDecorator<ProofOfIncomeDocumentFile> createItemDecorator() {
        VistaBoxFolderItemDecorator<ProofOfIncomeDocumentFile> decor = super.createItemDecorator();
        decor.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        decor.setExpended(false);
        decor.setCaptionFormatter(new IFormatter<ProofOfIncomeDocumentFile, SafeHtml>() {
            @Override
            public SafeHtml format(ProofOfIncomeDocumentFile value) {
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
    protected CForm<ProofOfIncomeDocumentFile> createItemForm(IObject<?> member) {
        return new CForm<ProofOfIncomeDocumentFile>(ProofOfIncomeDocumentFile.class) {

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(ProofOfIncomeDocumentCrmUploadService.class), new VistaFileURLBuilder(
                        ProofOfIncomeDocumentFile.class));

                formPanel.append(Location.Dual, proto().file(), cfile).decorate();
                formPanel.append(Location.Dual, proto().description()).decorate();

                formPanel.h4(i18n.tr("Verification:"));
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
