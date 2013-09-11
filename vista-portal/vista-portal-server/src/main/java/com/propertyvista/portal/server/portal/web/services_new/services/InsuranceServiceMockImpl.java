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
package com.propertyvista.portal.server.portal.web.services_new.services;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.NoInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.OtherProviderInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.services_new.services.InsuranceService;

public class InsuranceServiceMockImpl implements InsuranceService {

    @Override
    public void retreiveInsuranceStatus(AsyncCallback<InsuranceStatusDTO> callback) {
        if (false) {
            NoInsuranceStatusDTO statusDTO = EntityFactory.create(NoInsuranceStatusDTO.class);
            statusDTO.noInsuranceStatusMessage().setValue("Our records indicate you do not have valid tenant insurance.");
            statusDTO.tenantInsuranceInvitation().setValue(
                    "As per your lease agreement, you must obtain and provide the landlord with proof of tenant insurance.");
            callback.onSuccess(statusDTO);
        } else if (false) {
            OtherProviderInsuranceStatusDTO statusDTO = EntityFactory.create(OtherProviderInsuranceStatusDTO.class);
            statusDTO.insuranceProvider().setValue("Other Insurance");
            statusDTO.liabilityCoverage().setValue(new BigDecimal("1000000"));
            statusDTO.expiryDate().setValue(new LogicalDate());
            callback.onSuccess(statusDTO);
        } else if (true) {
            TenantSureInsuranceStatusDTO statusDTO = EntityFactory.create(TenantSureInsuranceStatusDTO.class);
            statusDTO.insuranceProvider().setValue("TenantSure Insurance");
            statusDTO.insuranceCertificateNumber().setValue("ABC12345");
            statusDTO.liabilityCoverage().setValue(new BigDecimal("1000000"));
            statusDTO.contentsCoverage().setValue(new BigDecimal("100000"));
            statusDTO.inceptionDate().setValue(new LogicalDate());
            statusDTO.expiryDate().setValue(new LogicalDate());
            callback.onSuccess(statusDTO);
        }
    }

}
