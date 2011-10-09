/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-02
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.gadgets;


import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dialog.DialogPanel;

import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;

public class AddGadgetBox extends DialogPanel {

    private final I18n i18n = I18n.get(AddGadgetBox.class);

    private final ListBox gadgetsList = new ListBox();

    private final Label gadgetDesc = new Label();

    private IGadget selectedGadget = null;

    public AddGadgetBox() {
        super(false, true);
        setCaption(i18n.tr("Gadget Directory"));

        listAvailableGadgets();

        HorizontalPanel gadgets = new HorizontalPanel();
        gadgets.add(gadgetsList);
        gadgets.add(gadgetDesc);
        gadgets.setSpacing(8);
        gadgets.setWidth("100%");

        gadgets.setCellWidth(gadgetsList, "35%");
        gadgetsList.setWidth("100%");

        // style right (description) cell:
        gadgetDesc.setText(i18n.tr("Select desired gadget in the list..."));
        Element cell = DOM.getParent(gadgetDesc.getElement());
        cell.getStyle().setPadding(3, Unit.PX);
        cell.getStyle().setBorderStyle(BorderStyle.SOLID);
        cell.getStyle().setBorderWidth(1, Unit.PX);
        cell.getStyle().setBorderColor("#bbb");

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(new Button("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createSelectedGadget();
                hide();
            }
        }));
        buttons.add(new Button("Cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        }));
        buttons.setSpacing(8);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(gadgets);
        vPanel.add(buttons);
        vPanel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_CENTER);
        vPanel.setSpacing(8);
        vPanel.setSize("100%", "100%");

        setWidget(vPanel);
        setSize("400px", "150px");
//        getElement().getStyle().setProperty("minWidth", "400px");
//        getElement().getStyle().setProperty("minHeight", "150px");
    }

    public IGadget getSelectedGadget() {
        return selectedGadget;
    }

    private void listAvailableGadgets() {
        gadgetsList.clear();
        for (GadgetType gt : GadgetType.values()) {
            gadgetsList.addItem(gt.name());
        }
        gadgetsList.setSelectedIndex(-1);
        gadgetsList.setVisibleItemCount(8);
        gadgetsList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                if (gadgetsList.getSelectedIndex() >= 0) {
                    gadgetDesc.setText(GadgetsFactory.getGadgetTypeDescription(GadgetType.valueOf(gadgetsList.getItemText(gadgetsList.getSelectedIndex()))));
                }
            }
        });
    }

    private void createSelectedGadget() {
        selectedGadget = null;
        if (gadgetsList.getSelectedIndex() >= 0) {
            selectedGadget = GadgetsFactory.createGadget(GadgetType.valueOf(gadgetsList.getItemText(gadgetsList.getSelectedIndex())), null);
        }
    }
}