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
package com.propertyvista.crm.client.ui.gadgets;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.DialogPanel;

import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public class AddGadgetBox extends DialogPanel {
    private static final GadgetFactoryCellTemplate GADGET_FACTORY_CELL_TEMPLATE = GWT.create(GadgetFactoryCellTemplate.class);

    private static final I18n i18n = I18n.get(AddGadgetBox.class);

    private final CellList<IGadgetFactory> gadgets;

    private final SingleSelectionModel<IGadgetFactory> selectionModel;

    private boolean isOK = false;

    public AddGadgetBox(final DashboardType dashboardType) {
        // FIXME styling
        super(false, true);
        setCaption(i18n.tr("Gadget Directory"));

        gadgets = new CellList<IGadgetFactory>(new GadgetFactoryCell());
        gadgets.setHeight("100%");

        selectionModel = new SingleSelectionModel<IGadgetFactory>();
        gadgets.setSelectionModel(selectionModel);

        SimplePager pager = new SimplePager();
        pager.setDisplay(gadgets);
        pager.setPageSize(10);
        pager.setWidth("100%");

        final ListDataProvider<IGadgetFactory> provider = new ListDataProvider<IGadgetFactory>();
        provider.addDataDisplay(gadgets);
        provider.setList(getAvailableGadgets(dashboardType));

        FormFlexPanel addGadgetBoxPanel = new FormFlexPanel();
        addGadgetBoxPanel.setWidget(0, 0, gadgets);
        addGadgetBoxPanel.setWidget(1, 0, pager);
        addGadgetBoxPanel.getFlexCellFormatter().setHeight(1, 0, "1em");
        addGadgetBoxPanel.setWidth("100%");

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(new Button(i18n.tr("Add"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                isOK = true;
                hide();
            }
        }));
        buttons.add(new Button(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        }));
        buttons.setSpacing(8);
        addGadgetBoxPanel.setWidget(2, 0, buttons);
        addGadgetBoxPanel.getFlexCellFormatter().setColSpan(2, 0, 2);
        addGadgetBoxPanel.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);

        setContentWidget(addGadgetBoxPanel);
        setSize("100%", "100%");
    }

    public IGadgetInstanceBase getSelectedGadget() {
        if (isOK) {
            IGadgetFactory gadgetFactory = selectionModel.getSelectedObject();
            return gadgetFactory != null ? gadgetFactory.createGadget(null) : null;
        } else {
            return null;
        }
    }

    private List<IGadgetFactory> getAvailableGadgets(DashboardType dashboardType) {
        List<IGadgetFactory> factories = new LinkedList<IGadgetFactory>();
        for (IGadgetFactory gadgetFactory : Directory.DIRECTORY) {
            if (gadgetFactory.isAcceptedBy(dashboardType)) {
                factories.add(gadgetFactory);
            }
        }
        return factories;
    }

    public interface GadgetFactoryCellTemplate extends SafeHtmlTemplates {
        @Template("<div title=\"{1}\" style=\"text-align: center\">{0}</div>")
        SafeHtml factoryCellWithTooltipDescription(String name, String description);
    }

    private class GadgetFactoryCell extends AbstractCell<IGadgetFactory> {
        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, IGadgetFactory value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.append(GADGET_FACTORY_CELL_TEMPLATE.factoryCellWithTooltipDescription(value.getName(), value.getDescription()));
            }
        }
    }
}