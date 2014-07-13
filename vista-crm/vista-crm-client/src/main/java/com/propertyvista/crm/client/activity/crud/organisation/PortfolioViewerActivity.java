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
package com.propertyvista.crm.client.activity.crud.organisation;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.organisation.portfolio.PortfolioViewerView;
import com.propertyvista.crm.rpc.services.organization.PortfolioCrudService;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioViewerActivity extends CrmViewerActivity<Portfolio> {

    public PortfolioViewerActivity(CrudAppPlace place) {
        super(Portfolio.class, place, CrmSite.getViewFactory().getView(PortfolioViewerView.class), GWT
                .<AbstractCrudService<Portfolio>> create(PortfolioCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return SecurityController.check(DataModelPermission.permissionUpdate(Portfolio.class));
    }
}
