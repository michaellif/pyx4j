/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.application;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.tenant.lease.TenantInLeaseLister;
import com.propertyvista.domain.tenant.TenantInLease;

public class ApplicationViewDelegate implements ApplicationView {

    private final IListerView<TenantInLease> tenantLister;

    public ApplicationViewDelegate(boolean readOnly) {
        tenantLister = new ListerInternalViewImplBase<TenantInLease>(new TenantInLeaseLister(/* readOnly */));
    }

    @Override
    public IListerView<TenantInLease> getTenantListerView() {
        return tenantLister;
    }
}
