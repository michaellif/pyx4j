/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.domain.financial.billing.Bill;

public class BillCrudServiceImpl extends AbstractCrudServiceImpl<Bill> implements BillCrudService {

    public BillCrudServiceImpl() {
        super(Bill.class);
    }

    @Override
    protected void enhanceRetrieved(Bill entity) {
        // load detached entities:
        Persistence.service().retrieve(entity.charges());
    }

    @Override
    protected void persist(Bill entity) {
        throw new IllegalArgumentException();
    }

}
