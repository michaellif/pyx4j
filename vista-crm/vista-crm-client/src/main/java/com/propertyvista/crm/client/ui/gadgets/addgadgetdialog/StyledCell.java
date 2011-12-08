/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 8, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.addgadgetdialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class StyledCell<T> extends CompositeCell<T> {
    private final String elementTagOpen;

    private static final String elementTagClose = "</div>";

    public StyledCell(final Cell<T> childCell, String cssClass) {
        super(toList(childCell));
        if (cssClass != null) {
            // TODO add style name validation
            this.elementTagOpen = "<div class=\"" + cssClass + "\">";
        } else {
            throw new Error("cssClass param contained illegal characters");
        }
    }

    private static <T> List<HasCell<T, ?>> toList(final Cell<T> childCell) {
        List<HasCell<T, ?>> hasCell = new ArrayList<HasCell<T, ?>>(1);
        hasCell.add(new HasCell<T, T>() {
            @Override
            public Cell<T> getCell() {

                return childCell;
            }

            @Override
            public FieldUpdater<T, T> getFieldUpdater() {
                return null;
            }

            @Override
            public T getValue(T object) {
                return object;
            }
        });
        return hasCell;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, T value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.appendHtmlConstant(elementTagOpen);
            super.render(context, value, sb);
            sb.appendHtmlConstant(elementTagClose);
        }
    }

    @Override
    protected Element getContainerElement(Element parent) {
        return parent.getFirstChildElement();
    }
}
