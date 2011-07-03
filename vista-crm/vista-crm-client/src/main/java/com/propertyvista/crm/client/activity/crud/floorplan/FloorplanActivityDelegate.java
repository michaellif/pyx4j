/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.floorplan;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanView;
import com.propertyvista.crm.rpc.services.ConcessionCrudService;
import com.propertyvista.domain.financial.Concession;

public class FloorplanActivityDelegate implements FloorplanView.Presenter {

    private final IListerView.Presenter concessionsLister;

    @SuppressWarnings("unchecked")
    public FloorplanActivityDelegate(FloorplanView view) {

        concessionsLister = new ListerActivityBase<Concession>(view.getConcessionsListerView(),
                (AbstractCrudService<Concession>) GWT.create(ConcessionCrudService.class), Concession.class);
    }

    @Override
    public Presenter getConcessionsPresenter() {
        return concessionsLister;
    }

    public void populate(Key parentID) {
        concessionsLister.setParentFiltering(parentID);
        concessionsLister.populateData(0);
    }
}
