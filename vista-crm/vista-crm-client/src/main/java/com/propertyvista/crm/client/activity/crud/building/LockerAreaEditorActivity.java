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
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaEditorView;
import com.propertyvista.crm.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaEditorActivity extends CrmEditorActivity<LockerAreaDTO> implements LockerAreaEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public LockerAreaEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(LockerAreaEditorView.class), (AbstractCrudService<LockerAreaDTO>) GWT.create(LockerAreaCrudService.class),
                LockerAreaDTO.class);
    }

    @Override
    protected void createNewEntity(AsyncCallback<LockerAreaDTO> callback) {
        LockerAreaDTO lockerArea = EntityFactory.create(getEntityClass());

        // do not allow null members!
        lockerArea.totalLockers().setValue(0);
        lockerArea.largeLockers().setValue(0);
        lockerArea.regularLockers().setValue(0);
        lockerArea.smallLockers().setValue(0);

        callback.onSuccess(lockerArea);
    }
}
