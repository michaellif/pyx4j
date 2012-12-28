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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

public class OnlinePaymentPricingTab extends Composite {

    private static final I18n i18n = I18n.get(OnlinePaymentPricingTab.class);

    private final FormFlexPanel contentPanel;

    public OnlinePaymentPricingTab() {
        int row = -1;
        contentPanel = new FormFlexPanel();
        contentPanel.setWidth("500px");

        contentPanel.setH1(++row, 0, 1, i18n.tr("Pricing Information For Online Payments"));

        contentPanel.setWidget(++row, 0, makePoweredByPanel());
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        contentPanel.setWidget(++row, 0, makePriceListPanel());
        // TODO add line
        Label setUpFeeExplanation = new Label();
        setUpFeeExplanation.setText(OnlinePaymentPricingResources.INSTANCE.setUpFeePricing().getText());
        contentPanel.setWidget(++row, 0, setUpFeeExplanation);

        // TODO add line
        Label otherText = new Label();
        otherText.setHTML(OnlinePaymentPricingResources.INSTANCE.otherText().getText());

        contentPanel.setWidget(+row, 0, otherText);

        FormFlexPanel containerPanel = new FormFlexPanel();
        containerPanel.setWidget(0, 0, contentPanel);
        containerPanel.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        initWidget(containerPanel);
    }

    private Widget makePriceListPanel() {
        FormFlexPanel priceListPanel = new FormFlexPanel();
        int row = -1;
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentPricingResources.INSTANCE.echequeLogo()), "$1.50", i18n.tr(" per transaction"));
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentPricingResources.INSTANCE.interacLogo()), "$1.50", i18n.tr(" per transaction"));
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentPricingResources.INSTANCE.directBankingLogo()), "$1.50",
                i18n.tr(" per transaction"));
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentPricingResources.INSTANCE.visaDebitLogo()), "0.75%",
                i18n.tr(" of amount processed"));

        // TODO add line
        Label startingFromLabel = new Label();
        startingFromLabel.setText(i18n.tr("Starting From:"));
        priceListPanel.setWidget(++row, 0, startingFromLabel);
        priceListPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentPricingResources.INSTANCE.visaLogo()), "1.50%", i18n.tr("of amount processed"));
        makePaymentMethodPriceRow(priceListPanel, ++row, new Image(OnlinePaymentPricingResources.INSTANCE.masterCardLogo()), "2.20%",
                i18n.tr("of amount processed"));

        return priceListPanel;

    }

    private Widget makePaymentMethodPriceRow(FormFlexPanel panel, int row, Image paymentMethodLogo, String price, String unit) {
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
        Image caledonLogo = new Image(OnlinePaymentPricingResources.INSTANCE.caledonLogo());
        poweredByPanel.add(caledonLogo);
        poweredByPanel.setWidgetLeftWidth(caledonLogo, 100, Unit.PX, caledonLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(caledonLogo, firstRowYStart, Unit.PX, caledonLogo.getHeight(), Unit.PX);

        Image paypadLogo = new Image(OnlinePaymentPricingResources.INSTANCE.paymentPadLogo());
        poweredByPanel.add(paypadLogo);
        poweredByPanel.setWidgetRightWidth(paypadLogo, 100, Unit.PX, paypadLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(paypadLogo, firstRowYStart, Unit.PX, paypadLogo.getHeight(), Unit.PX);

        // TODO add line
        int secondRowYStart = 100;
        Image visaLogo = new Image(OnlinePaymentPricingResources.INSTANCE.visaLogo());
        poweredByPanel.add(visaLogo);
        poweredByPanel.setWidgetLeftWidth(visaLogo, 30, Unit.PX, visaLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(visaLogo, secondRowYStart, Unit.PX, visaLogo.getHeight(), Unit.PX);

        Image debitLogo = new Image(OnlinePaymentPricingResources.INSTANCE.debitLogo());
        poweredByPanel.add(debitLogo);
        poweredByPanel.setWidgetLeftWidth(debitLogo, width / 2 - debitLogo.getWidth() / 2, Unit.PX, debitLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(debitLogo, secondRowYStart, Unit.PX, debitLogo.getHeight(), Unit.PX);

        Image masterCardLogo = new Image(OnlinePaymentPricingResources.INSTANCE.masterCardLogo());
        poweredByPanel.add(masterCardLogo);
        poweredByPanel.setWidgetRightWidth(masterCardLogo, 30, Unit.PX, masterCardLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(masterCardLogo, secondRowYStart, Unit.PX, masterCardLogo.getHeight(), Unit.PX);

        int thirdRowYStart = 180;
        Image echequeLogo = new Image(OnlinePaymentPricingResources.INSTANCE.echequeLogo());
        poweredByPanel.add(echequeLogo);
        poweredByPanel.setWidgetLeftWidth(echequeLogo, 100, Unit.PX, echequeLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(echequeLogo, thirdRowYStart, Unit.PX, echequeLogo.getHeight(), Unit.PX);

        Image directBankingLogo = new Image(OnlinePaymentPricingResources.INSTANCE.directBankingLogo());
        poweredByPanel.add(directBankingLogo);
        poweredByPanel.setWidgetRightWidth(directBankingLogo, 100, Unit.PX, directBankingLogo.getWidth(), Unit.PX);
        poweredByPanel.setWidgetTopHeight(directBankingLogo, thirdRowYStart, Unit.PX, directBankingLogo.getHeight(), Unit.PX);

        // TODO add line
        return poweredByPanel;
    }
}
