/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2015
 * @author michaellif
 */
package com.propertyvista.crm.client.activity.crud.communication;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.communication.BroadcustEventEditorView;
import com.propertyvista.crm.client.ui.crud.communication.BroadcustEventEditorView.BroadcustEventEditorPresenter;
import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.domain.communication.BroadcustEvent;

public class BroadcustEventEditorActivity extends CrmEditorActivity<BroadcustEvent> implements BroadcustEventEditorPresenter {

    public BroadcustEventEditorActivity(CrudAppPlace place) {
        super(BroadcustEvent.class, place, CrmSite.getViewFactory().getView(BroadcustEventEditorView.class), GWT
                .<AbstractCrudService<BroadcustEvent>> create(MessageCategoryCrudService.class));
    }

}
