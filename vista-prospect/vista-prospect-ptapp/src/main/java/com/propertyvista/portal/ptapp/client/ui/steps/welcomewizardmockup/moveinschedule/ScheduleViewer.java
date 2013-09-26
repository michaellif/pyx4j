/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.moveinschedule;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.CTimeField;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.moveinwizardmockup.ScheduleDTO;
import com.propertyvista.domain.moveinwizardmockup.TimeSegmentDTO;
import com.propertyvista.domain.moveinwizardmockup.TimeSegmentDTO.Status;

public class ScheduleViewer extends CViewer<ScheduleDTO> {

    private static final I18n i18n = I18n.get(ScheduleViewer.class);

    private final DateTimeFormat timeFormat;

    private final DateTimeFormat dateFormat;

    private final List<TimeSegmentDTO> selected;

    private ScheduleDTO schedule;

    private FlexTable schedulePanel;

    private int prevScrollPosition;

    private ScrollPanel scheduleScrollPanel;

    public ScheduleViewer() {
        this(CTimeField.defaultTimeFormat, CDatePicker.defaultDateFormat);
    }

    public ScheduleViewer(String timeFormatStr, String dateFormatStr) {
        timeFormat = DateTimeFormat.getFormat(timeFormatStr);
        dateFormat = DateTimeFormat.getFormat(dateFormatStr);
        selected = new ArrayList<TimeSegmentDTO>();
        prevScrollPosition = 0;
    }

    @Override
    public IsWidget createContent(ScheduleDTO value) {
        this.schedule = value;

        VerticalPanel panel = new VerticalPanel();
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        panel.add(new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Elevator Availability for")).toSafeHtml().asString()));
        // TODO i18n the string
        panel.add(new HTML(new SafeHtmlBuilder().appendEscaped(dateFormat.format(value.scheduleDate().getValue())).toSafeHtml().asString()));

        scheduleScrollPanel = new ScrollPanel(redraw().asWidget());
        scheduleScrollPanel.setWidth("17em");
        scheduleScrollPanel.setHeight("20em");
        panel.add(scheduleScrollPanel);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                scheduleScrollPanel.setVerticalScrollPosition(prevScrollPosition);
            }
        });

        return panel;
    }

    private IsWidget redraw() {
        if (schedulePanel == null) {
            schedulePanel = new FlexTable();
        }

        schedulePanel.setWidth("15em");

        int row = -1;
        for (TimeSegmentDTO segment : schedule.schedule()) {
            schedulePanel.setWidget(++row, 0, createTimeCell(segment));
        }

        return schedulePanel;
    }

    private IsWidget createTimeCell(final TimeSegmentDTO segment) {
        final HTML cell = new HTML(new SafeHtmlBuilder()
                .appendEscaped(timeFormat.format(segment.start().getValue()) + " - " + timeFormat.format(segment.end().getValue())).toSafeHtml().asString());

        cell.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        cell.getElement().getStyle().setPaddingRight(1, Unit.EM);
        cell.getElement().getStyle().setProperty("borderWidth", "1px");
        cell.getElement().getStyle().setProperty("borderStyle", "outset");
        cell.getElement().getStyle().setProperty("borderRadius", "5px");

        cell.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                processSelection((HTML) event.getSource(), segment);
                setCellStyling(cell, segment.status().getValue());
            }

        });

        setCellStyling(cell, segment.status().getValue());

        return cell;

    }

    private void processSelection(HTML cell, TimeSegmentDTO segment) {
        if (segment.status().getValue() == Status.free) {
            segment.status().setValue(Status.booked);
            if (selected.isEmpty() || selected.get(selected.size() - 1).end().getValue().equals(segment.start().getValue())) {
                selected.add(segment);
            } else if (selected.get(0).start().getValue().equals(segment.end().getValue())) {
                selected.add(0, segment);
            } else {
                for (TimeSegmentDTO s : selected) {
                    s.status().setValue(Status.free);
                }
                selected.clear();
                selected.add(segment);
            }
        } else if (segment.status().getValue() == Status.booked) {
            segment.status().setValue(Status.free);
            selected.remove(segment);
        }
        // init redraw
        prevScrollPosition = scheduleScrollPanel.getVerticalScrollPosition(); // hack to put the scroll bar to the same position after redraw initiated by setValue
        // this is workaround againts setting the same value in the setValue() (because we want to actually fire the event);
        ScheduleDTO clone = EntityFactory.create(ScheduleDTO.class);
        clone.schedule().set(getValue().schedule());
        clone.scheduleDate().setValue(getValue().scheduleDate().getValue());
        setValue(clone, true);

    }

    private static void setCellStyling(HTML cell, TimeSegmentDTO.Status status) {
        if (status != null) {
            final Style style = cell.asWidget().getElement().getStyle();
            style.setCursor(Cursor.POINTER);
            switch (status) {
            case free:
                style.setBackgroundColor("#DEFFBC");
                break;
            case busy:
                style.setBackgroundColor("#FFA7A7");
                break;
            case booked:
                style.setBackgroundColor("#99CCFF");
                break;
            }
        } else {
            cell.setVisible(false);
        }
    }

}
