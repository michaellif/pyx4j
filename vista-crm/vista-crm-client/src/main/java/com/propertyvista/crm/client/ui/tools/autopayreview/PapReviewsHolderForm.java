/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.autopayreview;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.CheckBox;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewsHolderDTO;

public class PapReviewsHolderForm extends CEntityDecoratableForm<PapReviewsHolderDTO> {

    private final static I18n i18n = I18n.get(PapReviewsHolderForm.class);

    public enum Styles implements IStyleName {

        AutoPayStatsPanel, AutoPayActionsPanel, AutoPayEverythingIsSelected, AutoPaySuperCaptionsPanel, AutoPayCaptionsPanel, AutoPayNoResultsPanel, AutoPayFolderHolder, AutoPayLoadMore

    }

    private FlowPanel statsPanel;

    private HTML counterPanel;

    private FlowPanel tableHeaderPanel;

    private Anchor toggleSelectEverythingAnchor;

    private HTML moreButton;

    private CheckBox checkAllVisibleItems;

    private boolean isSelectAllSet;

    private FlowPanel actionsPanel;

    public PapReviewsHolderForm() {
        super(PapReviewsHolderDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
        panel.add(createStatsPanel());
        panel.add(createActionsPanel());
        panel.add(createTableHeaderPanel());
        panel.add(createPapsFolderPanel());
        return panel;
    }

    public boolean isSelectAllSet() {
        return isSelectAllSet;
    }

    public void onMoreClicked() {

    }

    public void setLoading(boolean isLoading) {
        moreButton.setHTML(isLoading ? i18n.tr("Loading...") : i18n.tr("More..."));
    }

    @Override
    protected PapReviewsHolderDTO preprocessValue(PapReviewsHolderDTO value, boolean fireEvent, boolean populate) {
        if (isSelectAllSet) {
            for (PapReviewDTO papReview : value.papReviews()) {
                papReview.isSelected().setValue(true);
            }
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        checkAllVisibleItems.setValue(false);
        renderStatsPanel();
        moreButton.setVisible(!getValue().isNull() && (getValue().papReviewsTotalCount().getValue() != getValue().papReviews().size()));
        actionsPanel.setVisible(!getValue().isNull() && getValue().papReviewsTotalCount().getValue() != 0);
        tableHeaderPanel.setVisible(!getValue().isNull() && getValue().papReviewsTotalCount().getValue() != 0);
    }

    private FlowPanel createStatsPanel() {
        statsPanel = new FlowPanel();
        statsPanel.addStyleName(Styles.AutoPayStatsPanel.name());

        counterPanel = new HTML();
        statsPanel.add(counterPanel);

        // the caption of this anchor is defined dynamically when the form is populated and based on user's actions 
        toggleSelectEverythingAnchor = new Anchor("", new Command() {
            @Override
            public void execute() {
                toggleSelectAll();
            }
        });
        statsPanel.add(toggleSelectEverythingAnchor);
        return statsPanel;
    }

    private FlowPanel createActionsPanel() {
        actionsPanel = new FlowPanel();
        actionsPanel.setStyleName(Styles.AutoPayActionsPanel.name());

        checkAllVisibleItems = new CheckBox();
        checkAllVisibleItems.setTitle(i18n.tr(i18n.tr("Check/Uncheck all visible items")));
        checkAllVisibleItems.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                checkAll();
            }
        });
        actionsPanel.add(checkAllVisibleItems);

        return actionsPanel;
    }

    private FlowPanel createTableHeaderPanel() {
        tableHeaderPanel = new FlowPanel();

        FlowPanel superCaptionsPanel = new FlowPanel();
        superCaptionsPanel.addStyleName(Styles.AutoPaySuperCaptionsPanel.name());
        superCaptionsPanel.add(new HTML(i18n.tr("Previous")));
        superCaptionsPanel.add(new HTML(i18n.tr("Current")));
        tableHeaderPanel.add(superCaptionsPanel);

        FlowPanel captionsPanel = new FlowPanel();
        captionsPanel.setStylePrimaryName(Styles.AutoPayCaptionsPanel.name());
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Payment")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("% of Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Payment")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("% of Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("% of Change")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        tableHeaderPanel.add(captionsPanel);

        return tableHeaderPanel;
    }

    private FlowPanel createPapsFolderPanel() {
        FlowPanel leasePapsFolderHolder = new FlowPanel();
        leasePapsFolderHolder.setStyleName(Styles.AutoPayFolderHolder.name());

        leasePapsFolderHolder.add(inject(proto().papReviews(), new PapReviewFolder()));

        moreButton = new HTML(i18n.tr("More..."));
        moreButton.setStyleName(Styles.AutoPayLoadMore.name());
        moreButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PapReviewsHolderForm.this.onMoreClicked();
            }
        });
        leasePapsFolderHolder.add(moreButton);

        return leasePapsFolderHolder;
    }

    private void checkAll() {
        checkAll(checkAllVisibleItems.getValue());
        renderStatsPanel();
    }

    private void checkAll(boolean isChecked) {
        CComponent<?> c = get(proto().papReviews());
        PapReviewFolder folder = (PapReviewFolder) c;
        folder.checkAll(isChecked);
    }

    private void toggleSelectAll() {
        isSelectAllSet = !isSelectAllSet;

        checkAllVisibleItems.setValue(isSelectAllSet);
        checkAllVisibleItems.setEditable(!isSelectAllSet);
        setEditable(!isSelectAllSet);
        checkAll(isSelectAllSet);

        renderStatsPanel();
    }

    private void renderStatsPanel() {
        if (getValue().isNull()) {
            statsPanel.setVisible(false);
        } else {
            statsPanel.setVisible(true);
            statsPanel.setStyleName(Styles.AutoPayEverythingIsSelected.name(), isSelectAllSet);
            if (getValue().papReviewsTotalCount().getValue() > 0) {
                if (!isSelectAllSet) {
                    if (checkAllVisibleItems.getValue() == true) {
                        counterPanel.setText(i18n.tr("All {0,number,#,##0} AutoPays on this page are selected.", getValue().papReviews().size(), getValue()
                                .papReviewsTotalCount().getValue()));
                        if (getValue().papReviewsTotalCount().getValue() != getValue().papReviews().size()) {
                            toggleSelectEverythingAnchor.setVisible(true);
                            toggleSelectEverythingAnchor.setText(i18n.tr("Select all {0,number,#,##0} AutoPays for review", getValue().papReviewsTotalCount()
                                    .getValue()));
                        }
                    } else {
                        counterPanel.setText(i18n.tr("Displaying {0,number,#,##0} of {1,number,#,##0} AutoPays for review", getValue().papReviews().size(),
                                getValue().papReviewsTotalCount().getValue()));
                        toggleSelectEverythingAnchor.setVisible(false);
                        toggleSelectEverythingAnchor.setText("");
                    }
                } else {
                    toggleSelectEverythingAnchor.setVisible(true);
                    toggleSelectEverythingAnchor.setText(i18n.tr("Clear selection"));
                    counterPanel.setText(i18n.tr("All {0,number,#,##0} AutoPays for review are selected.", getValue().papReviewsTotalCount().getValue()));
                }
            } else {
                counterPanel.setText(i18n.tr("No AutoPays for review have been found"));
            }
        }
    }

}