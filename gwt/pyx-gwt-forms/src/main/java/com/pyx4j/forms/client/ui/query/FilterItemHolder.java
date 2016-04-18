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
 * Created on Dec 8, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.query;

import com.pyx4j.entity.core.query.IBooleanCondition;
import com.pyx4j.entity.core.query.ICondition;
import com.pyx4j.entity.core.query.IDateCondition;
import com.pyx4j.entity.core.query.IDateOffsetCondition;
import com.pyx4j.entity.core.query.IDecimalRangeCondition;
import com.pyx4j.entity.core.query.IEntityCondition;
import com.pyx4j.entity.core.query.IEnumCondition;
import com.pyx4j.entity.core.query.IIntegerRangeCondition;
import com.pyx4j.entity.core.query.IStringCondition;
import com.pyx4j.widgets.client.selector.EditableItemHolder;
import com.pyx4j.widgets.client.selector.SelectorListBoxValuePanel;

public class FilterItemHolder extends EditableItemHolder<FilterItem> {

    private IFilterEditor editor;

    public FilterItemHolder(FilterItem item, SelectorListBoxValuePanel<FilterItem> valuePanel) {
        super(item, new FilterItemFormatter(), true, valuePanel);
    }

    @SuppressWarnings("rawtypes")
    private static IFilterEditor createFilterEditor(ICondition condition) {
        IFilterEditor editor = null;
        if (condition instanceof IEntityCondition) {
            editor = new SuggestableMultiSelectFilterEditor((IEntityCondition) condition);
        } else if (condition instanceof IEnumCondition) {
            editor = new MultiSelectFilterEditor((IEnumCondition) condition);
        } else if (condition instanceof IBooleanCondition) {
            editor = new BooleanFilterEditor((IBooleanCondition) condition);
        } else if (condition instanceof IStringCondition) {
            editor = new TextQueryFilterEditor((IStringCondition) condition);
        } else if (condition instanceof IDateOffsetCondition) {
            editor = new DateOffsetFilterEditor((IDateOffsetCondition) condition);
        } else if (condition instanceof IDateCondition) {
            editor = new DateFilterEditor((IDateCondition) condition);
        } else if (condition instanceof IIntegerRangeCondition) {
            editor = new IntegerFilterEditor((IIntegerRangeCondition) condition);
        } else if (condition instanceof IDecimalRangeCondition) {
            editor = new DecimalFilterEditor((IDecimalRangeCondition) condition);
        } else {
            throw new Error("Filter can't be created");
        }
        editor.getStyle().setProperty("maxWidth", "250px");
        editor.getStyle().setProperty("minWidth", "200px");
        editor.populate();

        return editor;
    }

    @Override
    public IFilterEditor getEditor() {
        if (editor == null) {
            editor = createFilterEditor(getItem().getCondition());
        }
        return editor;
    }

    @Override
    protected void onEditorShown() {
        super.onEditorShown();
        getEditor().onShown();
    };

    @Override
    public boolean isEditorShownOnAttach() {
        return getItem().isEditorShownOnAttach();
    }

    @Override
    protected boolean onEditingComplete() {
        try {
            getEditor().save();
            return super.onEditingComplete();
        } catch (ConditionInitializationException e) {
            return false;
        }
    }
}
