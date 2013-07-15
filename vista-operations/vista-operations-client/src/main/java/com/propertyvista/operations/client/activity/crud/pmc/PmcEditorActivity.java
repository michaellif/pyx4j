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
package com.propertyvista.operations.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.operations.client.ui.crud.pmc.PmcEditorView;
import com.propertyvista.operations.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.operations.rpc.PmcDTO;
import com.propertyvista.operations.rpc.services.PmcCrudService;

public class PmcEditorActivity extends AbstractEditorActivity<PmcDTO> {

    private final PmcDTO newItem;

    @SuppressWarnings("unchecked")
    public PmcEditorActivity(CrudAppPlace place) {
        super(place, ManagementVeiwFactory.instance(PmcEditorView.class), (AbstractCrudService<PmcDTO>) GWT.create(PmcCrudService.class), PmcDTO.class);
        newItem = (PmcDTO) place.getNewItem();
    }

    @Override
    protected void createNewEntity(AsyncCallback<PmcDTO> callback) {
        PmcDTO entity;
        if (newItem == null) {
            entity = EntityFactory.create(getEntityClass());
        } else {
            entity = newItem;
        }

        entity.features().occupancyModel().setValue(Boolean.TRUE);
        entity.features().productCatalog().setValue(Boolean.TRUE);
        entity.features().leases().setValue(Boolean.TRUE);
        entity.features().onlineApplication().setValue(Boolean.FALSE);
        entity.features().defaultProductCatalog().setValue(true);
        entity.features().yardiIntegration().setValue(Boolean.FALSE);
        entity.features().countryOfOperation().setValue(CountryOfOperation.Canada);

        entity.features().tenantSureIntegration().setValue(Boolean.TRUE);

        callback.onSuccess(entity);

    }
}
