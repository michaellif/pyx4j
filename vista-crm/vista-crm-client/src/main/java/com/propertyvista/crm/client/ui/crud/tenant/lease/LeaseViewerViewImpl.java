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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImpl extends CrmViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private final LeaseViewDelegate delegate;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        delegate = new LeaseViewDelegate(true);

        // create/init/set main form here: 
        CrmEntityForm<LeaseDTO> form = new LeaseEditorForm(new CrmViewersComponentFactory(), this);
        form.initialize();
        setForm(form);
    }

    @Override
    public IListerView<BuildingDTO> getBuildingListerView() {
        return delegate.getBuildingListerView();
    }

    @Override
    public IListerView<AptUnit> getUnitListerView() {
        return delegate.getUnitListerView();
    }

    @Override
    public IListerView<Tenant> getTenantListerView() {
        return delegate.getTenantListerView();
    }
}