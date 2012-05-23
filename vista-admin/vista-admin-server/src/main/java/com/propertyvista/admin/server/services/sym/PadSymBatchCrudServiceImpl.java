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
package com.propertyvista.admin.server.services.sym;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.admin.domain.payment.pad.sym.PadSymBatch;
import com.propertyvista.admin.rpc.services.sym.PadSymBatchCrudService;

public class PadSymBatchCrudServiceImpl extends AbstractCrudServiceImpl<PadSymBatch> implements PadSymBatchCrudService {

    public PadSymBatchCrudServiceImpl() {
        super(PadSymBatch.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

}
