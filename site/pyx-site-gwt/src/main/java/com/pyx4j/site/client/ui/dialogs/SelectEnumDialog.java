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
import java.util.EnumSet;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class SelectEnumDialog<E extends Enum<E>> extends OkCancelDialog {
    public static interface CellTemplate extends SafeHtmlTemplates {
        @Template("{0}")
        SafeHtml typeCell(String type);
    }

    private static final CellTemplate TEMPLATE = GWT.create(CellTemplate.class);

    private final SingleSelectionModel<E> selectionModel;

    public SelectEnumDialog(String caption, EnumSet<E> values) {
        super(caption);
        assert !values.isEmpty() : "The set of values must not be empty";
        this.selectionModel = new SingleSelectionModel<E>();
        setBody(initBody(selectionModel, values, defineHeight()));
        setWidth(defineWidth());
    }

    protected <E extends Enum<E>> Widget initBody(SelectionModel<E> selectionModel, EnumSet<E> values, String height) {
        CellList<E> list = new CellList<E>(new AbstractCell<E>() {
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, E value, SafeHtmlBuilder sb) {
                sb.append(TEMPLATE.typeCell(value.toString()));
            }
        }, new FakeCellListResources());
        list.setHeight(height);
        list.setWidth("100%");
        list.setSelectionModel(selectionModel);

        ArrayList<E> listOfValues = new ArrayList<E>(values);
        list.setRowData(listOfValues);
        selectionModel.setSelected(listOfValues.get(0), true);
        ScrollPanel panel = new ScrollPanel(list);
        panel.getElement().getStyle().setProperty("borderStyle", "inset");
        panel.getElement().getStyle().setProperty("borderWidth", "1px");
        return panel;
    }

    protected E getSelectedType() {
        return selectionModel.getSelectedObject();
    }

    public String defineHeight() {
        return "10em";
    }

    public String defineWidth() {
        return "25em";
    }
}
