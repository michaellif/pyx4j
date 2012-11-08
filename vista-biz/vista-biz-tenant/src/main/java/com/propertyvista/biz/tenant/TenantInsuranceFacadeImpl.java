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
package com.propertyvista.biz.tenant;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;

// TODO this is a mockup
public class TenantInsuranceFacadeImpl implements TenantInsuranceFacade {

    @Override
    public TenantInsuranceStatusDTO getInsuranceStatus(Tenant tenantStub) {
        NoInsuranceTenantInsuranceStatusDTO noInsurance = EntityFactory.create(NoInsuranceTenantInsuranceStatusDTO.class);
        noInsurance.minimumRequiredLiability().setValue(new BigDecimal("90000000"));
        return noInsurance;
    }

}
