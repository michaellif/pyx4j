/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.misc;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.DatesPolicyDTO;

public class DatesPolicyForm extends PolicyDTOTabPanelBasedForm<DatesPolicyDTO> {

    private static final I18n i18n = I18n.get(DatesPolicyForm.class);

    public DatesPolicyForm(IFormView<DatesPolicyDTO, ?> view) {
        super(DatesPolicyDTO.class, view);
        addTab(createMiscPoliciesTab(), i18n.tr("Misc. Settings"));
    }

    private IsWidget createMiscPoliciesTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().yearRangeStart()).decorate().componentWidth(60);
        formPanel.append(Location.Left, proto().yearRangeFutureSpan()).decorate().componentWidth(60);

        // components tune up:
        CComponent<?, ?, ?, ?> comp = get(proto().yearRangeStart());
        if (comp instanceof CMonthYearPicker) {
            int rangeStart = 1500;
            ((CMonthYearPicker) comp).setYearRange(new Range(rangeStart, (1900 - rangeStart) + ClientContext.getServerDate().getYear() + 1));
        }

        return formPanel;
    }
}
