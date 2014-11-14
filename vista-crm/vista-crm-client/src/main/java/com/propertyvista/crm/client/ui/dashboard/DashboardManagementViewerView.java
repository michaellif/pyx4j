/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.dashboard;

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.CrmUser;

public interface DashboardManagementViewerView extends IPrimeViewerView<DashboardMetadata> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void takeOwnership(DashboardMetadata dashboardMetadatStub);

        void changeOwnership(DashboardMetadata createIdentityStub, CrmUser newOwner);

    }

    void setTakeOwnershipEnabled(boolean isEnabled);

    void setChangeOwnershipEnabled(boolean isEnabled);

}
