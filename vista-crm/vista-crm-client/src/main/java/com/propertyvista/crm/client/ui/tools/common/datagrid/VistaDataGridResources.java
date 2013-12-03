/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;


public class VistaDataGridResources implements DataGrid.Resources {

    private static VistaDataGridResources INSTANCE;

    private final DataGrid.Resources DEFAULTS = GWT.create(DataGrid.Resources.class);

    private final DataGrid.Style DEFAULT_STYLE = DEFAULTS.dataGridStyle();

    private final DataGrid.Style VISTA_DATAGRID_STYLE = new DataGrid.Style() {

        @Override
        public boolean ensureInjected() {
            return DEFAULT_STYLE.ensureInjected();
        }

        @Override
        public String getText() {
            return DEFAULT_STYLE.getText();
        }

        @Override
        public String getName() {
            return DEFAULT_STYLE.getName();
        }

        @Override
        public String dataGridCell() {
            return DEFAULT_STYLE.dataGridCell();
        }

        @Override
        public String dataGridEvenRow() {
            return DEFAULT_STYLE.dataGridEvenRow();
        }

        @Override
        public String dataGridEvenRowCell() {
            return DEFAULT_STYLE.dataGridEvenRowCell();
        }

        @Override
        public String dataGridFirstColumn() {
            return DEFAULT_STYLE.dataGridFirstColumn();
        }

        @Override
        public String dataGridFirstColumnFooter() {
            return DEFAULT_STYLE.dataGridFirstColumnFooter();
        }

        @Override
        public String dataGridFirstColumnHeader() {
            return DEFAULT_STYLE.dataGridFirstColumnHeader();
        }

        @Override
        public String dataGridFooter() {
            return VistaDataGridStyles.VistaDataGridFooter.name();
        }

        @Override
        public String dataGridHeader() {
            return VistaDataGridStyles.VistaDataGridFooter.name();
        }

        @Override
        public String dataGridHoveredRow() {
            return DEFAULT_STYLE.dataGridHoveredRow();
        }

        @Override
        public String dataGridHoveredRowCell() {
            return DEFAULT_STYLE.dataGridHoveredRowCell();
        }

        @Override
        public String dataGridKeyboardSelectedCell() {
            return DEFAULT_STYLE.dataGridKeyboardSelectedCell();
        }

        @Override
        public String dataGridKeyboardSelectedRow() {
            return DEFAULT_STYLE.dataGridKeyboardSelectedRow();
        }

        @Override
        public String dataGridKeyboardSelectedRowCell() {
            return DEFAULT_STYLE.dataGridKeyboardSelectedRowCell();
        }

        @Override
        public String dataGridLastColumn() {
            return DEFAULT_STYLE.dataGridLastColumn();
        }

        @Override
        public String dataGridLastColumnFooter() {
            return DEFAULT_STYLE.dataGridLastColumnFooter();
        }

        @Override
        public String dataGridLastColumnHeader() {
            return DEFAULT_STYLE.dataGridLastColumnHeader();
        }

        @Override
        public String dataGridOddRow() {
            return DEFAULT_STYLE.dataGridOddRow();
        }

        @Override
        public String dataGridOddRowCell() {
            return DEFAULT_STYLE.dataGridOddRowCell();
        }

        @Override
        public String dataGridSelectedRow() {
            return DEFAULT_STYLE.dataGridSelectedRow();
        }

        @Override
        public String dataGridSelectedRowCell() {
            return DEFAULT_STYLE.dataGridSelectedRowCell();
        }

        @Override
        public String dataGridSortableHeader() {
            return DEFAULT_STYLE.dataGridSortableHeader();
        }

        @Override
        public String dataGridSortedHeaderAscending() {
            return DEFAULT_STYLE.dataGridSortedHeaderAscending();
        }

        @Override
        public String dataGridSortedHeaderDescending() {
            return DEFAULT_STYLE.dataGridSortedHeaderDescending();
        }

        @Override
        public String dataGridWidget() {
            return DEFAULT_STYLE.dataGridWidget();
        }
    };

    @Override
    public ImageResource dataGridLoading() {
        return DEFAULTS.dataGridLoading();
    }

    @Override
    public ImageResource dataGridSortAscending() {
        return DEFAULTS.dataGridSortAscending();
    }

    @Override
    public ImageResource dataGridSortDescending() {
        return DEFAULTS.dataGridSortDescending();
    }

    @Override
    public Style dataGridStyle() {
        return VISTA_DATAGRID_STYLE;
    }

    private VistaDataGridResources() {
    }

    public static VistaDataGridResources getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VistaDataGridResources();
        }
        return INSTANCE;
    }
}
