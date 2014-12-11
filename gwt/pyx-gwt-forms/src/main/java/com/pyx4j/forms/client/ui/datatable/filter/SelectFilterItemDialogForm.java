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
 * Created on Dec 9, 2014
 * @author arminea
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.datatable.DataTablePanel;

public class SelectFilterItemDialogForm extends FlowPanel {

    private Collection<FilterItem> alwaysShownSelected;

    private Collection<FilterItem> removableSelected;

    public SelectFilterItemDialogForm(Collection<FilterItem> alreadySelected, DataTablePanel<?> dataTablePanel) {
        super();
        initForm(alreadySelected, dataTablePanel);
    }

    private void initForm(Collection<FilterItem> alreadySelected, DataTablePanel<?> dataTablePanel) {
        final ScrollPanel listScrollPanel = new ScrollPanel();

        listScrollPanel.setHeight("500px");
        listScrollPanel.setWidth("100%");

        dealSelectedItems(alreadySelected);

        FilterItemLister<FilterItemDTO> lister = new FilterItemLister<FilterItemDTO>(dataTablePanel, this, removableSelected);
        lister.populate();

        listScrollPanel.add(lister);
        add(listScrollPanel);
    }

    private void dealSelectedItems(Collection<FilterItem> selected) {
        alwaysShownSelected = new ArrayList<FilterItem>();

        removableSelected = new ArrayList<FilterItem>();

        for (FilterItem current : selected) {
            if (current.isRemovable()) {
                removableSelected.add(current);
            } else {
                alwaysShownSelected.add(current);
            }
        }

    }

    public Collection<FilterItem> getSelectedItems() {
        Collection<FilterItem> result = new ArrayList<FilterItem>();
        result.addAll(alwaysShownSelected);
        result.addAll(removableSelected);
        return result;
    }

    public void addSelected(Collection<FilterItem> changed) {
        if (changed != null && changed.size() != 0) {
            removableSelected.addAll(changed);
        }

    }

    public void removeSelected(Collection<FilterItem> changed) {
        if (changed != null && changed.size() != 0) {
            removableSelected.removeAll(changed);
        }
    }
}
