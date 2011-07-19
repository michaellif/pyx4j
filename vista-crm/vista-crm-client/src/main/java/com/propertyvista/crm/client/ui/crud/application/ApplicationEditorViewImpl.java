/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.application;

import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;

public class ApplicationEditorViewImpl extends CrmEditorViewImplBase<ApplicationDTO> implements ApplicationEditorView {

    private final ApplicationViewDelegate delegate;

    public ApplicationEditorViewImpl() {
        super(CrmSiteMap.Tenants.Application.class);

        delegate = new ApplicationViewDelegate(false);

        // create/init/set main form here: 
        CrmEntityForm<ApplicationDTO> form = new ApplicationEditorForm(this);
        form.initialize();
        setForm(form);
    }

    @Override
    public IListerView<AptUnitDTO> getUnitListerView() {
        return delegate.getUnitListerView();
    }

    @Override
    public IListerView<PotentialTenantInfo> getTenantListerView() {
        return delegate.getTenantListerView();
    }
}
