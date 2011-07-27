/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.OkCancelBox;
import com.propertyvista.crm.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorViewImpl extends CrmEditorViewImplBase<TenantDTO> implements TenantEditorView {

    private final TenantViewDelegate delegate;

    public TenantEditorViewImpl() {
        super(CrmSiteMap.Tenants.Tenant.class);

        delegate = new TenantViewDelegate(false);

        // create/init/set main form here: 
        CrmEntityForm<TenantDTO> form = new TenantEditorForm(this);
        form.initialize();
        setForm(form);
    }

    @Override
    public IListerView<TenantScreening> getScreeningListerView() {
        return delegate.getScreeningListerView();
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Tenant.Type> callback) {
        new ShowPopUpBox<SelectTypeBox>(new SelectTypeBox()) {
            @Override
            protected void onClose(SelectTypeBox box) {
                callback.onSuccess(box.getSelectedType());
            }
        };
    }

    private class SelectTypeBox extends OkCancelBox {

        private RadioButton person;

        public SelectTypeBox() {
            super("Select Tenant Type", true);
        }

        @Override
        protected Widget createContent() {
            HorizontalPanel main = new HorizontalPanel();
            main.add(person = new RadioButton("Type", Tenant.Type.person.toString()));
            main.add(new RadioButton("Type", Tenant.Type.company.toString()));
            main.setSpacing(8);
            main.setWidth("100%");
            person.setValue(true);
            return main;
        }

        @Override
        protected void setSize() {
            setSize("200px", "100px");
        }

        public Tenant.Type getSelectedType() {
            return (person.getValue() ? Tenant.Type.person : Tenant.Type.company);
        }
    }
}
