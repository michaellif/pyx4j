/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 14, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

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

import com.pyx4j.widgets.client.dialog.OkDialog;

public abstract class SelectTypeDialog<E extends Enum<E>> extends OkDialog {
    public static interface CellTemplate extends SafeHtmlTemplates {
        @Template("{0}")
        SafeHtml typeCell(String type);
    }

    private static final CellTemplate TEMPLATE = GWT.create(CellTemplate.class);

    private final SingleSelectionModel<E> selectionModel;

    public SelectTypeDialog(String caption, EnumSet<E> values) {
        super(caption);
        assert !values.isEmpty() : "The set of values must not be empty";
        this.selectionModel = new SingleSelectionModel<E>();
        setBody(initBody(selectionModel, values, defineHeight()));
        setWidth(defineWidth());
    }

    private static <E extends Enum<E>> Widget initBody(SelectionModel<E> selectionModel, EnumSet<E> values, String height) {
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
        return "30em";
    }
}
