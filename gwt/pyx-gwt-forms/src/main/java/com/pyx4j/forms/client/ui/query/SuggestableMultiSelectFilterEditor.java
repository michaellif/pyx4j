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

import com.pyx4j.entity.core.query.IEntityCondition;
import com.pyx4j.forms.client.ui.selector.EntitySelectorListBox;
import com.pyx4j.i18n.shared.I18n;

@SuppressWarnings("rawtypes")
public class SuggestableMultiSelectFilterEditor extends FilterEditorBase<IEntityCondition> {

    private static final I18n i18n = I18n.get(SuggestableMultiSelectFilterEditor.class);

    private final EntitySelectorListBox selector;

    @SuppressWarnings("unchecked")
    public SuggestableMultiSelectFilterEditor(IEntityCondition condition) {
        super(condition);

        selector = new EntitySelectorListBox<>(condition.references().getValueClass());
        selector.setWatermark(i18n.tr("+ Add item"));

        initWidget(selector);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void populate() {
        selector.setValue(getCondition().references());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save() {
        getCondition().references().clear();
        getCondition().references().addAll(selector.getValue());
    }

    @Override
    public void onShown() {
        super.onShown();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                selector.setFocus(true);
            }
        });
    }

}
