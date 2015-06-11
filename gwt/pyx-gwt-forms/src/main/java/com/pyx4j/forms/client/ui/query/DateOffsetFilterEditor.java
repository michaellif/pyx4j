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
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.core.query.IDateOffsetCondition;
import com.pyx4j.entity.core.query.IDateOffsetCondition.DateOffset;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.IntegerBox;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.ListBox;

public class DateOffsetFilterEditor extends FilterEditorBase<IDateOffsetCondition> {

    private static final I18n i18n = I18n.get(DateOffsetFilterEditor.class);

    private final ListBox offsetTypeBox;

    private final IntegerBox offsetBox;

    private final ValidationLabel dateOffsetTypeValidationLabel;

    private final ValidationLabel toBoxValidationLabel;

    public DateOffsetFilterEditor(IDateOffsetCondition condition) {
        super(condition);
        FlowPanel contentPanel = new FlowPanel();
        initWidget(contentPanel);

        contentPanel.add(new Label(i18n.tr("Step")));

        offsetTypeBox = new ListBox();
        for (DateOffset dateOffset : DateOffset.values()) {
            offsetTypeBox.addItem(dateOffset.toString());
        }
        contentPanel.add(offsetTypeBox);
        dateOffsetTypeValidationLabel = new ValidationLabel(offsetTypeBox);
        dateOffsetTypeValidationLabel.getElement().getStyle().setColor("red");
        contentPanel.add(dateOffsetTypeValidationLabel);

        contentPanel.add(new Label(i18n.tr("Offset")));

        offsetBox = new IntegerBox();
        offsetBox.setWidth("100px");
        contentPanel.add(offsetBox);
        toBoxValidationLabel = new ValidationLabel(offsetBox);
        toBoxValidationLabel.getElement().getStyle().setColor("red");
        contentPanel.add(toBoxValidationLabel);

        offsetBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                toBoxValidationLabel.setMessage(offsetBox.isParsedOk() ? null : offsetBox.getParseExceptionMessage());
            }
        });

        offsetTypeBox.setSelectedIndex(getCondition().dateOffsetType().getValue() == null ? DateOffset.Days.ordinal() : getCondition().dateOffsetType()
                .getValue().ordinal());
        offsetBox.setValue(getCondition().dateOffsetValue().getValue());

    }

    @Override
    public void save() throws ConditionInitializationException {
        if (!offsetBox.isParsedOk()) {
            throw new ConditionInitializationException();
        } else {
            getCondition().dateOffsetType().setValue(DateOffset.values()[offsetTypeBox.getSelectedIndex()]);
            getCondition().dateOffsetValue().setValue(offsetBox.getValue());
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                offsetBox.setFocus(true);
                offsetBox.setCursorToEnd();
            }
        });
    }

}
