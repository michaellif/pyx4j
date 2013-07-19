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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.operations.domain.payment.pad.sim.DirectDebitSimFile;
import com.propertyvista.operations.domain.payment.pad.sim.DirectDebitSimFile.DirectDebitSimFileStatus;
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
        // find or create file
        if (entity.file().isNull()) {
            EntityQueryCriteria<DirectDebitSimFile> criteria = EntityQueryCriteria.create(DirectDebitSimFile.class);
            criteria.eq(criteria.proto().status(), DirectDebitSimFileStatus.New);
            DirectDebitSimFile directDebitSimFile = Persistence.service().retrieve(criteria);
            if (directDebitSimFile == null) {
                directDebitSimFile = EntityFactory.create(DirectDebitSimFile.class);
                directDebitSimFile.status().setValue(DirectDebitSimFileStatus.New);
                Persistence.service().persist(directDebitSimFile);
            }
            entity.file().set(directDebitSimFile);
        }
        super.create(entity, dto);
    }
}
