/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;

public class MaintenanceDetailsViewImpl extends SimplePanel implements MaintenanceDetailsView {

    private static I18n i18n = I18n.get(MaintenanceDetailsViewImpl.class);

    public MaintenanceDetailsViewImpl() {
        setWidget(new HTML("MaintenanceDetailsViewImpl"));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        // TODO Auto-generated method stub

    }

    @Override
    public void populate(MaintananceDTO requests) {
        // TODO Auto-generated method stub

    }

}
