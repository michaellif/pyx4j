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
package com.pyx4j.forms.client.ui.datatable.filter;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.DatePicker;
import com.pyx4j.widgets.client.Label;

public class DateFilterEditor extends FilterEditorBase {

    private static final I18n i18n = I18n.get(DateFilterEditor.class);

    private final DatePicker fromBox;

    private final DatePicker toBox;

    private final ValidationLabel fromBoxValidationLabel;

    private final ValidationLabel toBoxValidationLabel;

    public DateFilterEditor(IObject<?> member) {
        super(member);
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
    public RangeCriterion getCriterion() throws CriterionInitializationException {
        if (fromBox.isParsedOk() && fromBox.getValue() == null && toBox.isParsedOk() && toBox.getValue() == null) {
            return null;
        } else if (!fromBox.isParsedOk() || !toBox.isParsedOk()) {
            throw new CriterionInitializationException();
        } else {
            return new RangeCriterion(getMember(), fromBox.getValue(), toBox.getValue());
        }
    }

    @Override
    public void setCriterion(Criterion criterion) {
        if (criterion == null) {
            fromBox.setValue(null);
            toBox.setValue(null);
        } else {
            RangeCriterion rangeCriterion;
            if (criterion instanceof RangeCriterion) {
                rangeCriterion = (RangeCriterion) criterion;
            } else if (criterion instanceof PropertyCriterion) {
                // TODO Change the editor type in future
                rangeCriterion = toRangeCriterion((PropertyCriterion) criterion);
            } else {
                throw new Error("Conversion from " + criterion + " to range unimplemented");
            }

            if (!getMember().getPath().equals(rangeCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't match filter criterion path");
            }

            fromBox.setValue((LogicalDate) rangeCriterion.getFromValue());
            toBox.setValue((LogicalDate) rangeCriterion.getToValue());
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

    @Override
    public void clear() {
        fromBox.setValue(null);
        toBox.setValue(null);
        fromBoxValidationLabel.setMessage(null);
        toBoxValidationLabel.setMessage(null);
    }

}
