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
 */
package com.propertyvista.crm.client.activity.crud.building.catalog;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionEditorView;
import com.propertyvista.crm.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionEditorActivity extends CrmEditorActivity<Concession> {

    public ConcessionEditorActivity(CrudAppPlace place) {
        super(Concession.class, place, CrmSite.getViewFactory().getView(ConcessionEditorView.class), GWT
                .<AbstractCrudService<Concession>> create(ConcessionCrudService.class));
    }

}
