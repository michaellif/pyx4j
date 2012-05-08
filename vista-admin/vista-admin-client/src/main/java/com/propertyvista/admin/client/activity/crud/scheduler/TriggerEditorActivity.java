/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.scheduler;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.ui.crud.scheduler.trigger.TriggerEditorView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.domain.scheduler.ScheduleType;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.rpc.services.scheduler.TriggerCrudService;

public class TriggerEditorActivity extends EditorActivityBase<Trigger> {

    @SuppressWarnings("unchecked")
    public TriggerEditorActivity(CrudAppPlace place) {
        super(place, ManagementVeiwFactory.instance(TriggerEditorView.class), (AbstractCrudService<Trigger>) GWT.create(TriggerCrudService.class),
                Trigger.class);
    }

    @Override
    protected void createNewEntity(AsyncCallback<Trigger> callback) {
        Trigger process = EntityFactory.create(getEntityClass());
        process.created().setValue(new Date());
        process.schedules().$().repeatType().setValue(ScheduleType.Weekly);
        process.schedules().$().repeatEvery().setValue(1);
        process.schedules().$().startsOn().setValue(new LogicalDate());

        callback.onSuccess(process);
    }

}
