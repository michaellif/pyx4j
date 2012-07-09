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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.ui.crud.pmc.PmcEditorView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;

public class PmcEditorActivity extends EditorActivityBase<PmcDTO> {

    @SuppressWarnings("unchecked")
    public PmcEditorActivity(CrudAppPlace place) {
        super(place, ManagementVeiwFactory.instance(PmcEditorView.class), (AbstractCrudService<PmcDTO>) GWT.create(PmcCrudService.class), PmcDTO.class);

    }

    @Override
    protected void createNewEntity(AsyncCallback<PmcDTO> callback) {
        PmcDTO entity = EntityFactory.create(getEntityClass());

        entity.features().occupancyModel().setValue(Boolean.TRUE);
        entity.features().productCatalog().setValue(Boolean.TRUE);
        entity.features().leases().setValue(Boolean.TRUE);
        entity.features().onlineApplication().setValue(Boolean.FALSE);
        entity.features().xmlSiteExport().setValue(Boolean.FALSE);

        callback.onSuccess(entity);
    }
}
