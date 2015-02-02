/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2015
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CPhoneField.PhoneType;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.legal.n4.N4Data;

public class N4DataEditorPanel<E extends N4Data> extends FormPanel {

    private static final I18n i18n = I18n.get(N4BatchForm.class);

    private final CForm<E> form;

    private FormPanel contactPanelN4;

    private FormPanel contactPanelCS;

    public N4DataEditorPanel(CForm<E> parent) {
        super(parent);
        this.form = parent;

        append(Location.Left, proto().serviceDate(), new CDateLabel()).decorate();
        append(Location.Left, proto().terminationDateOption()).decorate();

        append(Location.Right, proto().deliveryMethod()).decorate();
        append(Location.Right, proto().deliveryDate()).decorate();

        h1(i18n.tr("Signing Agent"));
        CField<Employee, ?> signingAgentBox = isEditable() ? new CEntityComboBox<>(Employee.class) : //
                new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class));
        append(Location.Left, proto().signingAgent(), signingAgentBox).decorate();
        append(Location.Right, contactPanelN4 = new FormPanel(form));
        contactPanelN4.append(Location.Left, proto().emailAddress()).decorate();
        contactPanelN4.append(Location.Left, proto().phoneNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        contactPanelN4.append(Location.Left, proto().faxNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();

        h1(i18n.tr("Servicing Agent"));
        CField<Employee, ?> servicingAgentBox = isEditable() ? new CEntityComboBox<>(Employee.class) : //
                new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class));
        append(Location.Left, proto().servicingAgent(), servicingAgentBox).decorate();
        append(Location.Right, contactPanelCS = new FormPanel(form));
        contactPanelCS.append(Location.Left, proto().phoneNumberCS()).decorate();

        h1(i18n.tr("Company Info"));
        append(Location.Left, proto().companyLegalName()).decorate().customLabel(i18n.tr("Legal Name"));
        append(Location.Dual, proto().companyAddress(), new InternationalAddressEditor());

        if (isEditable()) {
            form.addPropertyChangeHandler(new PropertyChangeHandler() {

                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName().equals(PropertyName.repopulated)) {
                        contactPanelN4.setVisible(!form.getValue().useAgentContactInfoN4().getValue(false));
                        contactPanelCS.setVisible(!form.getValue().useAgentContactInfoCS().getValue(false));
                    }
                }
            });
        }
    }

    public E proto() {
        return form.proto();
    }

    public boolean isEditable() {
        return form.isEditable();
    }
}
