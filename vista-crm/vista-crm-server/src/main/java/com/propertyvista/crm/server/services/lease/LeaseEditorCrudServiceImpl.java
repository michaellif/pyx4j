/*
 *
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.crm.rpc.services.lease.LeaseEditorCrudService;
import com.propertyvista.crm.server.services.lease.common.LeaseEditorCrudServiceBaseImpl;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorCrudServiceImpl extends LeaseEditorCrudServiceBaseImpl<LeaseDTO> implements LeaseEditorCrudService {

    private final static I18n i18n = I18n.get(LeaseEditorCrudServiceImpl.class);

    public LeaseEditorCrudServiceImpl() {
        super(LeaseDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseDTO dto) {
        super.enhanceRetrieved(in, dto);

        dto.transactionHistory().set(ServerSideFactory.create(ARFacade.class).getTransactionHistory(dto.billingAccount()));
    }
}