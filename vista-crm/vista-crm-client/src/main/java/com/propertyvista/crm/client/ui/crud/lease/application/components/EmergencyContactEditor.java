/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactEditor extends CForm<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactEditor.class);

    public EmergencyContactEditor() {
        super(EmergencyContact.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().name(), new NameEditor(i18n.tr("Person")));
        formPanel.append(Location.Left, proto().relationship()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().sex()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().birthDate()).decorate().componentWidth(120);

        formPanel.h3(i18n.tr("Contact Info"));
        formPanel.append(Location.Left, proto().homePhone()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().mobilePhone()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().workPhone()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().email()).decorate().componentWidth(250);

        formPanel.h3(i18n.tr("Address"));
        formPanel.append(Location.Dual, proto().address(), new InternationalAddressEditor());

        return formPanel;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().birthDate()).addComponentValidator(new BirthdayDateValidator());
    }
}