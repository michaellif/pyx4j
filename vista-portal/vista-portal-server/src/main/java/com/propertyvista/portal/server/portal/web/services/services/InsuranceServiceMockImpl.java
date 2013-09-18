/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.services;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.GeneralInsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.InsuranceService;

public class InsuranceServiceMockImpl implements InsuranceService {

    @Override
    public void retreiveInsuranceStatus(AsyncCallback<InsuranceStatusDTO> callback) {

        InsuranceStatusDTO statusDTO = EntityFactory.create(InsuranceStatusDTO.class);

        if (false) {
            GeneralInsuranceCertificateSummaryDTO summaryDTO = EntityFactory.create(GeneralInsuranceCertificateSummaryDTO.class);
            summaryDTO.insuranceProvider().setValue("Other Insurance");
            summaryDTO.liabilityCoverage().setValue(new BigDecimal("1000000"));
            summaryDTO.expiryDate().setValue(new LogicalDate());
            statusDTO.sertificates().add(summaryDTO);
        } else if (false) {
            TenantSureCertificateSummaryDTO summaryDTO = EntityFactory.create(TenantSureCertificateSummaryDTO.class);
            summaryDTO.insuranceProvider().setValue("TenantSure Insurance");
            summaryDTO.insuranceCertificateNumber().setValue("ABC12345");
            summaryDTO.liabilityCoverage().setValue(new BigDecimal("1000000"));
            summaryDTO.contentsCoverage().setValue(new BigDecimal("100000"));
            summaryDTO.inceptionDate().setValue(new LogicalDate());
            summaryDTO.expiryDate().setValue(new LogicalDate());
            statusDTO.sertificates().add(summaryDTO);
        } else {
            statusDTO.minimumRequiredLiability().setValue(new BigDecimal("1000000"));
        }
        callback.onSuccess(statusDTO);

    }

}
