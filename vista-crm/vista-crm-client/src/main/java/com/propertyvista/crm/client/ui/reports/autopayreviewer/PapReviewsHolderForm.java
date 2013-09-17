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
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

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
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewsHolderDTO;

public class PapReviewsHolderForm extends CEntityDecoratableForm<PapReviewsHolderDTO> {

    private final static I18n i18n = I18n.get(PapReviewsHolderForm.class);

    public enum Styles implements IStyleName {

        AutoPayStatsPanel, AutoPayActionsPanel, AutoPayEverythingIsSelected, AutoPaySuperCaptionsPanel, AutoPayCaptionsPanel, AutoPayFolderHolder, AutoPayLoadMore

    }

    private HTML counterPanel;

    private HTML moreButton;

    private Anchor toggleSelectEverythingAnchor;

    private boolean isEverythingSelected;

    private CheckBox checkVisibleItems;

    private FlowPanel statsPanel;

    public PapReviewsHolderForm() {
        super(PapReviewsHolderDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();

        panel.add(createStatsPanel());
        panel.add(createActionsPanel());
        panel.add(createSuperCaptionsPanel());
        panel.add(createCaptionsPanel());

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

        panel.add(leasePapsFolderHolder);

        return panel;
    }

    public boolean isEverythingSelected() {
        return isEverythingSelected;
    }

    public void onMoreClicked() {

    }

    public void setLoading(boolean isLoading) {
        moreButton.setHTML(isLoading ? i18n.tr("Loading...") : i18n.tr("More..."));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        isEverythingSelected = false;
        redrawStats();
    }

    private void checkAll(boolean isChecked) {
        CComponent<?> c = get(proto().papReviews());
        PapReviewFolder folder = (PapReviewFolder) c;
        folder.checkAll(isChecked);
    }

    private FlowPanel createStatsPanel() {
        statsPanel = new FlowPanel();
        statsPanel.addStyleName(Styles.AutoPayStatsPanel.name());

        counterPanel = new HTML();
        statsPanel.add(counterPanel);

        toggleSelectEverythingAnchor = new Anchor(i18n.tr("Select Everything"), new Command() {
            @Override
            public void execute() {
                toggleSelectEverything();
            }
        });
        statsPanel.add(toggleSelectEverythingAnchor);
        return statsPanel;
    }

    private FlowPanel createActionsPanel() {
        FlowPanel actionsPanel = new FlowPanel();
        actionsPanel.setStyleName(Styles.AutoPayActionsPanel.name());

        checkVisibleItems = new CheckBox();
        checkVisibleItems.setTitle(i18n.tr(i18n.tr("Check/Uncheck all visible items")));
        checkVisibleItems.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                checkAll(checkVisibleItems.getValue());
            }
        });
        actionsPanel.add(checkVisibleItems);
        return actionsPanel;
    }

    private FlowPanel createSuperCaptionsPanel() {
        FlowPanel superCaptionsPanel = new FlowPanel();
        superCaptionsPanel.addStyleName(Styles.AutoPaySuperCaptionsPanel.name());
        superCaptionsPanel.add(new HTML(i18n.tr("Suspended")));
        superCaptionsPanel.add(new HTML(i18n.tr("Suggested")));
        return superCaptionsPanel;
    }

    private FlowPanel createCaptionsPanel() {
        FlowPanel panel = new FlowPanel();
        panel.setStylePrimaryName(Styles.AutoPayCaptionsPanel.name());
        panel.add(new MiniDecorator(new HTML(i18n.tr("Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("Payment")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("% of Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("Payment")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("% of Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("% of Change")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        return panel;
    }

    private void toggleSelectEverything() {
        isEverythingSelected = !isEverythingSelected;
        checkAll(isEverythingSelected);
        redrawStats();
    }

    private void redrawStats() {
        setEditable(!isEverythingSelected);
        checkVisibleItems.setValue(isEverythingSelected);
        checkVisibleItems.setEditable(!isEverythingSelected);

        statsPanel.setStyleName(Styles.AutoPayEverythingIsSelected.name(), isEverythingSelected);
        if (!isEverythingSelected) {

            toggleSelectEverythingAnchor.setText(i18n.tr("Select Everything"));
            counterPanel.setText(i18n.tr("Displaying {0,number,#,##0} of {1,number,#,##0} Leases with suspended AutoPay", getValue().papReviews().size(),
                    getValue().papReviewsTotalCount().getValue()));
            moreButton.setVisible(getValue().papReviewsTotalCount().getValue() != getValue().papReviews().size());
        } else {
            toggleSelectEverythingAnchor.setText(i18n.tr("Unselect Everything"));
            counterPanel.setText(i18n.tr("All {0,number,#,##0} suspended AutoPays are selected", getValue().papReviewsTotalCount().getValue()));
            moreButton.setVisible(false);
        }
    }
}