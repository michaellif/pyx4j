/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.sim;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.operations.domain.payment.pad.sim.DirectDebitSimRecord;
import com.propertyvista.operations.rpc.services.sim.DirectDebitSimRecordCrudService;

public class DirectDebitSimRecordCrudServiceImpl extends AbstractCrudServiceImpl<DirectDebitSimRecord> implements DirectDebitSimRecordCrudService {

    public DirectDebitSimRecordCrudServiceImpl() {
        super(DirectDebitSimRecord.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void create(DirectDebitSimRecord entity, DirectDebitSimRecord dto) {
        // TODO find or create file
        super.create(entity, dto);
    }

}
