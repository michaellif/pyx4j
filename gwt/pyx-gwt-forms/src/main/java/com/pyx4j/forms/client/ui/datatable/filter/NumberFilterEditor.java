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
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.StringBox;

public class NumberFilterEditor extends FilterEditorBase implements IFilterEditor {

    @com.pyx4j.i18n.annotations.I18n
    public enum NumberType {
        decimal, integer;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    private static final I18n i18n = I18n.get(NumberFilterEditor.class);

    private final StringBox fromBox;

    private final StringBox toBox;

    private final HTML validationLabel;

    public NumberFilterEditor(IObject<?> member) {
        super(member);
        FlowPanel contentPanel = new FlowPanel();
        initWidget(contentPanel);

        contentPanel.add(new Label(i18n.tr("From:")));

        fromBox = new StringBox();
        contentPanel.add(fromBox);

        contentPanel.add(new Label(i18n.tr("To:")));

        toBox = new StringBox();
        contentPanel.add(toBox);

        validationLabel = new HTML();
        validationLabel.getElement().getStyle().setColor("red");
        contentPanel.add(validationLabel);

    }

    @Override
    public RangeCriterion getCriterion() throws CriterionInitializationException {
        if ((fromBox.getValue() == null || fromBox.getValue().trim().equals("")) && (toBox.getValue() == null || toBox.getValue().trim().equals(""))) {
            return null;
        } else {
            MemberMeta meta = getMember().getMeta();
            NumberType numberType = null;
            Serializable fromValue = null;
            Serializable toValue = null;
            try {
                if (meta.getValueClass().equals(Integer.class)) {
                    numberType = NumberType.integer;
                    fromValue = fromBox.getValue() == null ? null : Integer.parseInt(fromBox.getValue());
                    toValue = toBox.getValue() == null ? null : Integer.parseInt(toBox.getValue());
                } else if (meta.getValueClass().equals(Long.class)) {
                    numberType = NumberType.integer;
                    fromValue = fromBox.getValue() == null ? null : Long.parseLong(fromBox.getValue());
                    toValue = toBox.getValue() == null ? null : Long.parseLong(toBox.getValue());
                } else if (meta.getValueClass().equals(BigInteger.class)) {
                    numberType = NumberType.integer;
                    fromValue = fromBox.getValue() == null ? null : new BigInteger(fromBox.getValue());
                    toValue = toBox.getValue() == null ? null : new BigInteger(toBox.getValue());
                } else if (meta.getValueClass().equals(Float.class)) {
                    numberType = NumberType.decimal;
                    fromValue = fromBox.getValue() == null ? null : Float.parseFloat(fromBox.getValue());
                    toValue = toBox.getValue() == null ? null : Float.parseFloat(toBox.getValue());
                } else if (meta.getValueClass().equals(Double.class)) {
                    numberType = NumberType.decimal;
                    fromValue = fromBox.getValue() == null ? null : Double.parseDouble(fromBox.getValue());
                    toValue = toBox.getValue() == null ? null : Double.parseDouble(toBox.getValue());
                } else if (meta.getValueClass().equals(BigDecimal.class)) {
                    numberType = NumberType.decimal;
                    fromValue = fromBox.getValue() == null ? null : new BigDecimal(fromBox.getValue());
                    toValue = toBox.getValue() == null ? null : new BigDecimal(toBox.getValue());
                }
                return new RangeCriterion(getMember(), fromValue, toValue);
            } catch (Exception e) {
                String validationMessage = i18n.tr("Limits must be specified using {0}", numberType);
                validationLabel.setText(validationMessage);
                throw new CriterionInitializationException(validationMessage);
            }
        }
    }

    @Override
    public void setCriterion(Criterion criterion) {
        if (criterion == null) {
            fromBox.setValue(null);
            toBox.setValue(null);
        } else {
            if (!(criterion instanceof RangeCriterion)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            RangeCriterion rangeCriterion = (RangeCriterion) criterion;

            if (!getMember().getPath().toString().equals(rangeCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't match filter criterion path");
            }

            Serializable fromValue = rangeCriterion.getFromValue();
            Serializable toValue = rangeCriterion.getToValue();

            fromBox.setValue(fromValue == null ? null : fromValue.toString());
            toBox.setValue(toValue == null ? null : toValue.toString());
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        fromBox.setFocus(true);
    }

    @Override
    public void clear() {
        fromBox.setValue(null);
        toBox.setValue(null);
        validationLabel.setText(null);
    }
}
