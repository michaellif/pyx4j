/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.rpc.services.scheduler;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.Trigger;

public interface TriggerCrudService extends AbstractCrudService<Trigger> {

    public void runImmediately(AsyncCallback<Run> callback, Trigger triggerStub);

    public void runImmediatelyForDate(AsyncCallback<Run> callback, Trigger triggerStub, Date executionDate);
}
