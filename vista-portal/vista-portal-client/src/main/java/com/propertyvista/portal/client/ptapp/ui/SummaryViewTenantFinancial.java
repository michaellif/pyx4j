/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.domain.pt.SummaryPotentialTenantFinancial;

public class SummaryViewTenantFinancial extends SummaryViewTenantListBase<SummaryPotentialTenantFinancial> {

    public SummaryViewTenantFinancial() {
        super(SummaryPotentialTenantFinancial.class);
    }

    @Override
    public IsWidget getTenantFullName() {
        return inject(proto().tenantFullName());
    }

    @Override
    public IsWidget bindFullView() {
        return inject(proto().tenantFinancial(), new FinancialViewForm(this));
    }
}
