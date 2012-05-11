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
package com.propertyvista.crm.client.activity.crud.customer.screening;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.customer.screening.PersonScreeningEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.CustomerViewFactory;
import com.propertyvista.crm.rpc.services.customer.screening.PersonScreeningCrudService;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;

public class PersonScreeningEditorActivity extends EditorActivityBase<PersonScreening> {

    @SuppressWarnings("unchecked")
    public PersonScreeningEditorActivity(CrudAppPlace place) {
        super(place, CustomerViewFactory.instance(PersonScreeningEditorView.class), (AbstractCrudService<PersonScreening>) GWT
                .create(PersonScreeningCrudService.class), PersonScreening.class);
    }

    @Override
    protected void createNewEntity(AsyncCallback<PersonScreening> callback) {
        PersonScreening screening = EntityFactory.create(getEntityClass());

        if (Customer.class.getName().equals(getParentClassName())) {
            screening.screene().set(EntityFactory.create(Customer.class));
        } else if (Guarantor.class.getName().equals(getParentClassName())) {
            screening.screene().set(EntityFactory.create(Guarantor.class));
        }

        callback.onSuccess(screening);
    }
}
