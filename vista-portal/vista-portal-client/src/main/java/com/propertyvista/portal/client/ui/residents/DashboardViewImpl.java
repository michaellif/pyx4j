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
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.domain.dto.DashboardDTO;

public class DashboardViewImpl extends SimplePanel implements DashboardView {

    private final DashboardForm form;

    public DashboardViewImpl() {
        form = new DashboardForm();
        form.initContent();
        setWidget(form);

    }

    @Override
    public void populate(DashboardDTO dashboard) {
        form.populate(dashboard);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        form.setPresenter(presenter);
    }

}
