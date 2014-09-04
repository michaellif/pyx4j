/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.portal.prospect.themes.RentalSummaryTheme;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;

public class RentChargesSummaryGadget extends FlowPanel {

    private static final I18n i18n = I18n.get(RentChargesSummaryGadget.class);

    private final InfoSection monthlySection;

    private final InfoSection depositSection;

    private final InfoSection feeSection;

    public RentChargesSummaryGadget() {
        super();

        FlowPanel panel = new FlowPanel();

        monthlySection = new InfoSection(i18n.tr("Monthly Charges"));
        panel.add(monthlySection);

        depositSection = new InfoSection(i18n.tr("Deposit Charges"));
        depositSection.setVisible(false);
        panel.add(depositSection);

        feeSection = new InfoSection(i18n.tr("Fees"));
        feeSection.setVisible(false);
        panel.add(feeSection);

        add(panel);
    }

    public void populate(OnlineApplicationDTO onlineApplication) {
        updateMonthly(onlineApplication);
        updateDeposits(onlineApplication);
        updateFees(onlineApplication);
    }

    private void updateMonthly(OnlineApplicationDTO onlineApplication) {
        StringBuilder contentBuilder = new StringBuilder();

        if (onlineApplication != null && !onlineApplication.leaseChargesData().selectedService().isNull()) {
            contentBuilder.append(formatCharge(onlineApplication.leaseChargesData().selectedService().agreedPrice().getValue(BigDecimal.ZERO),
                    onlineApplication.leaseChargesData().selectedService().item().name().getStringView()));

            for (BillableItem billableItem : onlineApplication.leaseChargesData().selectedFeatures()) {
                contentBuilder.append(formatCharge(billableItem.agreedPrice().getValue(BigDecimal.ZERO), billableItem.item().name().getStringView()));
            }

            if (onlineApplication.leaseChargesData().totalMonthlyCharge().getValue().compareTo(BigDecimal.ZERO) > 0) {
                contentBuilder.append(formatCharge(onlineApplication.leaseChargesData().totalMonthlyCharge().getValue(), onlineApplication.leaseChargesData()
                        .totalMonthlyCharge().getMeta().getCaption(), true));
            }
        }

        monthlySection.setContentHTML(contentBuilder.length() > 0 ? contentBuilder.toString() : "&nbsp;");
        monthlySection.setVisible(contentBuilder.length() > 0);
    }

    private void updateDeposits(OnlineApplicationDTO onlineApplication) {
        StringBuilder contentBuilder = new StringBuilder();

        if (onlineApplication != null && !onlineApplication.leaseChargesData().selectedService().isNull()) {
            for (Deposit d : onlineApplication.leaseChargesData().deposits()) {
                contentBuilder.append(formatCharge(d.amount().getValue(), (d.description().isNull() ? d.type().getStringView() : d.description()
                        .getStringView())));
            }

            if (onlineApplication.leaseChargesData().totalDeposits().getValue().compareTo(BigDecimal.ZERO) > 0) {
                contentBuilder.append(formatCharge(onlineApplication.leaseChargesData().totalDeposits().getValue(), onlineApplication.leaseChargesData()
                        .totalDeposits().getMeta().getCaption(), true));
            }
        }

        depositSection.setContentHTML(contentBuilder.toString());
        depositSection.setVisible(contentBuilder.length() > 0);
    }

    private void updateFees(OnlineApplicationDTO onlineApplication) {
        if (onlineApplication != null && !onlineApplication.payment().isNull()
                && onlineApplication.payment().applicationFee().getValue(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0) {
            feeSection.setContentHTML(formatCharge(onlineApplication.payment().applicationFee().getValue(), i18n.tr("Application Fee")));
            feeSection.setVisible(true);
        } else {
            feeSection.setVisible(false);
        }
    }

    private String formatCharge(BigDecimal amount, String title) {
        return formatCharge(amount, title, false);
    }

    private String formatCharge(BigDecimal amount, String title, boolean bold) {
        return (bold ? "<i>" : "") + title + "&nbsp;-&nbsp;$" + amount + (bold ? "</i>" : "") + "</br>";
    }

    class InfoSection extends FlowPanel {
        private final Label caption;

        private final HTML content;

        public InfoSection(String title) {
            caption = new Label(title);
            caption.setStyleName(RentalSummaryTheme.StyleName.RentalSummaryCaption.name());
            add(caption);

            content = new HTML();
            content.setStyleName(RentalSummaryTheme.StyleName.RentalSummaryBlock.name());
            add(content);
        }

        public void setContentHTML(String html) {
            content.setHTML(html);
        }
    }
}
