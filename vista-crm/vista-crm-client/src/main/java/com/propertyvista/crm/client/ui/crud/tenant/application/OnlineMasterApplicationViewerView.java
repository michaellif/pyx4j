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
package com.propertyvista.crm.client.ui.crud.tenant.application;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.crud.form.IViewerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.OnlineApplicationDTO;
import com.propertyvista.dto.OnlineMasterApplicationDTO;
import com.propertyvista.dto.TenantInLeaseDTO;

public interface OnlineMasterApplicationViewerView extends IViewerView<OnlineMasterApplicationDTO> {

    interface Presenter extends IViewerView.Presenter {

        void retrieveUsers(AsyncCallback<List<ApplicationUserDTO>> callback);

        void inviteUsers(List<ApplicationUserDTO> users);
    }

    IListerView<OnlineApplicationDTO> getApplicationsView();

    IListerView<TenantInLeaseDTO> getTenantsView();
}