/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CViewer;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.GeneralInsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;

/** this is a class that supposed to implement 'polymorphic' tenant insurance status viewer */
public class TenantInsuranceStatusViewer extends CViewer<InsuranceStatusDTO> {

    public enum Styles {
        TenantInsuranceWarningText, TenantInsuranceAnchor;
    }

    @Override
    public IsWidget createContent(InsuranceStatusDTO status) {
        if (status == null) {
            return null;
        }

        if (status.sertificates().size() == 0) {
            return new NoTenantInsuranceStatusViewer().createContent(status);

        } else if (status.sertificates().get(0).isInstanceOf(GeneralInsuranceCertificateSummaryDTO.class)) {
            return new TenantSureInsuranceStatusViewer().createContent(status.sertificates().get(0).<TenantSureCertificateSummaryDTO> cast());

        } else if (status.sertificates().get(0).isInstanceOf(GeneralInsuranceCertificateSummaryDTO.class)) {
            return new GeneralTenantInsuranceStatusViewer().createContent(status.sertificates().get(0).<GeneralInsuranceCertificateSummaryDTO> cast());

        } else {
            throw new Error("A viewer for current insurance status was not found!");
        }
    }
}
