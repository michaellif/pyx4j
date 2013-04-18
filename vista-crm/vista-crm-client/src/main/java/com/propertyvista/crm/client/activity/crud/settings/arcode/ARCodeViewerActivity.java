/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.arcode;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.ARCodeViewerView;
import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.HasYardiIntegrationMode;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.admin.ARCodeCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.shared.config.VistaFeatures;

public class ARCodeViewerActivity extends CrmViewerActivity<ARCode> {

    @SuppressWarnings("unchecked")
    public ARCodeViewerActivity(CrudAppPlace place) {
        super(place, SettingsViewFactory.instance(ARCodeViewerView.class), (AbstractCrudService<ARCode>) GWT.create(ARCodeCrudService.class));
    }

    @Override
    protected void onPopulateSuccess(ARCode result) {
        super.onPopulateSuccess(result);
        ((HasYardiIntegrationMode) getView()).setYardiIntegrationModeEnabled(VistaFeatures.instance().yardiIntegration());
    }
}
