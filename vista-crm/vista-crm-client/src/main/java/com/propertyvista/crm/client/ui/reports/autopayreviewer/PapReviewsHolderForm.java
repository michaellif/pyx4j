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

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewsHolderDTO;

public class PapReviewsHolderForm extends CEntityDecoratableForm<PapReviewsHolderDTO> {

    private final static I18n i18n = I18n.get(PapReviewsHolderForm.class);

    public enum Styles implements IStyleName {

        AutoPayCounterPanel, AutoPaySuperCaptionsPanel, AutoPayCaptionsPanel, AutoPayLoadMore

    }

    private HTML counterPanel;

    private HTML moreButton;

    public PapReviewsHolderForm() {
        super(PapReviewsHolderDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();

        counterPanel = new HTML();
        counterPanel.addStyleName(Styles.AutoPayCounterPanel.name());
        panel.add(counterPanel);

        FlowPanel controlsPanel = new FlowPanel();
        controlsPanel.add(new Button(i18n.tr("Mark All"), new Command() {
            @Override
            public void execute() {
                markAll();
            }
        }));
        panel.add(controlsPanel);

        panel.add(createSuperCaptionsPanel());
        panel.add(createCaptionsPanel());

        FlowPanel leasePapsFolderHolder = new FlowPanel();
        leasePapsFolderHolder.getElement().getStyle().setOverflow(Overflow.AUTO);
        leasePapsFolderHolder.getElement().getStyle().setPosition(Position.ABSOLUTE);
        leasePapsFolderHolder.getElement().getStyle().setTop(100, Unit.PX);
        leasePapsFolderHolder.getElement().getStyle().setBottom(0, Unit.PX);
        leasePapsFolderHolder.getElement().getStyle().setLeft(0, Unit.PX);
        leasePapsFolderHolder.getElement().getStyle().setRight(0, Unit.PX);
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

    public void onMoreClicked() {

    }

    public void setLoading(boolean isLoading) {
        moreButton.setHTML(isLoading ? i18n.tr("Loading...") : i18n.tr("More..."));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        counterPanel.setText(i18n.tr("Displaying {0,number,#,##0} of {1,number,#,##0} Leases with suspended AutoPay", getValue().papReviews().size(),
                getValue().papReviewsTotalCount().getValue()));
        moreButton.setVisible(getValue().papReviewsTotalCount().getValue() != getValue().papReviews().size());
    }

    private void markAll() {
        CComponent<?> c = get(proto().papReviews());
        PapReviewFolder folder = (PapReviewFolder) c;
        folder.selectAll();
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

        panel.add(new MiniDecorator(new HTML("&nbsp;"), PapReviewFolder.Styles.AutoPayPapChargeNameColumn.name()));
        panel.add(new MiniDecorator(new HTML("&nbsp;"), PapReviewFolder.Styles.AutoPayPapChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("Charge")), PapReviewFolder.Styles.AutoPayPapChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("Payment")), PapReviewFolder.Styles.AutoPayPapChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("% of Charge")), PapReviewFolder.Styles.AutoPayPapChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("Charge")), PapReviewFolder.Styles.AutoPayPapChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("Payment")), PapReviewFolder.Styles.AutoPayPapChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("% of Charge")), PapReviewFolder.Styles.AutoPayPapChargeNumberColumn.name()));
        panel.add(new MiniDecorator(new HTML(i18n.tr("% of Change")), PapReviewFolder.Styles.AutoPayPapChargeNumberColumn.name()));
        return panel;
    }
}