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
package com.propertyvista.admin.client.activity.crud.proc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.admin.client.ui.crud.proc.ProcessViewerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.domain.proc.Process;
import com.propertyvista.admin.rpc.services.PmcCrudService;

public class ProcessViewerActivity extends ViewerActivityBase<Process> implements ProcessViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public ProcessViewerActivity(Place place) {
        super(place, ManagementVeiwFactory.instance(ProcessViewerView.class), (AbstractCrudService<Process>) GWT.create(PmcCrudService.class));
    }

}
