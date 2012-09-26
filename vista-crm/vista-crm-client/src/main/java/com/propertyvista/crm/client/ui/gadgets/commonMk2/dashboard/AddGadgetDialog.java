/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.crm.client.ui.gadgets.addgadgetdialog.GadgetCellTemplates;
import com.propertyvista.crm.rpc.dto.dashboard.GadgetDescriptorDTO;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public abstract class AddGadgetDialog extends OkCancelDialog implements OkOptionText {

    public static class GadgetDescriptorCell extends AbstractCell<GadgetDescriptorDTO> {

        private static final GadgetCellTemplates GADGET_CELL_TEMPLATES = GWT.create(GadgetCellTemplates.class);

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, GadgetDescriptorDTO value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.append(GADGET_CELL_TEMPLATES.gadgetCellWithInlineDescription(value.getName(), value.getDescription()));
            }
        }

    }

    public abstract static class KeywordsSelectionBox extends Composite {

        private final FlowPanel widgetPanel;

        private final ListBox potentialKeywordsBox;

        private final Button addKeywordButton;

        private final Set<String> activeKeywords;

        private final Set<String> potentialKeywords;

        private final FlowPanel activeKeywordsPanel;

        public KeywordsSelectionBox() {
            activeKeywords = new HashSet<String>();
            potentialKeywords = new HashSet<String>();

            final Command addKeywordCommand = new Command() {
                @Override
                public void execute() {
                    if (potentialKeywordsBox.getSelectedIndex() != -1) {
                        String keyword = potentialKeywordsBox.getItemText(potentialKeywordsBox.getSelectedIndex());
                        if (!"".equals(keyword)) {
                            potentialKeywords.remove(keyword);
                            activeKeywords.add(keyword);
                            updateKeywords(true);
                        }
                    }
                }
            };
            activeKeywordsPanel = new FlowPanel();
            activeKeywordsPanel.getElement().getStyle().setProperty("display", "inline");
            potentialKeywordsBox = new ListBox(false);
            potentialKeywordsBox.getElement().getStyle().setProperty("float", "left");
            potentialKeywordsBox.getElement().getStyle().setProperty("display", "inline");

            addKeywordButton = new Button("+", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    addKeywordCommand.execute();
                }
            });

            widgetPanel = new FlowPanel();
            widgetPanel.setWidth("100%");
            widgetPanel.add(activeKeywordsPanel);

            widgetPanel.add(potentialKeywordsBox);
            widgetPanel.add(addKeywordButton);

            initWidget(widgetPanel);
        }

        public void setKeywords(Set<String> keywords, boolean execCallback) {
            potentialKeywords.clear();
            potentialKeywords.addAll(keywords);
            activeKeywords.clear();
            updateKeywords(execCallback);
        }

        protected abstract void onKeywordsChanged(Set<String> keywords);

        private void updateKeywords(boolean execCallback) {
            potentialKeywordsBox.clear();

            for (String keyword : potentialKeywords) {
                potentialKeywordsBox.addItem(keyword, keyword);
            }
            potentialKeywordsBox.setVisible(!potentialKeywords.isEmpty());
            addKeywordButton.setVisible(!potentialKeywords.isEmpty());

            activeKeywordsPanel.clear();
            for (final String keyword : activeKeywords) {
                final Button keywordButton = new Button(keyword);
                keywordButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        activeKeywordsPanel.remove(keywordButton);

                        activeKeywords.remove(keyword);
                        potentialKeywords.add(keyword);

                        updateKeywords(true);
                    }
                });
                keywordButton.getElement().getStyle().setMarginRight(5, Unit.PX);
                activeKeywordsPanel.add(keywordButton);
            }

            if (execCallback) {
                onKeywordsChanged(activeKeywords);
            }
        }
    }

    private static final I18n i18n = I18n.get(AddGadgetDialog.class);

    private boolean isLoading;

    private final VerticalPanel body;

    private final CellList<GadgetDescriptorDTO> descriptorsListWidget;

    private final List<GadgetDescriptorDTO> descriptors;

    public AddGadgetDialog(DashboardMetadata.DashboardType boardType) {
        super(i18n.tr("Available Gadgets"));
        isLoading = true;

        descriptors = new ArrayList<GadgetDescriptorDTO>();

        descriptorsListWidget = new CellList<GadgetDescriptorDTO>(new GadgetDescriptorCell());
        descriptorsListWidget.setSelectionModel(new SingleSelectionModel<GadgetDescriptorDTO>());

        ScrollPanel descriptorListScrollPanel = new ScrollPanel(descriptorsListWidget);
        descriptorListScrollPanel.setWidth("100%");
        descriptorListScrollPanel.setHeight("30em");

        final ListDataProvider<GadgetDescriptorDTO> descriptorsProvider = new ListDataProvider<GadgetDescriptorDTO>();
        descriptorsProvider.addDataDisplay(descriptorsListWidget);

        final KeywordsSelectionBox keywordsSelectionBox = new KeywordsSelectionBox() {
            @Override
            protected void onKeywordsChanged(Set<String> keywords) {
                if (keywords == null || keywords.isEmpty()) {
                    descriptorsProvider.setList(descriptors);
                } else {
                    List<GadgetDescriptorDTO> filteredDescriptors = new ArrayList<GadgetDescriptorDTO>();
                    for (GadgetDescriptorDTO descriptor : descriptors) {
                        boolean isFiltered = true;
                        for (String keyword : keywords) {
                            if (!descriptor.getKeywords().contains(keyword)) {
                                isFiltered = false;
                                break;
                            }
                        }
                        if (isFiltered) {
                            filteredDescriptors.add(descriptor);
                        }
                    }
                    descriptorsProvider.setList(filteredDescriptors);
                }
            }
        };
        HTML keywordSelectionBoxLabel = new HTML(i18n.tr("Filter:"));

        body = new VerticalPanel();
        body.setSize("100%", "100%");
        body.add(keywordSelectionBoxLabel);
        body.add(keywordsSelectionBox);
        body.add(descriptorListScrollPanel);

        GWT.<DashboardMetadataService> create(DashboardMetadataService.class).listAvailableGadgets(new DefaultAsyncCallback<Vector<GadgetDescriptorDTO>>() {
            @Override
            public void onSuccess(Vector<GadgetDescriptorDTO> result) {
                isLoading = false;
                descriptors.clear();
                descriptors.addAll(result);

                Set<String> keywords = new HashSet<String>();
                for (GadgetDescriptorDTO descriptor : result) {
                    keywords.addAll(descriptor.getKeywords());
                }
                keywordsSelectionBox.setKeywords(keywords, true);
            }
        }, boardType);
        setBody(body);
        setSize("600px", "300px");
    }

    @Override
    public boolean onClickOk() {
        if (!isLoading) {
            GadgetDescriptorDTO selectedDescriptor = ((SingleSelectionModel<GadgetDescriptorDTO>) descriptorsListWidget.getSelectionModel())
                    .getSelectedObject();
            if (selectedDescriptor != null) {
                addGadget(selectedDescriptor.getProto());
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String optionTextOk() {
        return i18n.tr("Add");
    }

    protected abstract void addGadget(GadgetMetadata proto);
}
