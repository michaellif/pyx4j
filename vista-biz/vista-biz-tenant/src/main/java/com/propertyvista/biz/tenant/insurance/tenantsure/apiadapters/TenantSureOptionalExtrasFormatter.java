/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters;

import com.propertyvista.biz.tenant.insurance.TenantSureDeductibleOption;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;

public class TenantSureOptionalExtrasFormatter implements ITenantSureOptionalExtrasFormatter {

    @Override
    public String formatOptionalExtras(TenantSureCoverageDTO coverageRequest, Tenant tenant) {
        StringBuilder optionalExtras = new StringBuilder();

        optionalExtras.append(format("MonthlyRevenue", tenant.lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue()
                .toPlainString()));
        optionalExtras.append(format("Deductible", TenantSureDeductibleOption.deductibleOf(coverageRequest.deductible().getValue()).amount().toPlainString()));
        optionalExtras.append(format("Smoker", String.valueOf(coverageRequest.smoker().isBooleanTrue())));
        optionalExtras.append(format("Claims", String.valueOf(String.valueOf(coverageRequest.numberOfPreviousClaims().getValue().numericValue()))));
        optionalExtras.append(format("Alarm", String.valueOf(tenant.lease().unit().building().info().hasFireAlarm().isBooleanTrue())));
        optionalExtras.append(format("Sprinklers", String.valueOf(tenant.lease().unit().building().info().hasSprinklers().isBooleanTrue())));
        optionalExtras.append(format("BCEQ", String.valueOf(tenant.lease().unit().building().info().hasEarthquakes().isBooleanTrue())));

        return optionalExtras.toString();
    }

    private String format(String key, String value) {
        return key + '=' + value + ";";
    }
}
