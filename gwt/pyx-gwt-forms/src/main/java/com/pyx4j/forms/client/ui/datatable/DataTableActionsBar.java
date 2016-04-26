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
 */
package com.pyx4j.forms.client.ui.datatable;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.SimplePanel;

import com.pyx4j.widgets.client.Toolbar;

public class DataTableActionsBar extends SimplePanel implements DataTableModelListener {

    private final DataTablePanel<?> dataTablePanel;

    private DataTableModel<?> model;

    private final Toolbar toolbar;

    private PageNavigBar pageNavigBar;

    public DataTableActionsBar(DataTablePanel<?> dataTablePanel, boolean showPageNavigBar) {
        this.dataTablePanel = dataTablePanel;
        setStyleName(DataTableTheme.StyleName.DataTableActionsBar.name());

        FlowPanel content = new FlowPanel();
        setWidget(content);
        content.setStyleName(DataTableTheme.StyleName.DataTableActionsBarContent.name());

        toolbar = new Toolbar();
        toolbar.addStyleName(DataTableTheme.StyleName.DataTableToolBar.name());
        content.add(toolbar);

        if (showPageNavigBar) {
            pageNavigBar = new PageNavigBar(this);
            pageNavigBar.addStyleName(DataTableTheme.StyleName.DataTablePageNavigBar.name());
            content.add(pageNavigBar);
        }

    }

    public DataTablePanel<?> getDataTablePanel() {
        return dataTablePanel;
    }

    public void setDataTableModel(DataTableModel<?> model) {
        if (this.model != null) {
            this.model.removeDataTableModelListener(this);
        }
        this.model = model;
        model.addDataTableModelListener(this);
    }

    public DataTableModel<?> getDataTableModel() {
        return model;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onDataTableModelChanged(DataTableModelEvent e) {
        if (pageNavigBar != null) {
            pageNavigBar.onTableModelChanged(e);
        }
    }

    public void setFirstActionCommand(Command firstActionCommand) {
        if (pageNavigBar != null) {
            pageNavigBar.setFirstActionCommand(firstActionCommand);
        }
    }

    public void setPrevActionCommand(Command prevActionCommand) {
        if (pageNavigBar != null) {
            pageNavigBar.setPrevActionCommand(prevActionCommand);
        }
    }

    public void setNextActionCommand(Command nextActionCommand) {
        if (pageNavigBar != null) {
            pageNavigBar.setNextActionCommand(nextActionCommand);
        }
    }

    public void setLastActionCommand(Command lastActionCommand) {
        if (pageNavigBar != null) {
            pageNavigBar.setLastActionCommand(lastActionCommand);
        }
    }

    public void setPageSizeActionCommand(Command pageSizeActionCommand) {
        if (pageNavigBar != null) {
            pageNavigBar.setPageSizeActionCommand(pageSizeActionCommand);
        }
    }

    public void setPageSizeOptions(List<Integer> pageSizeOptions) {
        if (pageNavigBar != null) {
            pageNavigBar.setPageSizeOptions(pageSizeOptions);
        }
    }

}
