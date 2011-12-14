/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 13, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class SelectDialog<E extends IEntity> extends OkCancelDialog {
    private static final I18n i18n = I18n.get(SelectDialog.class);

    private final SelectionModel<E> selectionModel;

    private final Formatter formatter;

    private final List<E> data;

    public SelectDialog(String caption, boolean isMultiselectAllowed, List<E> data, Formatter formatter) {
        super(caption);
        this.data = data;
        this.selectionModel = isMultiselectAllowed ? new MultiSelectionModel<E>() : new SingleSelectionModel<E>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                getOkButton().setEnabled(false);
                for (E item : SelectDialog.this.data) {
                    if (selectionModel.isSelected(item)) {
                        getOkButton().setEnabled(true);
                        return;
                    }
                }
            }
        });
        this.formatter = formatter != null ? formatter : new Formatter() {
            @Override
            public String format(E enntity) {
                return enntity.getStringView();
            }
        };
        setBody(initBody(isMultiselectAllowed, data));
        setWidth(defineWidth());
        getOkButton().setEnabled(false);
    }

    public SelectDialog(String caption, boolean isMultiselectAllowed, List<E> data) {
        this(caption, isMultiselectAllowed, data, null);
    }

    /** Create single select dialog */
    public SelectDialog(String caption, List<E> data) {
        this(caption, false, data, null);
    }

    protected List<E> getSelectedItems() {
        ArrayList<E> selected = new ArrayList<E>(data.size());
        for (E item : data) {
            if (selectionModel.isSelected(item)) {
                selected.add(item);
            }
        }
        return selected;
    }

    /** Override to set required height */
    public String defineHeight() {
        return "10em";
    }

    public String defineWidth() {
        return "40em";
    }

    private Widget initBody(boolean isMultiselectAllowed, List<E> data) {

        List<HasCell<E, ?>> cells = new ArrayList<HasCell<E, ?>>(2);
        if (isMultiselectAllowed) {
            cells.add(new HasCell<E, Boolean>() {
                private final CheckboxCell cell = new CheckboxCell(true, false);

                @Override
                public Cell<Boolean> getCell() {
                    return cell;
                }

                @Override
                public FieldUpdater<E, Boolean> getFieldUpdater() {
                    return null;
                }

                @Override
                public Boolean getValue(E object) {
                    return selectionModel.isSelected(object);
                }
            });
        }
        cells.add(new HasCell<E, String>() {
            private final Cell<String> cell = new TextCell();

            @Override
            public Cell<String> getCell() {
                return cell;
            }

            @Override
            public FieldUpdater<E, String> getFieldUpdater() {
                return null;
            }

            @Override
            public String getValue(E object) {
                return formatter.format(object);
            }
        });

        Cell<E> cell = new CompositeCell<E>(cells) {
            @Override
            public void render(Cell.Context context, E value, com.google.gwt.safehtml.shared.SafeHtmlBuilder sb) {
                sb.appendHtmlConstant("<table><tbody><tr>");
                super.render(context, value, sb);
                sb.appendHtmlConstant("</table></tbody></tr>");
            }

            @Override
            protected <X extends Object> void render(Cell.Context context, E value, com.google.gwt.safehtml.shared.SafeHtmlBuilder sb,
                    com.google.gwt.cell.client.HasCell<E, X> hasCell) {
                Cell<X> cell = hasCell.getCell();
                sb.appendHtmlConstant("<td>");
                cell.render(context, hasCell.getValue(value), sb);
                sb.appendHtmlConstant("</td>");
            };

            @Override
            protected Element getContainerElement(Element parent) {
                // Return the first TR element in the table.
                return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
            }
        };

        CellList<E> cellList = new CellList<E>(cell, new FakeCellListResources());
        cellList.setHeight(defineHeight());
        cellList.setWidth("100%");
        if (isMultiselectAllowed) {
            cellList.setSelectionModel(selectionModel, DefaultSelectionEventManager.<E> createCheckboxManager());
        } else {
            cellList.setSelectionModel(selectionModel);
        }
        cellList.setRowData(data);
        cellList.setEmptyListWidget(new Label(i18n.tr("There are no available items")));
        ScrollPanel panel = new ScrollPanel(cellList);
        panel.getElement().getStyle().setProperty("borderStyle", "inset");
        panel.getElement().getStyle().setProperty("borderWidth", "1px");
        return panel;
    }

    public abstract class Formatter {
        public abstract String format(E enntity);
    }
}
