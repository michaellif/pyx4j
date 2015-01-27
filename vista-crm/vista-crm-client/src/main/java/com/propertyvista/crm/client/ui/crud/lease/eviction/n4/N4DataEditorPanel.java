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

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
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

    private final CForm<E> parent;

    public N4DataEditorPanel(CForm<E> parent) {
        super(parent);
        this.parent = parent;

        append(Location.Left, proto().created(), new CDateLabel()).decorate();
        append(Location.Left, proto().serviceDate(), new CDateLabel()).decorate();

        append(Location.Right, proto().deliveryMethod()).decorate();
        append(Location.Right, proto().deliveryDate()).decorate();
        append(Location.Right, proto().terminationDateOption()).decorate();

        h1(i18n.tr("Agent Information"));
        CField<Employee, ?> signingAgentBox = isEditable() ? new CEntityComboBox<>(Employee.class) : //
                new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class));
        append(Location.Left, proto().signingAgent(), signingAgentBox).decorate();

        append(Location.Left, proto().phoneNumber()).decorate();
        append(Location.Left, proto().faxNumber()).decorate();
        append(Location.Left, proto().emailAddress()).decorate();

        CField<Employee, ?> servicingAgentBox = isEditable() ? new CEntityComboBox<>(Employee.class) : //
                new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class));
        append(Location.Right, proto().servicingAgent(), servicingAgentBox).decorate();
        append(Location.Right, proto().phoneNumberCS()).decorate();

        h1(i18n.tr("Company Info"));
        append(Location.Left, proto().companyLegalName()).decorate().customLabel(i18n.tr("Legal Name"));
        append(Location.Dual, proto().companyAddress(), new InternationalAddressEditor());
    }

    public E proto() {
        return parent.proto();
    }

    public boolean isEditable() {
        return parent.isEditable();
    }
}
