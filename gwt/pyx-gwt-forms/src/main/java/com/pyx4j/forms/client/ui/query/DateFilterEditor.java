/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Dec 22, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.query;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.pyx4j.gwt.commons.ui.FlowPanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.query.IDateCondition;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.DatePicker;
import com.pyx4j.widgets.client.Label;

public class DateFilterEditor extends FilterEditorBase<IDateCondition> {

    private static final I18n i18n = I18n.get(DateFilterEditor.class);

    private final DatePicker fromBox;

    private final DatePicker toBox;

    private final ValidationLabel fromBoxValidationLabel;

    private final ValidationLabel toBoxValidationLabel;

    public DateFilterEditor(IDateCondition condition) {
        super(condition);
        FlowPanel contentPanel = new FlowPanel();
        initWidget(contentPanel);

        contentPanel.add(new Label(i18n.tr("Between")));

        fromBox = new DatePicker();
        contentPanel.add(fromBox);
        fromBoxValidationLabel = new ValidationLabel(fromBox);
        fromBoxValidationLabel.getStyle().setColor("red");
        contentPanel.add(fromBoxValidationLabel);

        fromBox.addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                fromBoxValidationLabel.setMessage(fromBox.isParsedOk() ? null : fromBox.getParseExceptionMessage());
            }
        });

        contentPanel.add(new Label(i18n.tr("and")));

        toBox = new DatePicker();
        contentPanel.add(toBox);
        toBoxValidationLabel = new ValidationLabel(toBox);
        toBoxValidationLabel.getStyle().setColor("red");
        contentPanel.add(toBoxValidationLabel);

        toBox.addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                toBoxValidationLabel.setMessage(toBox.isParsedOk() ? null : toBox.getParseExceptionMessage());
            }
        });

    }

    @Override
    public void populate() {
        fromBox.setValue(getCondition().fromDate().isNull() ? null : new LogicalDate(getCondition().fromDate().getValue()));
        toBox.setValue(getCondition().toDate().isNull() ? null : new LogicalDate(getCondition().toDate().getValue()));
    }

    @Override
    public void save() throws ConditionInitializationException {
        if (!fromBox.isParsedOk() || !toBox.isParsedOk()) {
            throw new ConditionInitializationException();
        } else {
            getCondition().fromDate().setValue(fromBox.getValue());
            getCondition().toDate().setValue(toBox.getValue());
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                fromBox.setFocus(true);
            }
        });
    }

}
