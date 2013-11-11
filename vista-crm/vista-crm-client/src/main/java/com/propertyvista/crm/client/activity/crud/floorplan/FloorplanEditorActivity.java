/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.floorplan;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanEditorView;
import com.propertyvista.crm.rpc.services.building.FloorplanCrudService;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.FloorplanDTO;

public class FloorplanEditorActivity extends CrmEditorActivity<FloorplanDTO> implements FloorplanEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public FloorplanEditorActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(FloorplanEditorView.class), (AbstractCrudService<FloorplanDTO>) GWT
                .create(FloorplanCrudService.class), FloorplanDTO.class);
    }

    @Override
    public void getILSVendors(AsyncCallback<Vector<ILSVendor>> callback, Floorplan floorplan) {
        ((FloorplanCrudService) getService()).getILSVendors(callback, floorplan);
    }
}
