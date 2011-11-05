/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 26, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.complex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.complex.ComplexEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.ComplexCrudService;
import com.propertyvista.dto.ComplexDTO;

public class ComplexEditorAcitvity extends EditorActivityBase<ComplexDTO> implements ComplexEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public ComplexEditorAcitvity(Place place) {
        super((ComplexEditorView) BuildingViewFactory.instance(ComplexEditorView.class),
                (AbstractCrudService<ComplexDTO>) GWT.create(ComplexCrudService.class), ComplexDTO.class);
        setPlace(place);
    }
}
