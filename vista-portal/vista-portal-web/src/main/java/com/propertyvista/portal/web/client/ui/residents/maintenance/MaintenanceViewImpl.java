/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.maintenance;

import java.util.Vector;

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceViewImpl extends FlowPanel implements MaintenanceView {

    private final MaintenanceViewList list;

    public MaintenanceViewImpl() {
        list = new MaintenanceViewList();
        add(list);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        list.setPresenter(presenter);
    }

    @Override
    public void populateOpenRequests(Vector<MaintenanceRequestDTO> openRequests) {
        list.populateOpenRequests(openRequests);
    }

    @Override
    public void populateClosedRequests(Vector<MaintenanceRequestDTO> historyRequests) {
        list.populateClosedRequests(historyRequests);
    }
}
