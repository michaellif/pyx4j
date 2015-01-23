/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2015
 * @author arminea
 */
package com.propertyvista.crm.rpc.services.communication.broadcasttemplate;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.communication.BroadcastTemplate;
import com.propertyvista.domain.communication.Schedule;

public interface SchedulerCrudService extends AbstractCrudService<Schedule> {
    @Transient
    interface SchedulerInitializationData extends InitializationData {

        BroadcastTemplate template();
    }

}
