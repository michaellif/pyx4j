/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.util.List;

import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

public interface ICfcApiClient {

    /**
     * Registers a client in CFC system.
     * 
     * @return reference ID for the client
     */
    public String createClient(Tenant tenant);

    public TenantSureQuoteDTO getQuote(InsuranceTenantSureClient client, TenantSureCoverageDTO coverageRequest);

    /**
     * Binds quote in CFC system
     * 
     * @return insurance certificate number
     */
    public String bindQuote(String quoteId);

    /** Send policy related documentation to the list of e-mail addresses */
    public void requestDocument(String quoteId, List<String> emails);

}