/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.services.customer.InsuranceCertificateScanCrmUploadService;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;

public class InsuranceCertificateDocFolder extends VistaBoxFolder<InsuranceCertificateScan> {

    private final static I18n i18n = I18n.get(InsuranceCertificateDocFolder.class);

    public InsuranceCertificateDocFolder() {
        super(InsuranceCertificateScan.class, i18n.tr("Certificate Scan"));
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof InsuranceCertificateScan) {
            return new CertificateScanViewer();
        }
        return super.create(member);
    }

    private class CertificateScanViewer extends CEntityForm<InsuranceCertificateScan> {

        public CertificateScanViewer() {
            super(InsuranceCertificateScan.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            content.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().file(), new CFile(GWT.<UploadService<?, ?>> create(InsuranceCertificateScanCrmUploadService.class),
                            new VistaFileURLBuilder(InsuranceCertificateScan.class))), 250).build());

            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description())).componentWidth("300px").build());

            return content;
        }
    }

}
