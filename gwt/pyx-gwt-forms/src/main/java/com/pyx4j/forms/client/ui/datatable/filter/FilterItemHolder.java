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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.widgets.client.selector.EditableItemHolder;
import com.pyx4j.widgets.client.selector.SelectorListBoxValuePanel;

public class FilterItemHolder extends EditableItemHolder<FilterItem> {

    public FilterItemHolder(FilterItem item, IFormatter<FilterItem, String> valueFormatter, SelectorListBoxValuePanel<FilterItem> valuePanel) {
        super(item, valueFormatter, item.isRemovable(), valuePanel);
        ColumnDescriptor columnDescriptor = item.getColumnDescriptor();
        IsWidget editor = createEditor(columnDescriptor.getMemeber());
        editor.asWidget().setWidth("200px");
        setEditor(editor);
    }

    @Override
    protected void onEditingComplete() {
        System.out.println("+++++++++++++++++333");
    }

    private IsWidget createEditor(IObject<?> member) {
        return new HTML("TESTTEST TESTTEST TESTTEST TESTTEST");
    }

}
