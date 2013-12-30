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
package com.propertyvista.operations.server.services.simulator;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimRecord;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile.DirectDebitSimFileStatus;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimRecordCrudService;

public class DirectDebitSimRecordCrudServiceImpl extends AbstractCrudServiceImpl<DirectDebitSimRecord> implements DirectDebitSimRecordCrudService {

    public DirectDebitSimRecordCrudServiceImpl() {
        super(DirectDebitSimRecord.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void create(DirectDebitSimRecord entity, DirectDebitSimRecord dto) {
        // find or create file
        if (entity.file().isNull()) {
            EntityQueryCriteria<DirectDebitSimFile> criteria = EntityQueryCriteria.create(DirectDebitSimFile.class);
            criteria.eq(criteria.proto().status(), DirectDebitSimFileStatus.New);
            DirectDebitSimFile directDebitSimFile = Persistence.service().retrieve(criteria);
            if (directDebitSimFile == null) {
                directDebitSimFile = DirectDebitSimFileCrudServiceImpl.createNewFile(EntityFactory.create(DirectDebitSimFile.class));
            }
            entity.file().set(directDebitSimFile);
        }
        super.create(entity, dto);
    }
}
