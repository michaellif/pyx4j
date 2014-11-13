/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 22, 2012
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.communityevent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.activity.AbstractVisorController;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;

import com.propertyvista.crm.rpc.services.building.communityevent.CommunityEventCrudService;
import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.domain.property.asset.building.Building;

public class CommunityEventVisorController extends AbstractVisorController {

    private final CommunityEventCrudService service;

    private final CommunityEventVisorView visor;

    private final Building building;

    public CommunityEventVisorController(IPrimePaneView parentView, Building building) {
        super(parentView);
        service = GWT.<CommunityEventCrudService> create(CommunityEventCrudService.class);
        visor = new CommunityEventVisorView(this);
        this.building = building;
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

    public void populate(DefaultAsyncCallback<EntitySearchResult<CommunityEvent>> callback) {
        EntityListCriteria<CommunityEvent> criteria = new EntityListCriteria<CommunityEvent>(CommunityEvent.class);
        criteria.eq(criteria.proto().building(), building);
        criteria.desc(criteria.proto().date());
        service.list(callback, criteria);
    }

    public void save(CommunityEvent item, DefaultAsyncCallback<Key> callback) {
        item.building().set(building);
        if (item.getPrimaryKey() == null) {
            service.create(callback, item);
        } else {
            service.save(callback, item);
        }
    }

    public void remove(CommunityEvent item, DefaultAsyncCallback<Boolean> callback) {
        if (item.isNull() || item.getPrimaryKey() == null) {
            callback.onSuccess(true);
        } else {
            service.delete(callback, item.getPrimaryKey());
        }
    }
}
