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
 * Created on Jan 19, 2012
 * @author artem
 * @version $Id$
 */
package com.pyx4j.site.client.ui.dialogs;

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

public abstract class EntitySelectorListDialog<E extends IEntity> extends AbstractEntitySelectorDialog<E> {

    private static final I18n i18n = I18n.get(EntitySelectorListDialog.class);

    private final SelectionModel<E> selectionModel;

    private final Formatter<E> formatter;

    private CellList<E> cellList;

    private List<E> data;

    public EntitySelectorListDialog(String caption, boolean isMultiselectAllowed, List<E> data, Formatter<E> formatter) {
        super(caption);
        this.data = data;
        this.selectionModel = isMultiselectAllowed ? new MultiSelectionModel<E>() : new SingleSelectionModel<E>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                getOkButton().setEnabled(false);
                for (E item : EntitySelectorListDialog.this.data) {
                    if (selectionModel.isSelected(item)) {
                        getOkButton().setEnabled(true);
                        return;
                    }
                }
            }
        });
        this.formatter = formatter != null ? formatter : new Formatter<E>() {
            @Override
            public String format(E entity) {
                return entity.getStringView();
            }
        };

        setBody(initBody(isMultiselectAllowed, data));
        setDialogPixelWidth(defineWidth());

        getOkButton().setEnabled(false);
    }

    public EntitySelectorListDialog(String caption, boolean isMultiselectAllowed, List<E> data) {
        this(caption, isMultiselectAllowed, data, null);
    }

    /** Create single select dialog */
    public EntitySelectorListDialog(String caption, List<E> data) {
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

    public int defineWidth() {
        return 600;
    }

    protected Widget initBody(boolean isMultiselectAllowed, List<E> data) {

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

        cellList = new CellList<E>(cell, new FakeCellListResources());
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
        panel.getElement().getStyle().setProperty("padding", "5px");
        return panel;
    }

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
        if (cellList != null) {
            cellList.setRowData(data);
        }
    }

    public static interface Formatter<E> {
        public String format(E entity);
    }
}
