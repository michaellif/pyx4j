/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 21, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.prime.form.AccessoryEntityForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.building.BuildingEditorView.Presenter;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.OrganizationContact;

class OrganizationContactFolder extends VistaBoxFolder<OrganizationContact> {

    private final BuildingForm parent;

    public OrganizationContactFolder(boolean modifyable, BuildingForm parent) {
        super(OrganizationContact.class, modifyable);
        this.parent = parent;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof OrganizationContact) {
            return new OrganizationContactEditor();
        } else {
            return super.create(member);
        }
    }

    private BuildingEditorView.Presenter getPresenter() {
        return (Presenter) parent.getParentView().getPresenter();
    }

    private class OrganizationContactEditor extends AccessoryEntityForm<OrganizationContact> {

        public OrganizationContactEditor() {
            super(OrganizationContact.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            main.setWidget(++row, 0, injectAndDecorate(proto().person()));
            main.setWidget(++row, 0, injectAndDecorate(proto().person().email()));

            row = -1;
            main.setWidget(++row, 1, injectAndDecorate(proto().person().workPhone()));
            main.setWidget(++row, 1, injectAndDecorate(proto().person().mobilePhone()));
            main.setWidget(++row, 1, injectAndDecorate(proto().person().homePhone()));

            main.setWidget(++row, 0, 2, injectAndDecorate(proto().description(), true));

            // repopulate related fields from selected employee:
            get(proto().person()).addValueChangeHandler(new ValueChangeHandler<Employee>() {
                @Override
                public void onValueChange(ValueChangeEvent<Employee> event) {
                    getPresenter().retrieveEmployee(new DefaultAsyncCallback<Employee>() {
                        @Override
                        public void onSuccess(Employee result) {
                            OrganizationContact value = getValue();
                            value.set(value.person(), result);
                            setValue(value, false);
                        }
                    }, event.getValue());
                }
            });

            return main;
        }
    }
}