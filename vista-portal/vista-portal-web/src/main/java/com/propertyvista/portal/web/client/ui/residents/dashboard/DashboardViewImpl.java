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
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.dashboard;

import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.web.client.ui.residents.ViewBase;
import com.propertyvista.portal.web.client.ui.residents.ViewImpl;

public class DashboardViewImpl extends ViewImpl<TenantDashboardDTO> implements DashboardView {

    public DashboardViewImpl() {
        super(new DashboardForm(), true, true);
    }

    @Override
    public void setPresenter(ViewBase.Presenter<TenantDashboardDTO> presenter) {
        ((DashboardForm) getForm()).setPresenter((DashboardView.Presenter) presenter);
    }

}
