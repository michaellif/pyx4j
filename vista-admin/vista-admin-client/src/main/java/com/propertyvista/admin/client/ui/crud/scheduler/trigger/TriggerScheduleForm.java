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
package com.propertyvista.admin.client.ui.crud.scheduler.trigger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.scheduler.ScheduleType;
import com.propertyvista.admin.domain.scheduler.TriggerSchedule;

public class TriggerScheduleForm extends AdminEntityForm<TriggerSchedule> {

    private static final I18n i18n = I18n.get(TriggerScheduleForm.class);

    public TriggerScheduleForm() {
        this(false);
    }

    public TriggerScheduleForm(boolean viewMode) {
        super(TriggerSchedule.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().repeatType()), 12).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().nextFireTime()), 12).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().repeatEvery()), 12).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().time()), 12).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().startsOn()), 12).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().endsOn()), 12).build());

        // fill ScheduleType types:
        if (get(proto().repeatType()) instanceof CComboBox) {
            ((CComboBox<ScheduleType>) get(proto().repeatType())).setOptions(ScheduleType.uiSet());
        }

        selectTab(addTab(content, i18n.tr("General")));
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
    protected void onSetValue(boolean populate) {
        super.onSetValue(populate);
        if (isValueEmpty()) {
            return;
        }

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
