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
import java.util.Collections;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IGadgetDirectory;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

// TODO review styling/learn how to use the standard GWT resources
public abstract class GadgetDirectoryDialog extends Dialog implements OkOptionText, OkCancelOption {
    public static final String STYLE = "GadgetDirectoryDialog";

    public static final String GADGET_DIRECTORY_CELL_STYLE = "GadgetDirectoryCell";

    private static final I18n i18n = I18n.get(GadgetDirectoryDialog.class);

    private static final double DIALOG_WIDTH = 60;

    private static final double DIALOG_HEIGHT = 40;

    private static final double GADGETS_DIRECTORY_BROWSER_HEIGHT = 30;

    private static final double SELECTED_GADGETS_HEIGHT = 10;

    private ListDataProvider<IGadgetFactory> selectedGadgetsListProvider;

    private final IGadgetDirectory gadgetDirectory;

    /**
     * @param dashboardType
     *            <b>Warning:</b> <code>null</code> won't be tolerated here!
     */
    public GadgetDirectoryDialog(IGadgetDirectory gadgetDirectory) {
        super(i18n.tr("Gadget Directory"));
        this.gadgetDirectory = gadgetDirectory;
        setDialogOptions(this);
        setSize(getDialogWidth(), getDialogHeight());
        setBody(createContentPanel());
    }

    private Widget createContentPanel() {
        selectedGadgetsListProvider = new ListDataProvider<IGadgetFactory>();
        VerticalPanel content = new VerticalPanel();
        content.setSize("100%", "100%");
        content.setStylePrimaryName(STYLE);
        content.setSpacing(10);

        HorizontalPanel directoryBrowserPanel = new HorizontalPanel();
        directoryBrowserPanel.setSize("100%", "100%");

        Widget w;
        ListDataProvider<IGadgetFactory> gadgetListProvider = new ListDataProvider<IGadgetFactory>();
        w = createCategoriesPanel(gadgetListProvider);
        w.setHeight(getDirectoryBrowserHeight());
        directoryBrowserPanel.add(w);
        directoryBrowserPanel.setCellWidth(w, "30%");
        setBoxStyle(w.getElement().getParentElement().getStyle());

        w = createGadgetsListPanel(gadgetListProvider, null, new GadgetAdditionCell(selectedGadgetsListProvider.getList()));
        w.setHeight(getDirectoryBrowserHeight());
        directoryBrowserPanel.add(w);
        directoryBrowserPanel.setCellWidth(w, "70%");
        setBoxStyle(w.getElement().getParentElement().getStyle());

        content.add(directoryBrowserPanel);
        content.setCellHeight(directoryBrowserPanel, "100%");
        content.setCellWidth(directoryBrowserPanel, getDirectoryBrowserHeight());

        com.google.gwt.user.client.ui.CaptionPanel selectedGadgetsPanel = new com.google.gwt.user.client.ui.CaptionPanel(i18n.tr("Selected"));
        selectedGadgetsPanel.add(createGadgetsListPanel(selectedGadgetsListProvider, null, new SelectedGadgetCell(selectedGadgetsListProvider.getList())));
        selectedGadgetsPanel.setHeight(getSelectedGadgetsHeight());
        content.setCellHorizontalAlignment(selectedGadgetsPanel, HasHorizontalAlignment.ALIGN_CENTER);
        content.add(selectedGadgetsPanel);
        setBoxStyle(selectedGadgetsPanel.getElement().getStyle());

        return content;
    }

    private Widget createCategoriesPanel(final ListDataProvider<IGadgetFactory> gadgetListProvider) {

        final SingleSelectionModel<GadgetCategoryWrapper> selectionModel = new SingleSelectionModel<GadgetCategoryWrapper>();
        // TODO this is a HACK to show all the gadgets at the root node without root node being selected (i.e. to show all the gadgets by default when the add gadget dialog opens) : find some way to select the root node programmatically
        gadgetListProvider.getList().addAll(new ArrayList<IGadgetFactory>(gadgetDirectory.getAvailableGadgets()));

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                gadgetListProvider.getList().clear();
                GadgetCategoryWrapper gadgetCategoryWrapper = selectionModel.getSelectedObject();
                if (gadgetCategoryWrapper != null) {
                    gadgetListProvider.getList().addAll(gadgetCategoryWrapper.getGadgets());
                } else {
                    gadgetListProvider.getList().addAll(gadgetDirectory.getAvailableGadgets());
                }
            }
        });

        // TODO remove the workaround when it's not required
        // WORKAROUND START (see: http://code.google.com/p/google-web-toolkit/issues/detail?id=6359 for more details)
        Resources resources = GWT.create(CellTree.BasicResources.class);
        StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeTopItem() + " {margin-top: 0px;}");
        // WORKAROUND END
        // remove padding (i don't really know why it must be done here and not in the theme, but it that the way it works)
        StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeItem() + " {padding: 0px;}");
        CellTree categoriesTree = new CellTree(new GadgetCategoryTreeViewModel(selectionModel, gadgetDirectory), null, resources);
        categoriesTree.setAnimationEnabled(true);
        categoriesTree.getRootTreeNode().setChildOpen(0, true);
        return categoriesTree;
    }

    private Widget createGadgetsListPanel(final ListDataProvider<IGadgetFactory> gadgetListProvider, final SelectionModel<IGadgetFactory> selectionModel,
            Cell<IGadgetFactory> gadgetCell) {

        CellList<IGadgetFactory> gadgetList = new CellList<IGadgetFactory>(gadgetCell);
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

    @Override
    public boolean onClickOk() {
        if (selectedGadgetsListProvider.getList().isEmpty()) {
            return false;
        } else {
            // reverse list because addGadget() inserts gadgets from the top
            List<IGadgetFactory> factories = new ArrayList<IGadgetFactory>(selectedGadgetsListProvider.getList());
            Collections.reverse(factories);
            for (final IGadgetFactory factory : factories) {
                GWT.<DashboardMetadataService> create(DashboardMetadataService.class).createGadgetMetadata(new DefaultAsyncCallback<GadgetMetadata>() {

                    @Override
                    public void onSuccess(GadgetMetadata result) {
                        IGadgetInstance instance = factory.createGadget(null);
                        addGadget(instance);
                        instance.start();
                    }
                }, EntityFactory.getEntityPrototype(factory.getGadgetMetadataClass()));

            }
            return true;
        }
    }

    protected abstract void addGadget(IGadgetInstance gadget);

    @Override
    public boolean onClickCancel() {
        return true;
    }

    @Override
    public String optionTextOk() {
        return i18n.tr("Add Selected");
    }
}
