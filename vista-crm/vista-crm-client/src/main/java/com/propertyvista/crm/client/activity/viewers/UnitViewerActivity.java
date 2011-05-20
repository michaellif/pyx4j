/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.viewers;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import com.propertyvista.crm.client.ui.vewers.IUnitViewerView;
import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.dto.AptUnitDTO;

public class UnitViewerActivity extends ViewerActivityBase<AptUnitDTO> {

    @Inject
    @SuppressWarnings("unchecked")
    public UnitViewerActivity(IUnitViewerView view) {
        super(view, (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class));
    }

}
