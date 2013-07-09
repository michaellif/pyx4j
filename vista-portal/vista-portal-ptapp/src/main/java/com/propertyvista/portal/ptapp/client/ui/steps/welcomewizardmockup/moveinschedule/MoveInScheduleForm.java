/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.moveinschedule;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CTimeField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.moveinwizardmockup.MoveInScheduleDTO;
import com.propertyvista.domain.moveinwizardmockup.ScheduleDTO;
import com.propertyvista.domain.moveinwizardmockup.TimeSegmentDTO;
import com.propertyvista.domain.moveinwizardmockup.TimeSegmentDTO.Status;

public class MoveInScheduleForm extends CEntityDecoratableForm<MoveInScheduleDTO> {

    private final static I18n i18n = I18n.get(MoveInScheduleForm.class);

    private HTML message;

    public MoveInScheduleForm() {
        super(MoveInScheduleDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        content.getColumnFormatter().setWidth(0, "70%");
        content.getColumnFormatter().setWidth(1, "30%");
        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().moveInDay()), 10).build());

        content.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        message = new HTML();
        content.setWidget(++row, 0, message);

        row = -1;
        content.setWidget(++row, 1, inject(proto().elevatorSchedule(), new ScheduleViewer()));
        content.getFlexCellFormatter().setRowSpan(row, 1, 2);

        get(proto().moveInDay()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                // TODO request the presenter to refill the schedule
            }
        });
        get(proto().elevatorSchedule()).addValueChangeHandler(new ValueChangeHandler<ScheduleDTO>() {

            @Override
            public void onValueChange(ValueChangeEvent<ScheduleDTO> event) {

                getValue().elevatorReserveFrom().setValue(getReserveFrom(event.getValue()));
                getValue().elevatorReserveTo().setValue(getReserveTo(event.getValue()));

                repopulateMessage();
            }

            private Time getReserveFrom(ScheduleDTO value) {
                for (TimeSegmentDTO s : value.schedule()) {
                    if (s.status().getValue() == Status.booked) {
                        return s.start().getValue();
                    }
                }
                return null;
            }

            private Time getReserveTo(ScheduleDTO value) {
                List<TimeSegmentDTO> revSchedule = new ArrayList<TimeSegmentDTO>(value.schedule());
                Collections.reverse(revSchedule);
                for (TimeSegmentDTO s : revSchedule) {
                    if (s.status().getValue() == Status.booked) {
                        return s.end().getValue();
                    }
                }
                return null;
            }
        });
        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        repopulateMessage();
    }

    private void repopulateMessage() {
        if (!getValue().moveInDay().isNull()) {
            String chosenDate = DateTimeFormat.getFormat(CDatePicker.defaultDateFormat).format(getValue().moveInDay().getValue());
            String chosenTime = !getValue().elevatorReserveFrom().isNull() ? DateTimeFormat.getFormat(CTimeField.defaultTimeFormat).format(
                    getValue().elevatorReserveFrom().getValue()) : "";
            String chosendDateAndTime = chosenDate + ("".equals(chosenTime) ? "" : " " + chosenTime);

            message.setHTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Congratulations!")).toSafeHtml().asString()
                    + "<br/><br/>"
                    + new SafeHtmlBuilder()
                            .appendEscaped(i18n.tr("You have chosen {0} as your Move-In Time and Date. We look forward to Welcoming you.", chosendDateAndTime))
                            .toSafeHtml().asString());
        }
    }
}
