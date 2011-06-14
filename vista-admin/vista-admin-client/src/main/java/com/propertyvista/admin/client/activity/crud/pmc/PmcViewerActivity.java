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
package com.propertyvista.admin.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.admin.client.ui.crud.pmc.PmcViewerView;
import com.propertyvista.admin.client.viewfactories.PmcCrudVeiwFactory;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;

public class PmcViewerActivity extends ViewerActivityBase<PmcDTO> {

    @SuppressWarnings("unchecked")
    public PmcViewerActivity(Place place) {
        super((PmcViewerView) PmcCrudVeiwFactory.instance(PmcViewerView.class), (AbstractCrudService<PmcDTO>) GWT.create(PmcCrudService.class));
        withPlace(place);
    }
}
