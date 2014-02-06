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

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.tenant.insurance.errors.CfcApiException;
import com.propertyvista.biz.tenant.insurance.errors.TooManyPreviousClaimsException;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyClient;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;

public interface CfcApiAdapterFacade {

    public enum CancellationType {

        RETROACTIVE, PROACTIVE

    }

    public enum ReinstatementType {

        REINSTATEMENT_PROACTIVE, REINSTATEMENT_RETROACTIVE

    }

    /**
     * Registers a client in CFC system.
     * 
     * @return reference ID for the client
     */
    public String createClient(Tenant tenant, String tenantName, String tenantPhone) throws CfcApiException;

    public TenantSureQuoteDTO getQuote(TenantSureInsurancePolicyClient client, TenantSureCoverageDTO coverageRequest) throws TooManyPreviousClaimsException,
            CfcApiException;

    /**
     * Binds quote in CFC system
     * 
     * @return insurance certificate number
     */
    public String bindQuote(String quoteId) throws CfcApiException;

    /** Send policy related documentation to the list of e-mail addresses */
    public void requestDocument(String quoteId, List<String> emails) throws CfcApiException;

    public LogicalDate cancel(String policyId, CancellationType cancellationType, String toAddress) throws CfcApiException;

    public void reinstate(String policyId, ReinstatementType reinstatementType, String toAddress) throws CfcApiException;

}