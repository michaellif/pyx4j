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
package com.pyx4j.forms.client.ui.datatable.filter;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.widgets.client.selector.EditableItemHolder;
import com.pyx4j.widgets.client.selector.SelectorListBoxValuePanel;

public class FilterItemHolder extends EditableItemHolder<FilterItem> {

    private final IFilterEditor editor;

    public FilterItemHolder(FilterItem item, SelectorListBoxValuePanel<FilterItem> valuePanel) {
        super(item, new FilterItemFormatter(), item.isRemovable(), valuePanel);
        ColumnDescriptor columnDescriptor = item.getColumnDescriptor();
        editor = createFilterEditor(columnDescriptor.getMemeber());
        editor.asWidget().setWidth("200px");
        setEditor(editor);
    }

    @Override
    protected boolean onEditingComplete() {
        try {
            getItem().setCriterion(editor.getCriterion());
            return super.onEditingComplete();
        } catch (CriterionInitializationException e) {
            return false;
        }
    }

    private IFilterEditor createFilterEditor(IObject<?> member) {
        Class<?> valueClass = member.getValueClass();
        if (member.getMeta().isEntity()) {
            return new SuggestableMultiSelectFilterEditor<>((IEntity) member);
        } else if (valueClass.isEnum() || valueClass.equals(Boolean.class)) {
            return new MultiSelectFilterEditor(member);
        } else if (valueClass.equals(String.class)) {
            return new TextQueryFilterEditor(member);
        } else if (valueClass.equals(Date.class) || valueClass.equals(java.sql.Date.class) || valueClass.equals(LogicalDate.class)) {
            return new DateFilterEditor(member);
        } else if (valueClass.equals(BigDecimal.class) || valueClass.equals(Key.class) || member.getMeta().isNumberValueClass()) {
            return new NumberFilterEditor(member);
        } else {
            throw new Error("Filter can't be created");
        }

    }

    @Override
    protected void onEditorShown() {
        super.onEditorShown();
        editor.setCriterion(getItem().getCriterion());
        editor.onShown();
    };

    @Override
    protected void onEditorHidden() {
        super.onEditorHidden();
        editor.clear();
        editor.onHidden();
    };

    @Override
    public boolean isEditorShownOnAttach() {
        return getItem().isEditorShownOnAttach();
    }
}
