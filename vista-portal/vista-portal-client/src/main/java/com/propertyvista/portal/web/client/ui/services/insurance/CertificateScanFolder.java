/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 29, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateDoc;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.portal.rpc.portal.web.services.services.InsuranceCertificateScanUploadService;
import com.propertyvista.portal.web.client.ui.util.decorators.FormWidgetDecoratorBuilder;

public class CertificateScanFolder extends VistaBoxFolder<InsuranceCertificateDoc> {

    private final static I18n i18n = I18n.get(CertificateScanFolder.class);

    public CertificateScanFolder() {
        super(InsuranceCertificateDoc.class, i18n.tr("Certificate Scan"));
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof InsuranceCertificateDoc) {
            return new CertificateScanViewer();
        }
        return super.create(member);
    }

    private class CertificateScanViewer extends CEntityForm<InsuranceCertificateDoc> {

        public CertificateScanViewer() {
            super(InsuranceCertificateDoc.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            content.setWidget(
                    ++row,
                    0,
                    new FormWidgetDecoratorBuilder(inject(proto().scan(),
                            new CFile<InsuranceCertificateScan>(
                                    GWT.<InsuranceCertificateScanUploadService> create(InsuranceCertificateScanUploadService.class),
                                    new FileURLBuilder<InsuranceCertificateScan>() {

                                        @Override
                                        public String getUrl(InsuranceCertificateScan file) {
                                            return MediaUtils.createInsuranceCertificateScanUrl(file);
                                        }
                                    })), 250).build());

            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description()), 200).mockValue("Description").build());

            return content;
        }
    }

}