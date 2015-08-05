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

import java.text.ParseException;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.entity.core.query.IIntegerRangeCondition;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.TextBox;

public class IntegerFilterEditor extends FilterEditorBase<IIntegerRangeCondition> {

    private static final I18n i18n = I18n.get(IntegerFilterEditor.class);

    private final IntegerBox fromBox;

    private final IntegerBox toBox;

    private final ValidationLabel fromBoxValidationLabel;

    private final ValidationLabel toBoxValidationLabel;

    public IntegerFilterEditor(IIntegerRangeCondition condition) {
        super(condition);
        FlowPanel contentPanel = new FlowPanel();
        initWidget(contentPanel);

        contentPanel.add(new Label(i18n.tr("From:")));

        fromBox = new IntegerBox();
        contentPanel.add(fromBox);
        fromBoxValidationLabel = new ValidationLabel(fromBox);
        fromBoxValidationLabel.getElement().getStyle().setColor("red");
        contentPanel.add(fromBoxValidationLabel);

        fromBox.addValueChangeHandler(new ValueChangeHandler<Long>() {

            @Override
            public void onValueChange(ValueChangeEvent<Long> event) {
                fromBoxValidationLabel.setMessage(fromBox.isParsedOk() ? null : fromBox.getParseExceptionMessage());
            }
        });

        contentPanel.add(new Label(i18n.tr("To:")));

        toBox = new IntegerBox();
        contentPanel.add(toBox);
        toBoxValidationLabel = new ValidationLabel(toBox);
        toBoxValidationLabel.getElement().getStyle().setColor("red");
        contentPanel.add(toBoxValidationLabel);

        toBox.addValueChangeHandler(new ValueChangeHandler<Long>() {

            @Override
            public void onValueChange(ValueChangeEvent<Long> event) {
                toBoxValidationLabel.setMessage(toBox.isParsedOk() ? null : toBox.getParseExceptionMessage());
            }
        });

        Label descrLabel = new Label(i18n.tr("Enter a minimum, maximum or range limit"));
        descrLabel.getElement().getStyle().setPaddingTop(10, Unit.PX);
        contentPanel.add(descrLabel);

    }

    @Override
    public void populate() {
        fromBox.setValue(getCondition().fromInteger().getValue());
        toBox.setValue(getCondition().toInteger().getValue());
    }

    @Override
    public void save() throws ConditionInitializationException {
        if (!fromBox.isParsedOk() || !toBox.isParsedOk()) {
            throw new ConditionInitializationException();
        } else {
            getCondition().fromInteger().setValue(fromBox.getValue());
            getCondition().toInteger().setValue(toBox.getValue());
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

    class IntegerBox extends TextBox<Long> {
        IntegerBox() {

            setParser(new IParser<Long>() {

                @Override
                public Long parse(String string) throws ParseException {
                    if (string == null || string.trim().equals("")) {
                        return null;
                    }
                    return Long.parseLong(string);
                }
            });

            setFormatter(new IFormatter<Long, String>() {

                @Override
                public String format(Long value) {
                    return value.toString();
                }
            });
        }
    }

}
