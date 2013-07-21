/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.simulator;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.operations.domain.payment.pad.simulator.PadSimBatch;
import com.propertyvista.operations.rpc.services.simulator.PadSimBatchCrudService;

public class PadSimBatchCrudServiceImpl extends AbstractCrudServiceImpl<PadSimBatch> implements PadSimBatchCrudService {

    public PadSimBatchCrudServiceImpl() {
        super(PadSimBatch.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

}
