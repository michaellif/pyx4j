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
package com.propertyvista.portal.resident.ui.services.insurance;

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
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.portal.rpc.portal.resident.services.services.InsuranceCertificateScanResidentUploadService;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class CertificateScanFolder extends PortalBoxFolder<InsuranceCertificateScan> {

    private final static I18n i18n = I18n.get(CertificateScanFolder.class);

    public CertificateScanFolder() {
        super(InsuranceCertificateScan.class, i18n.tr("Certificate Scan"));
        setOrderable(false);
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof InsuranceCertificateScan) {
            return (T) new CertificateScanViewer();
        }
        return super.create(member);
    }

    private class CertificateScanViewer extends CEntityForm<InsuranceCertificateScan> {

        public CertificateScanViewer() {
            super(InsuranceCertificateScan.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(InsuranceCertificateScanResidentUploadService.class), new VistaFileURLBuilder(
                    InsuranceCertificateScan.class));

            content.setWidget(++row, 0, 1, inject(proto().file(), cfile, new FieldDecoratorBuilder().customLabel("").labelWidth("0px").build()));
            content.setWidget(++row, 0, inject(proto().description(), new FieldDecoratorBuilder().build()));

            return content;
        }

        @Override
        public void generateMockData() {
            get(proto().description()).setMockValue("Description");
        }

        @Override
        public void addValidations() {
            super.addValidations();
            get(proto().file()).setMandatory(true);
        }
    }

}