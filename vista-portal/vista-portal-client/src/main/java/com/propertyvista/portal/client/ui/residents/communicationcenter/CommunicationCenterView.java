/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.communicationcenter;

import java.util.Vector;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.dto.MaintenanceRequestDTO;

//TODO: change the MaintenanceRequestDTO into CommunicationModuleSomenthingDTO
public interface CommunicationCenterView extends IsWidget {

    interface Presenter {

        void createNewRequest();

        void editRequest(MaintenanceRequestDTO requests);

        void cancelRequest(MaintenanceRequestDTO request);

        void rateRequest(MaintenanceRequestDTO request, Integer rate);
    }

    void setPresenter(Presenter presenter);

    void populateOpenRequests(Vector<MaintenanceRequestDTO> openRequests);

    void populateHistoryRequests(Vector<MaintenanceRequestDTO> historyRequests);
}
