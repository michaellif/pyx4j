/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 */
package com.propertyvista.portal.prospect.ui.application;

import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationStatus;
import com.propertyvista.portal.shared.ui.IViewerView;

public interface ApplicationStatusPageView extends IViewerView<MasterOnlineApplicationStatus> {

    public interface ApplicationStatusPagePresenter extends IViewerPresenter<MasterOnlineApplicationStatus> {

        public OnlineApplicationStatus getUserApplicationStatus(MasterOnlineApplicationStatus masterAppStatus);

    }

}
