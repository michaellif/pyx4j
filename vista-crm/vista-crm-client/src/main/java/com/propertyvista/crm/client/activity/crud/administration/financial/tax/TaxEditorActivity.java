/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.crud.administration.financial.tax;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.administration.financial.tax.TaxEditorView;
import com.propertyvista.crm.rpc.services.admin.TaxCrudService;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxEditorActivity extends CrmEditorActivity<Tax> {

    public TaxEditorActivity(CrudAppPlace place) {
        super(Tax.class,  place, CrmSite.getViewFactory().getView(TaxEditorView.class), GWT.<AbstractCrudService<Tax>> create(TaxCrudService.class));
    }
}
