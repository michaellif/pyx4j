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
package com.propertyvista.crm.client.visor.scheduler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.activity.AbstractVisorController;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;

import com.propertyvista.crm.rpc.services.communication.broadcasttemplate.SchedulerCrudService;
import com.propertyvista.domain.communication.BroadcastTemplate;
import com.propertyvista.domain.communication.Schedule;

public class SchedulerVisorController extends AbstractVisorController {

    private final SchedulerCrudService service;

    private final SchedulerVisorView visor;

    private final BroadcastTemplate template;

    public SchedulerVisorController(IPrimePaneView<?> parentView, BroadcastTemplate template) {
        super(parentView);
        service = GWT.<SchedulerCrudService> create(SchedulerCrudService.class);
        visor = new SchedulerVisorView(this);
        this.template = template;
    }

    @Override
    public void show() {
        visor.populate(new Command() {
            @Override
            public void execute() {
                getParentView().showVisor(visor);
            }
        });
    }

    public void populate(DefaultAsyncCallback<EntitySearchResult<Schedule>> callback) {
        EntityListCriteria<Schedule> criteria = new EntityListCriteria<Schedule>(Schedule.class);
        criteria.eq(criteria.proto().template(), template);
        service.list(callback, criteria);
    }

    public void save(Schedule item, DefaultAsyncCallback<Key> callback) {
        item.template().set(template);
        if (item.getPrimaryKey() == null) {
            service.create(callback, item);
        } else {
            service.save(callback, item);
        }
    }

    public void remove(Schedule item, DefaultAsyncCallback<Boolean> callback) {
        if (item.isNull() || item.getPrimaryKey() == null) {
            callback.onSuccess(true);
        } else {
            service.delete(callback, item.getPrimaryKey());
        }
    }

}
