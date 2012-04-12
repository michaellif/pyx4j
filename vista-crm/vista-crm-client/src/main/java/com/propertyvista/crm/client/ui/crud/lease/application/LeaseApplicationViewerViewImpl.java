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
package com.propertyvista.crm.client.ui.crud.lease.application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lease.LeaseApplication.Status;
import com.propertyvista.dto.LeaseApplicationDTO;

public class LeaseApplicationViewerViewImpl extends CrmViewerViewImplBase<LeaseApplicationDTO> implements LeaseApplicationViewerView {

    private static final I18n i18n = I18n.get(LeaseApplicationViewerViewImpl.class);

    private final Button onlineApplication;

    public LeaseApplicationViewerViewImpl() {
        super(CrmSiteMap.Tenants.LeaseApplication.class);

        onlineApplication = new Button(i18n.tr("Start Online Application"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseApplicationViewerView.Presenter) presenter).startOnlineApplication();
            }
        });
        addHeaderToolbarTwoItem(onlineApplication.asWidget());
    }

    @Override
    public void reset() {
        onlineApplication.setVisible(false);
    }

    @Override
    public void populate(LeaseApplicationDTO value) {
        super.populate(value);

        Status status = value.leaseApplication().status().getValue();

        // set buttons state:
        if (!value.unit().isNull()) {
            onlineApplication.setVisible(status != Status.OnlineApplicationInProgress);
        }
    }
}