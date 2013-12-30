/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.entity.server.AbstractListServiceDtoImpl;

import com.propertyvista.operations.domain.payment.pad.FundsReconciliationSummary;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;
import com.propertyvista.operations.rpc.services.PadReconciliationSummaryListService;

public class PadReconciliationSummaryListServiceImpl extends AbstractListServiceDtoImpl<FundsReconciliationSummary, FundsReconciliationSummaryDTO> implements
        PadReconciliationSummaryListService {

    public PadReconciliationSummaryListServiceImpl() {
        super(FundsReconciliationSummary.class, FundsReconciliationSummaryDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }
}
