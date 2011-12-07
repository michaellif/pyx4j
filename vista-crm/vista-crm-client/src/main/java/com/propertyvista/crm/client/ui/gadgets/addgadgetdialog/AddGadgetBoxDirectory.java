/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 6, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.addgadgetdialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.DialogPanel;

import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.Directory;
import com.propertyvista.crm.client.ui.gadgets.util.Collections2;
import com.propertyvista.crm.client.ui.gadgets.util.Predicate;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

// TODO change styling
public class AddGadgetBoxDirectory extends DialogPanel {
    private static final I18n i18n = I18n.get(AddGadgetBoxDirectory.class);

    private static final String DIALOG_WIDTH = "80em";

    private static final String DIALOG_HEIGHT = "50em";

    private static final String BUTTONS_PANEL_HEIGHT = "5em";

    private final ListDataProvider<AbstractGadget<?>> selectedGadgetsListProvider;

    private boolean isOK = false;

    /**
     * @param dashboardType
     *            <b>Warning:</b> <code>null</code> won't be tolerated here!
     */
    public AddGadgetBoxDirectory(final DashboardType dashboardType) {
        super(false, true);
        selectedGadgetsListProvider = new ListDataProvider<AbstractGadget<?>>();

        setContentWidget(createContentPanel(dashboardType, selectedGadgetsListProvider));
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    }

    public List<AbstractGadget<?>> getSelectedGadgets() {
        return isOK ? selectedGadgetsListProvider.getList() : null;
    }

    private Widget createContentPanel(DashboardType dashboardType, ListDataProvider<AbstractGadget<?>> selectedGadgetsListProvider) {

        DockPanel content = new DockPanel();

        int row = -1;
        FormFlexPanel panel = new FormFlexPanel();
        panel.setSize("100%", "100%");

        ListDataProvider<AbstractGadget<?>> gadgetListProvider = new ListDataProvider<AbstractGadget<?>>();
        panel.setWidget(++row, 0, createCategoriesPanel(gadgetListProvider, dashboardType));
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setWidth(row, 0, "30%");

        panel.setWidget(row, 1, createGadgetsPanel(gadgetListProvider, null, new GadgetAdditionCell(selectedGadgetsListProvider.getList())));
        panel.getFlexCellFormatter().setWidth(row, 1, "70%");
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
        content.add(panel, DockPanel.CENTER);
        content.setCellHeight(panel, "100%");

        Widget buttonsPanel = createButtonsPanel();
        content.add(buttonsPanel, DockPanel.SOUTH);
        content.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_CENTER);
        content.setCellHeight(buttonsPanel, BUTTONS_PANEL_HEIGHT);

        final MultiSelectionModel<AbstractGadget<?>> selectedGadgetsSelectionModel = new MultiSelectionModel<AbstractGadget<?>>();

        Widget selectedGadgetsPanel = createGadgetsPanel(selectedGadgetsListProvider, selectedGadgetsSelectionModel, new GadgetDescriptionCell());
        content.add(selectedGadgetsPanel, DockPanel.SOUTH);
        content.setCellHorizontalAlignment(selectedGadgetsPanel, HasHorizontalAlignment.ALIGN_CENTER);
        content.setCellWidth(selectedGadgetsPanel, "30em");
        content.setCellHeight(selectedGadgetsPanel, "15em");

        return content;
    }

    private Widget createButtonsPanel() {
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
        return buttons;
    }

    private Widget createCategoriesPanel(final ListDataProvider<AbstractGadget<?>> gadgetListProvider, DashboardType dashboardType) {
        final Predicate<AbstractGadget<?>> supportedGadgetP;
        switch (dashboardType) {
        case building:
            supportedGadgetP = new Predicate<AbstractGadget<?>>() {
                @Override
                public boolean apply(AbstractGadget<?> paramT) {
                    return paramT.isBuildingGadget();
                }
            };
            break;
        case system:
            supportedGadgetP = new Predicate<AbstractGadget<?>>() {
                @Override
                public boolean apply(AbstractGadget<?> paramT) {
                    return !paramT.isBuildingGadget();
                }
            };
            break;
        default:
            throw new Error("the following type of dashboard is not supported or simply unknown: " + dashboardType);
        }

        final SingleSelectionModel<GadgetCategoryWrapper> selectionModel = new SingleSelectionModel<GadgetCategoryWrapper>();
        // TODO this is a HACK to show all the gadgets at the root node without root node being selected (i.e. to show all the gadgets by default when the add gadget dialog opens) : find some way to select the root node programmatically
        gadgetListProvider.getList().addAll(new ArrayList<AbstractGadget<?>>(Collections2.filter(Directory.DIRECTORY, supportedGadgetP)));

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                gadgetListProvider.getList().clear();
                GadgetCategoryWrapper gadgetCategoryWrapper = selectionModel.getSelectedObject();
                if (gadgetCategoryWrapper != null) {
                    gadgetListProvider.getList().addAll(gadgetCategoryWrapper.getGadgets());
                } else {
                    gadgetListProvider.getList().addAll(new ArrayList<AbstractGadget<?>>(Collections2.filter(Directory.DIRECTORY, supportedGadgetP)));
                }
            }
        });

        // TODO remove the workaround when it's not required
        // WORKAROUND START (see: http://code.google.com/p/google-web-toolkit/issues/detail?id=6359 for more details)
        Resources resource = GWT.create(Resources.class);
        StyleInjector.injectAtEnd("." + resource.cellTreeStyle().cellTreeTopItem() + " {margin-top: 0px;}");
        // WORKAROUND END
        CellTree categoriesTree = new CellTree(new GadgetCategoryTreeViewModel(selectionModel, supportedGadgetP), null, resource);
        return categoriesTree;
    }

    private Widget createGadgetsPanel(final ListDataProvider<AbstractGadget<?>> gadgetListProvider, final SelectionModel<AbstractGadget<?>> selectionModel,
            Cell<AbstractGadget<?>> gadgetCell) {
        DockPanel gadgetsPanel = new DockPanel();
        gadgetsPanel.setHeight("100%");
        gadgetsPanel.setWidth("100%");

        CellList<AbstractGadget<?>> gadgetList = new CellList<AbstractGadget<?>>(gadgetCell);
        gadgetList.setSelectionModel(selectionModel);
        gadgetListProvider.addDataDisplay(gadgetList);
        gadgetsPanel.add(gadgetList, DockPanel.CENTER);
        gadgetsPanel.setCellHeight(gadgetList, "100%");

        SimplePager pager = new SimplePager();
        pager.setDisplay(gadgetList);
        pager.setPageSize(10);

        gadgetsPanel.add(pager, DockPanel.SOUTH);
        gadgetsPanel.setCellHeight(pager, "2em");
        gadgetsPanel.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_CENTER);

        return gadgetsPanel;
    }
}
