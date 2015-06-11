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

import java.util.ArrayList;
import java.util.EnumSet;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import com.pyx4j.entity.core.query.IEnumCondition;
import com.pyx4j.widgets.client.OptionGroup.Layout;

@SuppressWarnings("rawtypes")
public class MultiSelectFilterEditor extends FilterEditorBase<IEnumCondition> {

    private final CheckGroup<Enum> checkGroup;

    @SuppressWarnings({ "unchecked" })
    public MultiSelectFilterEditor(IEnumCondition condition) {
        super(condition);

        checkGroup = new CheckGroup<Enum>(Layout.VERTICAL);
        checkGroup.setEmptyFieldFormatter();

        checkGroup.setOptions(new ArrayList(EnumSet.allOf((Class<Enum>) condition.values().getValueClass())));

        initWidget(checkGroup);

        checkGroup.setValue(getCondition().values());

    }

    @SuppressWarnings("unchecked")
    @Override
    public void save() {
        getCondition().values().clear();
        getCondition().values().addAll(checkGroup.getValue());
    }

    @Override
    public void onShown() {
        super.onShown();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                checkGroup.setFocus(true);
            }
        });
    }

}
