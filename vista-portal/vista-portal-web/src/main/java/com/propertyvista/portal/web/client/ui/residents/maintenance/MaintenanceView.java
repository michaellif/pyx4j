/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.maintenance;

import java.util.Vector;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;

import com.propertyvista.dto.MaintenanceRequestDTO;

public interface MaintenanceView extends IsWidget {

    interface Presenter {

        void createNewRequest();

        void viewRequest(MaintenanceRequestDTO requests);

        void cancelRequest(Key requestId);

        void rateRequest(Key requestId, Integer rate);
    }

    void setPresenter(Presenter presenter);

    void populateOpenRequests(Vector<MaintenanceRequestDTO> openRequests);

    void populateClosedRequests(Vector<MaintenanceRequestDTO> historyRequests);
}
