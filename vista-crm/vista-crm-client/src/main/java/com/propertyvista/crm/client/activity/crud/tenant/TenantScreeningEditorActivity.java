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
package com.propertyvista.crm.client.activity.crud.tenant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.TenantScreeningEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.TenantScreeningCrudService;
import com.propertyvista.domain.tenant.TenantScreening;

public class TenantScreeningEditorActivity extends EditorActivityBase<TenantScreening> {

    @SuppressWarnings("unchecked")
    public TenantScreeningEditorActivity(Place place) {
        super((TenantScreeningEditorView) TenantViewFactory.instance(TenantScreeningEditorView.class), (AbstractCrudService<TenantScreening>) GWT
                .create(TenantScreeningCrudService.class), TenantScreening.class);
        setPlace(place);
    }
}
