/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-02
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;

/**
 * Same as GWT DataGrid, but with some convenience methods for defining columns based on subject contained entity type
 */
@Deprecated
public class VistaDataGrid<E extends IEntity> extends DataGrid<E> {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private final E proto;

    private final boolean createFooter;

    public VistaDataGrid(Class<E> klass, boolean createFooter) {
        super(DEFAULT_PAGE_SIZE, VistaDataGridResources.getInstance());
        DataGridScrollFixerHack.apply(this);
        this.createFooter = createFooter;
        this.proto = EntityFactory.getEntityPrototype(klass);
        this.addDefaultSortHandler();
    }

    protected E proto() {
        return this.proto;
    }

    protected void onSort(String memberPath, boolean isAscending) {
    }

    @Deprecated
    /**
     * deprecated because I don't like that some of the columns get caption from the <code>columnField</code> metadata, and some don't: it's better to have consistent API
     */
    protected void defTextColumn(IObject<String> columnField, double columWidth, Unit columnWidthUnit) {
        defTextColumn(columnField, columnField.getMeta().getCaption(), columWidth, columnWidthUnit);
    }

    protected Column<?, ?> defTextColumn(IObject<String> columnField, String headerCaption, double columWidth, Unit columnWidthUnit) {
        EntityFieldColumn<E, String> column = new EntityFieldColumn<E, String>(columnField, new TextCell());
        return defColumn(column, headerCaption, columWidth, columnWidthUnit);
    }

    protected Column<?, ?> defColumn(Column<E, ?> column, String headerCaption, double columWidth, Unit columnWidthUnit) {
        this.addColumn(column, new SafeHtmlBuilder().appendHtmlConstant("<div>").appendEscaped(headerCaption).appendHtmlConstant("</div>").toSafeHtml(),
                createFooter ? new SafeHtmlBuilder().appendHtmlConstant("<div>").appendEscaped(headerCaption).appendHtmlConstant("</div>").toSafeHtml() : null);
        this.setColumnWidth(column, columWidth, columnWidthUnit);
        return column;
    }

    private void addDefaultSortHandler() {
        ColumnSortEvent.Handler sortHandler = new ColumnSortEvent.Handler() {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                VistaDataGrid.this.onSort(event.getColumn().getDataStoreName(), event.isSortAscending());
            }
        };
        this.addColumnSortHandler(sortHandler);
    }

}
