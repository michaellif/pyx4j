/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 7, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.addgadgetdialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;

public class GadgetAdditionCell extends CompositeCell<AbstractGadget<?>> {
    private static final I18n i18n = I18n.get(GadgetAdditionCell.class);

    public GadgetAdditionCell(final List<AbstractGadget<?>> selectedGadgetsList) {
        super(createCells(selectedGadgetsList));
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, AbstractGadget<?> value, SafeHtmlBuilder sb) {

        if (value != null) {
            sb.appendHtmlConstant("<table style=\"width:100%; height:100%;\"><tbody><tr>");
            super.render(context, value, sb);
            sb.appendHtmlConstant("</tr></tbody></table>");
        }
    }

    @Override
    protected <X> void render(com.google.gwt.cell.client.Cell.Context context, AbstractGadget<?> value, SafeHtmlBuilder sb,
            HasCell<AbstractGadget<?>, X> hasCell) {
        Cell<X> cell = hasCell.getCell();
        if (cell instanceof GadgetDescriptionCell) {
            sb.appendHtmlConstant("<td style=\"padding-right : 1em; width:100%;\">");
        } else {
            sb.appendHtmlConstant("<td>");
        }
        cell.render(context, hasCell.getValue(value), sb);
        sb.appendHtmlConstant("</td>");
    }

    @Override
    protected Element getContainerElement(Element parent) {
        return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
    }

    private static List<HasCell<AbstractGadget<?>, ?>> createCells(final List<AbstractGadget<?>> selectedGadgetsList) {
        List<HasCell<AbstractGadget<?>, ?>> hasCells = new ArrayList<HasCell<AbstractGadget<?>, ?>>(2);
        hasCells.add(new HasCell<AbstractGadget<?>, AbstractGadget<?>>() {
            private final GadgetDescriptionCell description = new GadgetDescriptionCell();

            @Override
            public Cell<AbstractGadget<?>> getCell() {
                return description;
            }

            @Override
            public FieldUpdater<AbstractGadget<?>, AbstractGadget<?>> getFieldUpdater() {
                return null;
            }

            @Override
            public AbstractGadget<?> getValue(AbstractGadget<?> object) {
                return object;
            }
        });
        hasCells.add(new HasCell<AbstractGadget<?>, AbstractGadget<?>>() {
            final ActionCell<AbstractGadget<?>> addGadgetButtonCell = new ActionCell<AbstractGadget<?>>(i18n.tr("Select"),
                    new ActionCell.Delegate<AbstractGadget<?>>() {
                        @Override
                        public void execute(AbstractGadget<?> object) {
                            if (object != null) {
                                selectedGadgetsList.add(object);
                            }
                        }
                    });

            @Override
            public Cell<AbstractGadget<?>> getCell() {
                return addGadgetButtonCell;
            }

            @Override
            public FieldUpdater<AbstractGadget<?>, AbstractGadget<?>> getFieldUpdater() {
                return null;
            }

            @Override
            public AbstractGadget<?> getValue(AbstractGadget<?> object) {
                return object;
            }
        });

        return hasCells;
    }
}
