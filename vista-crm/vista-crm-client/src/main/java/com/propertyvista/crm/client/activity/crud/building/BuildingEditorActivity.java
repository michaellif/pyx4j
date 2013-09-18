/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.building.BuildingEditorView;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.dto.BuildingDTO;

public class BuildingEditorActivity extends CrmEditorActivity<BuildingDTO> implements BuildingEditorView.Presenter {

    public BuildingEditorActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().instantiate(BuildingEditorView.class), GWT.<BuildingCrudService> create(BuildingCrudService.class),
                BuildingDTO.class);
    }

    @Override
    public void retrieveMerchantAccountStatus(AsyncCallback<MerchantAccount> callback, MerchantAccount merchantAccountStub) {
        ((BuildingCrudService) getService()).retrieveMerchantAccountStatus(callback, merchantAccountStub);
    }
}
