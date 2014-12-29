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

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.StringBox;

public class NumberFilterEditor extends FilterEditorBase implements IFilterEditor {

    private static final I18n i18n = I18n.get(NumberFilterEditor.class);

    private final StringBox fromBox;

    private final StringBox toBox;

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
    }

    @Override
    public RangeCriterion getCriterion() {
        if ((fromBox.getValue() == null || fromBox.getValue().trim().equals("")) && (toBox.getValue() == null || toBox.getValue().trim().equals(""))) {
            return null;
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
            if (!(criterion instanceof RangeCriterion)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            RangeCriterion rangeCriterion = (RangeCriterion) criterion;

            if (!getMember().getPath().toString().equals(rangeCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't match filter criterion path");
            }

            if (!(rangeCriterion.getFromValue() instanceof String)) {
                throw new Error("Filter criterion value class is" + rangeCriterion.getFromValue().getClass().getSimpleName() + ". String is expected.");
            }

            if (!(rangeCriterion.getToValue() instanceof String)) {
                throw new Error("Filter criterion value class is" + rangeCriterion.getFromValue().getClass().getSimpleName() + ". String is expected.");
            }

            fromBox.setValue((String) rangeCriterion.getFromValue());
            toBox.setValue((String) rangeCriterion.getToValue());
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
    }
}
