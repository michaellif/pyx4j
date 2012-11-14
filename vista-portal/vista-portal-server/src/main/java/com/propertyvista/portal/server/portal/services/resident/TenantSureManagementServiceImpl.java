/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.portal.services.resident.TenantSureManagementService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePremiumTaxDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceDetailedStatusDTO;

public class TenantSureManagementServiceImpl implements TenantSureManagementService {

    @Override
    public void getTenantSureDetailedStatus(AsyncCallback<TenantSureTenantInsuranceDetailedStatusDTO> callback) {
        TenantSureTenantInsuranceDetailedStatusDTO status = EntityFactory.create(TenantSureTenantInsuranceDetailedStatusDTO.class);
        status.quote().grossPremium().setValue(new BigDecimal("1003"));
        status.quote().underwriterFee().setValue(new BigDecimal("55.51"));
        TenantSurePremiumTaxDTO tax = status.quote().taxBreakdown().$();
        tax.taxName().setValue("HST");
        tax.absoluteAmount().setValue(new BigDecimal("52.99"));
        status.quote().taxBreakdown().add(tax);
        status.quote().totalPayable().setValue(new BigDecimal("9000.01"));
        TenantSureMessageDTO message = status.messages().$();
        message.message().setValue("Your insurance is about to expire, we strongly advise you to stop playing with matches :)");
        status.messages().add(message);
        callback.onSuccess(status);
    }

}
