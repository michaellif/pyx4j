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

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.widgets.client.StringBox;

public class TextQueryFilterEditor extends FilterEditorBase {

    private final StringBox queryBox;

    public TextQueryFilterEditor(IObject<?> member) {
        super(member);
        queryBox = new StringBox();
        initWidget(queryBox);
    }

    @Override
    public PropertyCriterion getCriterion() {
        if (queryBox.getValue() == null || queryBox.getValue().trim().equals("")) {
            return null;
        } else {
            return PropertyCriterion.like(getMember(), queryBox.getValue());
        }
    }

    @Override
    public void setCriterion(Criterion criterion) {
        if (criterion == null) {
            queryBox.setValue(null);
        } else {
            if (!(criterion instanceof PropertyCriterion)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

            if (propertyCriterion.getRestriction() != PropertyCriterion.Restriction.RDB_LIKE) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            if (!getMember().getPath().equals(propertyCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't mach filter criterion path");
            }

            if (!(propertyCriterion.getValue() instanceof String)) {
                throw new Error("Filter criterion value class is" + propertyCriterion.getValue().getClass().getSimpleName() + ". String is expected.");
            }

            queryBox.setValue((String) propertyCriterion.getValue());
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                queryBox.setFocus(true);
            }
        });
    }

    @Override
    public void clear() {
        queryBox.setValue(null);
    }
}
