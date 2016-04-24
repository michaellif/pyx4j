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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.TextBox;

public class NumberFilterEditor extends FilterEditorBase {

    @com.pyx4j.i18n.annotations.I18n
    public enum NumberType {
        decimal, integer;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    private static final I18n i18n = I18n.get(NumberFilterEditor.class);

    private final NumberBox fromBox;

    private final ValidationLabel fromBoxValidationLabel;

    private final NumberBox toBox;

    private final ValidationLabel toBoxValidationLabel;

    public NumberFilterEditor(IObject<?> member) {
        super(member);
        FlowPanel contentPanel = new FlowPanel();
        initWidget(contentPanel);

        contentPanel.add(new Label(i18n.tr("From:")));

        fromBox = new NumberBox();
        contentPanel.add(fromBox);
        fromBoxValidationLabel = new ValidationLabel(fromBox);
        fromBoxValidationLabel.getStyle().setColor("red");
        contentPanel.add(fromBoxValidationLabel);

        fromBox.addValueChangeHandler(new ValueChangeHandler<Serializable>() {

            @Override
            public void onValueChange(ValueChangeEvent<Serializable> event) {
                fromBoxValidationLabel.setMessage(fromBox.isParsedOk() ? null : fromBox.getParseExceptionMessage());
            }
        });

        contentPanel.add(new Label(i18n.tr("To:")));

        toBox = new NumberBox();
        contentPanel.add(toBox);
        toBoxValidationLabel = new ValidationLabel(toBox);
        toBoxValidationLabel.getStyle().setColor("red");
        contentPanel.add(toBoxValidationLabel);

        toBox.addValueChangeHandler(new ValueChangeHandler<Serializable>() {

            @Override
            public void onValueChange(ValueChangeEvent<Serializable> event) {
                toBoxValidationLabel.setMessage(toBox.isParsedOk() ? null : toBox.getParseExceptionMessage());
            }
        });

        Label descrLabel = new Label(i18n.tr("Enter a minimum, maximum or range limit"));
        descrLabel.getStyle().setPaddingTop(10, Unit.PX);
        contentPanel.add(descrLabel);
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
                PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;
                Serializable fromValue = null;
                Serializable toValue = null;
                switch (propertyCriterion.getRestriction()) {
                case EQUAL:
                    fromValue = toValue = propertyCriterion.getValue();
                    break;
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUAL:
                    fromValue = propertyCriterion.getValue();
                    break;
                case LESS_THAN:
                case LESS_THAN_OR_EQUAL:
                    toValue = propertyCriterion.getValue();
                    break;
                default:
                    throw new Error("Conversion from " + criterion + " to range unimplemented");
                }
                rangeCriterion = new RangeCriterion(propertyCriterion.getPropertyPath(), fromValue, toValue);
            } else {
                throw new Error("Conversion from " + criterion + " to range unimplemented");
            }

            if (!getMember().getPath().equals(rangeCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't match filter criterion path");
            }

            fromBox.setValue(rangeCriterion.getFromValue());
            toBox.setValue(rangeCriterion.getToValue());
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

    class NumberBox extends TextBox<Serializable> {
        NumberBox() {

            setParser(new IParser<Serializable>() {

                @Override
                public Serializable parse(String string) throws ParseException {
                    if (string == null || string.trim().equals("")) {
                        return null;
                    }

                    MemberMeta meta = getMember().getMeta();
                    NumberType numberType = null;

                    try {
                        if (meta.getValueClass().equals(Key.class)) {
                            numberType = NumberType.integer;
                            return Long.parseLong(string);
                        } else if (meta.getValueClass().equals(Integer.class)) {
                            numberType = NumberType.integer;
                            return Integer.parseInt(string);
                        } else if (meta.getValueClass().equals(Long.class)) {
                            numberType = NumberType.integer;
                            return Long.parseLong(string);
                        } else if (meta.getValueClass().equals(BigInteger.class)) {
                            numberType = NumberType.integer;
                            return new BigInteger(string);
                        } else if (meta.getValueClass().equals(Float.class)) {
                            numberType = NumberType.decimal;
                            return Float.parseFloat(string);
                        } else if (meta.getValueClass().equals(Double.class)) {
                            numberType = NumberType.decimal;
                            return Double.parseDouble(string);
                        } else if (meta.getValueClass().equals(BigDecimal.class)) {
                            numberType = NumberType.decimal;
                            return new BigDecimal(string);
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        throw new ParseException(i18n.tr("Invalid format. Must be specified using {0}.", numberType), 0);
                    }
                }
            });

            setFormatter(new IFormatter<Serializable, String>() {

                @Override
                public String format(Serializable value) {
                    return value.toString();
                }
            });
        }
    }

}
