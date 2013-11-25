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
package com.propertyvista.crm.client.ui.crud.customer.tenant;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.ui.prime.form.IViewer;

import com.propertyvista.crm.client.visor.maintenance.MaintenanceRequestVisorController;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;

public interface TenantViewerView extends IViewer<TenantDTO> {

    interface Presenter extends IViewer.Presenter {

        MaintenanceRequestVisorController getMaintenanceRequestVisorController();

        void goToCreateScreening();

        void goToCreateMaintenanceRequest();

        void goToChangePassword(Key tenantPrincipalPk, String tenantName);

        void getPortalRegistrationInformation();

        void viewDeletedPaps();
    }

    public void displayPortalRegistrationInformation(TenantPortalAccessInformationDTO info);
}