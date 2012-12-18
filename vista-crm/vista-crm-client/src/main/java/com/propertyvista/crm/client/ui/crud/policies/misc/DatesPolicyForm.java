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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.view.client.Range;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.DatesPolicyDTO;

public class DatesPolicyForm extends PolicyDTOTabPanelBasedForm<DatesPolicyDTO> {

    private static final I18n i18n = I18n.get(DatesPolicyForm.class);

    public DatesPolicyForm(IFormView<DatesPolicyDTO> view) {
        super(DatesPolicyDTO.class, view);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(createMiscPoliciesTab());
    }

    private FormFlexPanel createMiscPoliciesTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Misc Settings"));
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yearRangeStart())).labelWidth(20).componentWidth(5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yearRangeFutureSpan())).labelWidth(20).componentWidth(3).build());

        // components tune up:
        CComponent<?, ?> comp = get(proto().yearRangeStart());
        if (comp instanceof CMonthYearPicker) {
            int rangeStart = 1500;
            ((CMonthYearPicker) comp).setYearRange(new Range(rangeStart, (1900 - rangeStart) + ClientContext.getServerDate().getYear() + 1));
        }

        return content;
    }
}
