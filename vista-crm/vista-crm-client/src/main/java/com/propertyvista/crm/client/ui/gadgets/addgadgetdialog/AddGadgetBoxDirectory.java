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
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.DialogPanel;

import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.Directory;
import com.propertyvista.crm.client.ui.gadgets.util.Collections2;
import com.propertyvista.crm.client.ui.gadgets.util.Predicate;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

// TODO review styling/learn how to use the standard GWT resources
public class AddGadgetBoxDirectory extends DialogPanel {
    public static final String STYLE = "GadgetDirectory";

    public static final String GADGET_DIRECTORY_CELL_STYLE = "GadgetDirectoryCell";

    private static final I18n i18n = I18n.get(AddGadgetBoxDirectory.class);

    private static final double DIALOG_WIDTH = 60;

    private static final double DIALOG_HEIGHT = 40;

    private static final double GADGETS_DIRECTORY_BROWSER_HEIGHT = 30;

    private static final double SELECTED_GADGETS_HEIGHT = 10;

    private final ListDataProvider<AbstractGadget<?>> selectedGadgetsListProvider;

    private boolean isOK = false;

    /**
     * @param dashboardType
     *            <b>Warning:</b> <code>null</code> won't be tolerated here!
     */
    public AddGadgetBoxDirectory(final DashboardType dashboardType) {
        super(false, true);
        setSize(getDialogWidth(), getDialogHeight());
        setCaption(i18n.tr("Gadget Directory"));
        setContentWidget(new SimplePanel(createContentPanel(dashboardType, selectedGadgetsListProvider = new ListDataProvider<AbstractGadget<?>>())));
    }

    public List<AbstractGadget<?>> getSelectedGadgets() {
        return isOK ? selectedGadgetsListProvider.getList() : null;
    }

    private Widget createContentPanel(DashboardType dashboardType, ListDataProvider<AbstractGadget<?>> selectedGadgetsListProvider) {
        VerticalPanel content = new VerticalPanel();
        content.setSize("100%", "100%");
        content.setStylePrimaryName(STYLE);
        content.setSpacing(10);

        HorizontalPanel directoryPanel = new HorizontalPanel();
        directoryPanel.setSize("100%", "100%");

        Widget w;
        ListDataProvider<AbstractGadget<?>> gadgetListProvider = new ListDataProvider<AbstractGadget<?>>();
        w = createCategoriesPanel(gadgetListProvider, dashboardType);
        w.setHeight(getDirectoryBrowserHeight());
        directoryPanel.add(w);
        directoryPanel.setCellWidth(w, "30%");
        setBoxStyle(w.getElement().getParentElement().getStyle());

        w = createGadgetsListPanel(gadgetListProvider, null, new GadgetAdditionCell(selectedGadgetsListProvider.getList()));
        w.setHeight(getDirectoryBrowserHeight());
        directoryPanel.add(w);
        directoryPanel.setCellWidth(w, "70%");
        setBoxStyle(w.getElement().getParentElement().getStyle());

        content.add(directoryPanel);
        content.setCellHeight(directoryPanel, "100%");
        content.setCellWidth(directoryPanel, getDirectoryBrowserHeight());

        Widget buttonsPanel = createButtonsPanel(selectedGadgetsListProvider);
        content.add(buttonsPanel);
        content.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_CENTER);

        Widget selectedGadgetsPanel = createGadgetsListPanel(selectedGadgetsListProvider, null, new GadgetDescriptionCell());
        selectedGadgetsPanel.setHeight(getSelectedGadgetsHeight());
        content.add(selectedGadgetsPanel);
        setBoxStyle(selectedGadgetsPanel.getElement().getParentElement().getStyle());

        Widget cancelButton = new Button(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        content.add(cancelButton);
        content.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_CENTER);

        return content;
    }

    private Widget createButtonsPanel(final ListDataProvider<AbstractGadget<?>> selectedGadgetsProvider) {
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(new Button(i18n.tr("Add Selected"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!selectedGadgetsProvider.getList().isEmpty()) {
                    isOK = true;
                    hide();
                }
            }
        }));
        buttons.add(new Button(i18n.tr("Clear"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectedGadgetsProvider.getList().clear();
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
        Resources resources = GWT.create(CellTree.BasicResources.class);
        StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeTopItem() + " {margin-top: 0px;}");
        // WORKAROUND END
        CellTree categoriesTree = new CellTree(new GadgetCategoryTreeViewModel(selectionModel, supportedGadgetP), null, resources);
        categoriesTree.setAnimationEnabled(true);
        return categoriesTree;
    }

    private Widget createGadgetsListPanel(final ListDataProvider<AbstractGadget<?>> gadgetListProvider, final SelectionModel<AbstractGadget<?>> selectionModel,
            Cell<AbstractGadget<?>> gadgetCell) {

        CellList<AbstractGadget<?>> gadgetList = new CellList<AbstractGadget<?>>(new StyledCell<AbstractGadget<?>>(gadgetCell, GADGET_DIRECTORY_CELL_STYLE));
        gadgetList.setSelectionModel(selectionModel);
        gadgetListProvider.addDataDisplay(gadgetList);

        ScrollPanel gadgetsPanel = new ScrollPanel(gadgetList);
        gadgetsPanel.setHeight("100%");
        gadgetsPanel.setWidth("100%");

        return gadgetsPanel;
    }

    private static String getDirectoryBrowserHeight() {
        return "" + GADGETS_DIRECTORY_BROWSER_HEIGHT + "em";
    }

    private static String getSelectedGadgetsHeight() {
        return "" + SELECTED_GADGETS_HEIGHT + "em";
    }

    private static String getDialogWidth() {
        return "" + DIALOG_WIDTH + "em";
    }

    private static String getDialogHeight() {
        return "" + DIALOG_HEIGHT + "em";
    }

    private static void setBoxStyle(Style style) {
        style.setProperty("padding", "5px");
        style.setProperty("borderStyle", "inset");
        style.setProperty("borderWidth", "1px");
    }
}
