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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;

public class SelectedGadgetCell extends CompositeCell<IGadgetFactory> {
    private final static I18n i18n = I18n.get(SelectedGadgetCell.class);

    private final static GadgetCellTemplates TEMPLATES = GWT.create(GadgetCellTemplates.class);

    public SelectedGadgetCell(final List<IGadgetFactory> selectedGadgetsList) {
        super(createCells(selectedGadgetsList));
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, IGadgetFactory value, SafeHtmlBuilder sb) {

        if (value != null) {
            sb.appendHtmlConstant("<table style=\"width:100%; height:100%;\"><tbody><tr>");
            super.render(context, value, sb);
            sb.appendHtmlConstant("</tr></tbody></table>");
        }
    }

    @Override
    protected <X> void render(com.google.gwt.cell.client.Cell.Context context, IGadgetFactory value, SafeHtmlBuilder sb, HasCell<IGadgetFactory, X> hasCell) {
        Cell<X> cell = hasCell.getCell();
        if (!(cell instanceof ActionCell)) {
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

    private static List<HasCell<IGadgetFactory, ?>> createCells(final List<IGadgetFactory> selectedGadgetsList) {
        List<HasCell<IGadgetFactory, ?>> hasCells = new ArrayList<HasCell<IGadgetFactory, ?>>(2);

        hasCells.add(new HasCell<IGadgetFactory, IGadgetFactory>() {
            private final Cell<IGadgetFactory> description = new AbstractCell<IGadgetFactory>() {

                @Override
                public void render(com.google.gwt.cell.client.Cell.Context context, IGadgetFactory value, SafeHtmlBuilder sb) {
                    if (value != null) {
                        sb.append(TEMPLATES.gadgetCellWithTooltipDescription(value.getName(), value.getDescription()));
                    }
                }
            };

            @Override
            public Cell<IGadgetFactory> getCell() {
                return description;
            }

            @Override
            public FieldUpdater<IGadgetFactory, IGadgetFactory> getFieldUpdater() {
                return null;
            }

            @Override
            public IGadgetFactory getValue(IGadgetFactory object) {
                return object;
            }
        });
        hasCells.add(new HasCell<IGadgetFactory, IGadgetFactory>() {
            final ActionCell<IGadgetFactory> removeGadgetButtonCell = new ActionCell<IGadgetFactory>(i18n.tr("Remove"),
                    new ActionCell.Delegate<IGadgetFactory>() {
                        @Override
                        public void execute(IGadgetFactory gadget) {
                            if (gadget != null) {
                                selectedGadgetsList.remove(gadget);
                            }
                        }
                    });

            @Override
            public Cell<IGadgetFactory> getCell() {
                return removeGadgetButtonCell;
            }

            @Override
            public FieldUpdater<IGadgetFactory, IGadgetFactory> getFieldUpdater() {
                return null;
            }

            @Override
            public IGadgetFactory getValue(IGadgetFactory object) {
                return object;
            }
        });

        return hasCells;
    }
}
