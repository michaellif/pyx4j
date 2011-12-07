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

import com.propertyvista.common.client.ui.components.OkBox;
import com.propertyvista.common.client.ui.components.OkBox.OkResult;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorViewImpl extends CrmEditorViewImplBase<TenantDTO> implements TenantEditorView {

    public TenantEditorViewImpl() {
        super(CrmSiteMap.Tenants.Tenant.class, new TenantEditorForm());
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Tenant.Type> callback) {
        final SelectTypeBox box = new SelectTypeBox();
        box.run(new OkResult() {
            @Override
            public void onOk() {
                callback.onSuccess(box.getSelectedType());
            }
        });
    }

    private class SelectTypeBox extends OkBox {

        private RadioButton person;

        public SelectTypeBox() {
            super(i18n.tr("Select Tenant Type"));
            setContent(createContent());
        }

        protected Widget createContent() {
            HorizontalPanel main = new HorizontalPanel();
            main.add(person = new RadioButton(i18n.tr("Type"), Tenant.Type.person.toString()));
            main.add(new RadioButton(i18n.tr("Type"), Tenant.Type.company.toString()));
            main.setSpacing(8);
            main.setWidth("100%");
            person.setValue(true);
            return main;
        }

        public Tenant.Type getSelectedType() {
            return (person.getValue() ? Tenant.Type.person : Tenant.Type.company);
        }
    }
}
