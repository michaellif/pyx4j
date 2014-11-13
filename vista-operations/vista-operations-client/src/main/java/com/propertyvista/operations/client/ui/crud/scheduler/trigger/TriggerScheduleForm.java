/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.scheduler.ScheduleType;
import com.propertyvista.operations.domain.scheduler.TriggerSchedule;

public class TriggerScheduleForm extends OperationsEntityForm<TriggerSchedule> {

    private static final I18n i18n = I18n.get(TriggerScheduleForm.class);

    public TriggerScheduleForm(IFormView<TriggerSchedule, ?> view) {
        super(TriggerSchedule.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().repeatType()).decorate().componentWidth(144);
        formPanel.append(Location.Right, proto().nextFireTime()).decorate().componentWidth(144);

        formPanel.append(Location.Left, proto().repeatEvery()).decorate().componentWidth(144);
        formPanel.append(Location.Left, proto().time()).decorate().componentWidth(144);

        formPanel.append(Location.Left, proto().startsOn()).decorate().componentWidth(144);
        formPanel.append(Location.Right, proto().endsOn()).decorate().componentWidth(144);

        // fill ScheduleType types:
        if (get(proto().repeatType()) instanceof CComboBox) {
            ((CComboBox<ScheduleType>) get(proto().repeatType())).setOptions(ScheduleType.uiSet());
        }

        selectTab(addTab(formPanel, i18n.tr("General")));
    }

    @Override
    public void addValidations() {
        get(proto().repeatType()).addValueChangeHandler(new ValueChangeHandler<ScheduleType>() {
            @Override
            public void onValueChange(ValueChangeEvent<ScheduleType> event) {
                updateVisibility();
                get(proto().nextFireTime()).setValue(null);
            }
        });

        @SuppressWarnings("rawtypes")
        CComponent starts = get(proto().startsOn());
        ((CDatePicker) starts).setPastDateSelectionAllowed(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateVisibility();
    }

    private void updateVisibility() {
        ScheduleType type = get(proto().repeatType()).getValue();
        boolean enabled = (type != ScheduleType.Manual);
        get(proto().time()).setEnabled(enabled);
        get(proto().startsOn()).setEnabled(enabled);

        boolean repeat = enabled && (type != ScheduleType.Once);
        get(proto().repeatEvery()).setEnabled(repeat);
        get(proto().endsOn()).setEnabled(repeat);
    }
}
