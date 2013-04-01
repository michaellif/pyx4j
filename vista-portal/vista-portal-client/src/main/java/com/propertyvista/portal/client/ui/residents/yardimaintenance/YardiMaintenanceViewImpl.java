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
package com.propertyvista.portal.client.ui.residents.yardimaintenance;

import java.util.Vector;

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.dto.YardiServiceRequestDTO;

public class YardiMaintenanceViewImpl extends FlowPanel implements YardiMaintenanceView {

    private final YardiMaintenanceViewList list;

    public YardiMaintenanceViewImpl() {
        list = new YardiMaintenanceViewList();
        add(list);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        list.setPresenter(presenter);
    }

    @Override
    public void populateOpenRequests(Vector<YardiServiceRequestDTO> openRequests) {
        list.populateOpenRequests(openRequests);
    }

    @Override
    public void populateHistoryRequests(Vector<YardiServiceRequestDTO> historyRequests) {
        list.populateHistoryRequests(historyRequests);
    }
}
