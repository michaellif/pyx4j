/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;
import java.util.Arrays;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuotationRequestParamsDTO;

public class TenantSurePurchaseServiceImpl implements TenantSurePurchaseService {

    @Override
    public void getQuotationRequestParams(AsyncCallback<TenantSureQuotationRequestParamsDTO> callback) {
        TenantSureQuotationRequestParamsDTO params = EntityFactory.create(TenantSureQuotationRequestParamsDTO.class);

        // these values are taken from the TenantSure API document: Appendix I
        params.generalLiabilityCoverageOptions().addAll(Arrays.asList(//@formatter:off
                new BigDecimal("1000000"),
                new BigDecimal("2000000"),
                new BigDecimal("5000000")
        ));//@formatter:on
        params.contentsCoverageOptions().addAll(Arrays.asList(//@formatter:off
                new BigDecimal("10000"),
                new BigDecimal("20000"),
                new BigDecimal("30000"),
                new BigDecimal("40000"),
                new BigDecimal("50000")
        ));//@formatter:on

        // these values are taken from a email that was sent to me by Arthur (insurance_items_matrix.xls)
        params.deductibleOptions().addAll(Arrays.asList(//@formatter:off
                        BigDecimal.ZERO,
                        new BigDecimal("500"),
                        new BigDecimal("1000"),
                        new BigDecimal("2000")
        ));//@formatter:on
        callback.onSuccess(params);

    }
}
