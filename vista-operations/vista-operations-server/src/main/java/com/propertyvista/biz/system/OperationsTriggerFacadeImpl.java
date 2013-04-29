/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunStatus;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.server.proc.JobUtils;

public class OperationsTriggerFacadeImpl implements OperationsTriggerFacade {

    @Override
    public void startProcess(PmcProcessType pmcProcessType) {
        Trigger trigger;
        {
            EntityQueryCriteria<Trigger> criteria = EntityQueryCriteria.create(Trigger.class);
            criteria.eq(criteria.proto().triggerType(), pmcProcessType);
            trigger = Persistence.service().retrieve(criteria);
            if (trigger == null) {
                throw new UserRuntimeException("The Trigger " + pmcProcessType + " not found");
            }
        }
        {
            EntityQueryCriteria<Run> criteria = EntityQueryCriteria.create(Run.class);
            criteria.eq(criteria.proto().trigger(), trigger);
            criteria.eq(criteria.proto().status(), RunStatus.Running);
            Run existingRun = Persistence.service().retrieve(criteria);
            if (existingRun != null) {
                throw new UserRuntimeException("The process " + pmcProcessType + " is already running");
            }
        }

        JobUtils.runNow(trigger, SystemDateManager.getDate());

    }

}
