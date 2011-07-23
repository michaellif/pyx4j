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
package com.propertyvista.crm.client.ui.crud.tenant;

import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.dto.TenantDTO;

public class TenantViewerViewImpl extends CrmViewerViewImplBase<TenantDTO> implements TenantViewerView {

    private final TenantViewDelegate delegate;

    public TenantViewerViewImpl() {
        super(CrmSiteMap.Tenants.Tenant.class);

        delegate = new TenantViewDelegate(true);

        // create/init/set main form here: 
        CrmEntityForm<TenantDTO> form = new TenantEditorForm(new CrmViewersComponentFactory(), this);
        form.initialize();
        setForm(form);
    }

    @Override
    public IListerView<TenantScreening> getScreeningListerView() {
        return delegate.getScreeningListerView();
    }
}