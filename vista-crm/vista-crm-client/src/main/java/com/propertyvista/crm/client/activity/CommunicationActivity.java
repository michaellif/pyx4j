/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.communication.CommunicationView;
import com.propertyvista.dto.MessageDTO;

public class CommunicationActivity extends AbstractActivity implements CommunicationView.CommunicationPresenter {

    private final CommunicationView view;

    public CommunicationActivity(Place place) {
        view = CrmSite.getViewFactory().getView(CommunicationView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        if (!SecurityController.check(DataModelPermission.permissionRead(MessageDTO.class))) {
            return;
        }
        panel.setWidget(view);
        view.populate(null);
    }
}
