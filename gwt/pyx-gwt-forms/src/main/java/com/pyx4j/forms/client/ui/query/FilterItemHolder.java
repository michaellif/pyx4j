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

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.query.ICondition;
import com.pyx4j.widgets.client.selector.EditableItemHolder;
import com.pyx4j.widgets.client.selector.SelectorListBoxValuePanel;

public class FilterItemHolder extends EditableItemHolder<FilterItem> {

    private IFilterEditor editor;

    public FilterItemHolder(FilterItem item, SelectorListBoxValuePanel<FilterItem> valuePanel) {
        super(item, new FilterItemFormatter(), true, valuePanel);
    }

    @Override
    protected boolean onEditingComplete() {
        try {
            getItem().setCondition(getEditor().getCondition());
            return super.onEditingComplete();
        } catch (ConditionInitializationException e) {
            return false;
        }
    }

    @Override
    public IFilterEditor getEditor() {
//        if (editor == null) {
//            Class<?> valueClass = condition.getValueClass();
//            if (condition.getMeta().isEntity()) {
//                return new SuggestableMultiSelectFilterEditor<>((IEntity) member);
//            } else if (valueClass.isEnum() || valueClass.equals(Boolean.class)) {
//                return new MultiSelectFilterEditor(member);
//            } else if (valueClass.equals(String.class)) {
//                return new TextQueryFilterEditor(member);
//            } else if (valueClass.equals(Date.class) || valueClass.equals(java.sql.Date.class) || valueClass.equals(LogicalDate.class)) {
//                return new DateFilterEditor(member);
//            } else if (valueClass.equals(BigDecimal.class) || valueClass.equals(Key.class) || member.getMeta().isNumberValueClass()) {
//                return new NumberFilterEditor(member);
//            } else {
//                throw new Error("Filter can't be created");
//            }
//            editor.asWidget().getElement().getStyle().setProperty("maxWidth", "250px");
//            editor.asWidget().getElement().getStyle().setProperty("minWidth", "200px");
//        }
        return editor;
    }

    @Override
    protected void onEditorShown() {
        super.onEditorShown();
        IFilterEditor editor = getEditor();
        editor.setCondition(getItem().getCondition());
        editor.onShown();
    };

    @Override
    protected void onEditorHidden() {
        super.onEditorHidden();
        IFilterEditor editor = getEditor();
        editor.clear();
        editor.onHidden();
    };

    @Override
    public boolean isEditorShownOnAttach() {
        return true;
    }
}
