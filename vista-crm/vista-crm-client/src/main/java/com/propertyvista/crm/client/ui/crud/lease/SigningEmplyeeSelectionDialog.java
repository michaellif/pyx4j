/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.company.Employee;

public abstract class SigningEmplyeeSelectionDialog extends OkCancelDialog {

    private static final I18n i18n = I18n.get(SigningEmplyeeSelectionDialog.class);

    private final SigningEmployeeSelectionForm form;

    public SigningEmplyeeSelectionDialog(List<Employee> agents) {
        super(i18n.tr("Choose Employee to Sign the Lease Agreement"));
        form = new SigningEmployeeSelectionForm();
        form.initContent();
        form.populateNew();
        form.setAgents(agents);

        setBody(form);
    }

    @Override
    public final boolean onClickOk() {
        if (form.getValue().employee().isNull()) {
            return false;
        } else {
            onEmployeeSelected(form.getValue().employee());
            return true;
        }
    }

    public abstract void onEmployeeSelected(Employee employee);

    @Transient
    public interface SigningEmployeeSelectionModel extends IEntity {

        Employee employee();

    }

    private static class SigningEmployeeSelectionForm extends CEntityForm<SigningEmployeeSelectionModel> {

        private CComboBox<Employee> agentComboBox;

        public SigningEmployeeSelectionForm() {
            super(SigningEmployeeSelectionModel.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
            int row = -1;
            panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().employee(), createAgentComboBox())).componentWidth("150px").labelWidth("150px")
                    .contentWidth("150px").build());
            return panel;
        }

        public void setAgents(List<Employee> agents) {
            agentComboBox.setOptions(agents);
        }

        private CComboBox<Employee> createAgentComboBox() {
            agentComboBox = new CComboBox<Employee>(CComboBox.NotInOptionsPolicy.DISCARD) {
                @Override
                public String getItemName(Employee o) {
                    return (o != null && !o.isNull()) ? o.name().getStringView() + (o.signature().getPrimaryKey() == null ? i18n.tr(" (No Signature)") : "")
                            : "";
                }
            };
            agentComboBox.setMandatory(true);
            return agentComboBox;
        }

    }

}
