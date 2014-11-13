/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.restrictions;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.RestrictionsPolicyDTO;

public class RestrictionsPolicyForm extends PolicyDTOTabPanelBasedForm<RestrictionsPolicyDTO> {

    private static final I18n i18n = I18n.get(RestrictionsPolicyForm.class);

    public RestrictionsPolicyForm(IFormView<RestrictionsPolicyDTO, ?> view) {
        super(RestrictionsPolicyDTO.class, view);
        addTab(createOccupationTab(), i18n.tr("Occupation"));
        addTab(createMagorityTab(), i18n.tr("Age of Majority"));
        addTab(createFinancialTab(), i18n.tr("Financial"));
        addTab(createMiscTab(), i18n.tr("Miscellaneous"));
    }

    private IsWidget createOccupationTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().occupantsPerBedRoom()).decorate().componentWidth(40).labelWidth(300);

        formPanel.br();

        formPanel.append(Location.Dual, proto().maxParkingSpots()).decorate().componentWidth(40).labelWidth(300);
        formPanel.append(Location.Dual, proto().maxLockers()).decorate().componentWidth(40).labelWidth(300);
        formPanel.append(Location.Dual, proto().maxPets()).decorate().componentWidth(40).labelWidth(300);

        return formPanel;
    }

    private IsWidget createMagorityTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().ageOfMajority()).decorate().componentWidth(40).labelWidth(300);
        formPanel.append(Location.Dual, proto().enforceAgeOfMajority()).decorate().componentWidth(40).labelWidth(300);
        formPanel.append(Location.Dual, proto().maturedOccupantsAreApplicants()).decorate().componentWidth(40).labelWidth(300);

        return formPanel;
    }

    private IsWidget createFinancialTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().noNeedGuarantors()).decorate().componentWidth(40).labelWidth(300);

        formPanel.br();

        formPanel.append(Location.Dual, proto().minEmploymentDuration()).decorate().componentWidth(40).labelWidth(300);
        formPanel.append(Location.Dual, proto().maxNumberOfEmployments()).decorate().componentWidth(40).labelWidth(300);

        return formPanel;
    }

    private IsWidget createMiscTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().yearsToForcingPreviousAddress()).decorate().componentWidth(40).labelWidth(300);

        formPanel.br();

        formPanel.append(Location.Dual, proto().emergencyContactsIsMandatory()).decorate().componentWidth(40).labelWidth(300);
        formPanel.append(Location.Dual, proto().emergencyContactsNumber()).decorate().componentWidth(40).labelWidth(300);

        return formPanel;
    }
}
