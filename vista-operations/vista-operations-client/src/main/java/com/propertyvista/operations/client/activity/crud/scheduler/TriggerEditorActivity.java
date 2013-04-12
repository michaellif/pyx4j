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
package com.propertyvista.operations.client.activity.crud.scheduler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerEditorView;
import com.propertyvista.operations.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.operations.rpc.TriggerDTO;
import com.propertyvista.operations.rpc.services.scheduler.TriggerCrudService;

public class TriggerEditorActivity extends AbstractEditorActivity<TriggerDTO> {

    @SuppressWarnings("unchecked")
    public TriggerEditorActivity(CrudAppPlace place) {
        super(place, ManagementVeiwFactory.instance(TriggerEditorView.class), (AbstractCrudService<TriggerDTO>) GWT.create(TriggerCrudService.class),
                TriggerDTO.class);
    }

    @Override
    protected void createNewEntity(AsyncCallback<TriggerDTO> callback) {
        TriggerDTO process = EntityFactory.create(getEntityClass());
        process.created().setValue(ClientContext.getServerDate());
        callback.onSuccess(process);
    }

}
