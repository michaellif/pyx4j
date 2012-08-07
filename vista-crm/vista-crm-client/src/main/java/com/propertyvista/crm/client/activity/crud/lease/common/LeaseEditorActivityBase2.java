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
package com.propertyvista.crm.client.activity.crud.lease.common;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseEditorViewBase2;
import com.propertyvista.crm.rpc.services.lease.common.LeaseEditorCrudServiceBase2;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.LeaseDTO2;

public abstract class LeaseEditorActivityBase2<DTO extends LeaseDTO2> extends EditorActivityBase<DTO> implements LeaseEditorViewBase2.Presenter {

    public LeaseEditorActivityBase2(CrudAppPlace place, IEditorView<DTO> view, LeaseEditorCrudServiceBase2<DTO> service, Class<DTO> entityClass) {
        super(place, view, service, entityClass);

//        getService().create(new DefaultAsyncCallback<Key>() {
//            @Override
//            public void onSuccess(Key result) {
//                getService().retrieve(new DefaultAsyncCallback<DTO>() {
//                    @Override
//                    public void onSuccess(DTO result) {
//                        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseTerm().formEditorPlace(result.currentLeaseTerm().getPrimaryKey()));
//                    }
//                }, result, RetrieveTraget.Edit);
//            }
//        }, (DTO) place.getNewItem());
    }

    @Override
    public void setSelectedUnit(AptUnit item) {
        ((LeaseEditorCrudServiceBase2<DTO>) getService()).setSelectedUnit(new DefaultAsyncCallback<DTO>() {
            @Override
            public void onSuccess(DTO result) {
                ((LeaseEditorViewBase2<DTO>) getView()).updateUnitValue(result);
            }
        }, EntityFactory.createIdentityStub(AptUnit.class, item.getPrimaryKey()), getView().getValue());
    }
}
