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
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.portal.rpc.portal.resident.services.insurance.InsuranceCertificateScanResidentUploadService;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class CertificateScanFolder extends PortalBoxFolder<InsuranceCertificateScan> {

    private final static I18n i18n = I18n.get(CertificateScanFolder.class);

    public CertificateScanFolder() {
        super(InsuranceCertificateScan.class, i18n.tr("Certificate Scan"));
        setOrderable(false);
    }

    @Override
    protected CForm<InsuranceCertificateScan> createItemForm(IObject<?> member) {
        return new CertificateScanViewer();
    }

    private class CertificateScanViewer extends CForm<InsuranceCertificateScan> {

        public CertificateScanViewer() {
            super(InsuranceCertificateScan.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(InsuranceCertificateScanResidentUploadService.class), new VistaFileURLBuilder(
                    InsuranceCertificateScan.class));

            formPanel.append(Location.Left, proto().file(), cfile).decorate();
            formPanel.append(Location.Left, proto().description()).decorate();

            return formPanel;
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