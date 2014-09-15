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
import com.pyx4j.site.client.backoffice.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.complex.ComplexListerView;
import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.dto.ComplexDTO;

public class ComplexListerActivity extends AbstractListerActivity<ComplexDTO> {

    public ComplexListerActivity(Place place) {
        super(ComplexDTO.class, place, CrmSite.getViewFactory().getView(ComplexListerView.class), GWT
                .<AbstractCrudService<ComplexDTO>> create(ComplexCrudService.class));
    }

}
