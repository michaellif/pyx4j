/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;

public class OnlinePaymentPricingTab extends Composite {

    private static final I18n i18n = I18n.get(OnlinePaymentPricingTab.class);

    private final TwoColumnFlexFormPanel contentPanel;

    public OnlinePaymentPricingTab() {
        int row = -1;
        contentPanel = new TwoColumnFlexFormPanel();
        contentPanel.setWidth("500px");

        contentPanel.setWidget(++row, 0, makePoweredByPanel());
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        contentPanel.setWidget(++row, 0, makePriceListPanel());
        // TODO add line
        Label setUpFeeExplanation = new Label();
        setUpFeeExplanation.setText(OnlinePaymentWizardResources.INSTANCE.setUpFeePricingExplanation().getText());
        contentPanel.setWidget(++row, 0, setUpFeeExplanation);

        // TODO add line
        Label otherText = new Label();
        otherText.setHTML(OnlinePaymentWizardResources.INSTANCE.marketingText().getText());

        contentPanel.setWidget(+row, 0, otherText);

        TwoColumnFlexFormPanel containerPanel = new TwoColumnFlexFormPanel();
        containerPanel.setWidget(0, 0, contentPanel);
        containerPanel.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        initWidget(containerPanel);
    }

    public void setPaymentFees(AbstractPaymentFees paymentFees) {
        // TODO implement propagation of payment fees   
    }

    private Widget makePriceListPanel() {
        TwoColumnFlexFormPanel priceListPanel = new TwoColumnFlexFormPanel();
        int row = -1;
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentWizardResources.INSTANCE.echequeLogo()), "$1.50", i18n.tr(" per transaction"));
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentWizardResources.INSTANCE.interacLogo()), "$1.50", i18n.tr(" per transaction"));
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentWizardResources.INSTANCE.directBankingLogo()), "$1.50",
                i18n.tr(" per transaction"));
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentWizardResources.INSTANCE.visaDebitLogo()), "0.75%",
                i18n.tr(" of amount processed"));

        // TODO add line
        Label startingFromLabel = new Label();
        startingFromLabel.setText(i18n.tr("Starting From:"));
        priceListPanel.setWidget(++row, 0, startingFromLabel);
        priceListPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentWizardResources.INSTANCE.visaLogo()), "1.50%", i18n.tr("of amount processed"));
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentWizardResources.INSTANCE.masterCardLogo()), "2.20%",
                i18n.tr("of amount processed"));

        return priceListPanel;

    }

    private Widget makePaymentMethodPriceRow(TwoColumnFlexFormPanel panel, int row, Image paymentMethodLogo, String price, String unit) {
        panel.setWidget(row, 0, paymentMethodLogo);
        Label priceLabel = new Label();
        priceLabel.setText(price);
        panel.setWidget(row, 1, priceLabel);
        Label unitLabel = new Label();
        unitLabel.setText(unit);
        panel.setWidget(row, 2, unitLabel);
        return panel;
    }

    private Widget makePoweredByPanel() {
        LayoutPanel poweredByPanel = new LayoutPanel();
        int width = 500;
        int height = 250;
        poweredByPanel.setSize(500 + "px", height + "px");

        Label poweredByCaption = new Label();
        poweredByCaption.setText(i18n.tr("Powered By"));
        poweredByCaption.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        poweredByCaption.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        poweredByCaption.getElement().getStyle().setFontSize(15, Unit.PX);
        poweredByPanel.add(poweredByCaption);

        poweredByPanel.setWidgetLeftWidth(poweredByCaption, 0, Unit.PCT, 100, Unit.PCT);
        poweredByPanel.setWidgetTopHeight(poweredByCaption, 0, Unit.PX, 20, Unit.PX);

        int firstRowYStart = 30;
        Image caledonLogo = new Image(OnlinePaymentWizardResources.INSTANCE.caledonLogo());
        poweredByPanel.add(caledonLogo);
        poweredByPanel.setWidgetLeftWidth(caledonLogo, 100, Unit.PX, caledonLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(caledonLogo, firstRowYStart, Unit.PX, caledonLogo.getHeight(), Unit.PX);

        Image paypadLogo = new Image(OnlinePaymentWizardResources.INSTANCE.paymentPadLogo());
        poweredByPanel.add(paypadLogo);
        poweredByPanel.setWidgetRightWidth(paypadLogo, 100, Unit.PX, paypadLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(paypadLogo, firstRowYStart, Unit.PX, paypadLogo.getHeight(), Unit.PX);

        // TODO add line
        int secondRowYStart = 100;
        Image visaLogo = new Image(OnlinePaymentWizardResources.INSTANCE.visaLogo());
        poweredByPanel.add(visaLogo);
        poweredByPanel.setWidgetLeftWidth(visaLogo, 30, Unit.PX, visaLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(visaLogo, secondRowYStart, Unit.PX, visaLogo.getHeight(), Unit.PX);

        Image debitLogo = new Image(OnlinePaymentWizardResources.INSTANCE.debitLogo());
        poweredByPanel.add(debitLogo);
        poweredByPanel.setWidgetLeftWidth(debitLogo, width / 2 - debitLogo.getWidth() / 2, Unit.PX, debitLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(debitLogo, secondRowYStart, Unit.PX, debitLogo.getHeight(), Unit.PX);

        Image masterCardLogo = new Image(OnlinePaymentWizardResources.INSTANCE.masterCardLogo());
        poweredByPanel.add(masterCardLogo);
        poweredByPanel.setWidgetRightWidth(masterCardLogo, 30, Unit.PX, masterCardLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(masterCardLogo, secondRowYStart, Unit.PX, masterCardLogo.getHeight(), Unit.PX);

        int thirdRowYStart = 180;
        Image echequeLogo = new Image(OnlinePaymentWizardResources.INSTANCE.echequeLogo());
        poweredByPanel.add(echequeLogo);
        poweredByPanel.setWidgetLeftWidth(echequeLogo, 100, Unit.PX, echequeLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(echequeLogo, thirdRowYStart, Unit.PX, echequeLogo.getHeight(), Unit.PX);

        Image directBankingLogo = new Image(OnlinePaymentWizardResources.INSTANCE.directBankingLogo());
        poweredByPanel.add(directBankingLogo);
        poweredByPanel.setWidgetRightWidth(directBankingLogo, 100, Unit.PX, directBankingLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(directBankingLogo, thirdRowYStart, Unit.PX, directBankingLogo.getHeight(), Unit.PX);

        // TODO add line
        return poweredByPanel;
    }
}
