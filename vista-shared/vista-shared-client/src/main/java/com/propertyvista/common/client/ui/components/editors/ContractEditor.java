/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.property.vendor.Contract;

public class ContractEditor extends CForm<Contract> {

    public ContractEditor() {
        super(Contract.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().contractID()).decorate();
        formPanel.append(Location.Left, proto().contractor()).decorate();
        formPanel.append(Location.Left, proto().cost()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().start()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().end()).decorate().componentWidth(120);

        return formPanel;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        new StartEndDateValidation(get(proto().start()), get(proto().end()));
        get(proto().start()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().end()))); //connects validation of both fields
        get(proto().end()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().start())));

    }
}
