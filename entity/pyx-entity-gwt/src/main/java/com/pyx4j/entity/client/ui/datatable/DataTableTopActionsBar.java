/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Feb 24, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.widgets.client.actionbar.Toolbar;

public class DataTableTopActionsBar extends SimplePanel implements DataTableModelListener {

    private DataTableModel<?> model;

    private final Toolbar toolbar;

    public DataTableTopActionsBar() {
        setStyleName(DefaultDataTableTheme.StyleName.DataTableActionsBar.name());

        toolbar = new Toolbar();
        setWidget(toolbar);
    }

    public void setDataTableModel(DataTableModel<?> model) {
        if (this.model != null) {
            this.model.removeDataTableModelListener(this);
        }
        this.model = model;
        model.addDataTableModelListener(this);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onTableModelChanged(DataTableModelEvent e) {

    }

}
