/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 14, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.widgets.client.selector.ItemHolder;
import com.pyx4j.widgets.client.selector.ItemHolderFactory;
import com.pyx4j.widgets.client.selector.SelectorListBox;

public class FilterPanel extends SelectorListBox<FilterItem> {

    private DataTablePanel<?> dataTablePanel;

    public FilterPanel(DataTablePanel<?> dataTablePanel) {
        super(new FilterOptionsGrabber(dataTablePanel), new Command() {
            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }
        }, new IFormatter<FilterItem, SafeHtml>() {
            @Override
            public SafeHtml format(FilterItem value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.appendHtmlConstant(SimpleMessageFormat.format("<div style=\"padding:2px;\">{0}</div>", value.toString()));
                return builder.toSafeHtml();
            }
        }, new ItemHolderFactory<FilterItem>() {

            @Override
            public ItemHolder<FilterItem> createItemHolder(FilterItem item) {
                return new ItemHolder<FilterItem>(item, new FilterItemFormatter());
            }
        });
        this.dataTablePanel = dataTablePanel;

    }
}