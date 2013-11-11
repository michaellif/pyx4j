/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.complex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.complex.ComplexListerView;
import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.ComplexDTO;

public class ComplexListerActivity extends AbstractListerActivity<ComplexDTO> {

    @SuppressWarnings("unchecked")
    public ComplexListerActivity(Place place) {
        super(place, (ILister<ComplexDTO>)  CrmSite.getViewFactory().getView(ComplexListerView.class), (AbstractCrudService<ComplexDTO>) GWT
                .create(ComplexCrudService.class), ComplexDTO.class);
    }

    @Override
    public boolean canCreateNewItem() {
        return SecurityController.checkBehavior(VistaCrmBehavior.PropertyManagement);
    }
}
