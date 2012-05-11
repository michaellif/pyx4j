/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.insurancemockup.forms;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.client.ui.residents.insurancemockup.components.InsuranceMessagePanel;

public class TenantSureInsuranceForm implements IsWidget {

    private final static I18n i18n = I18n.get(TenantSureInsuranceForm.class);

    private final FormFlexPanel panel = new FormFlexPanel();

    public TenantSureInsuranceForm() {
        int row = -1;
        panel.setCellPadding(10);
        panel.setCellSpacing(10);
        panel.setWidget(++row, 0, new InsuranceMessagePanel(new HTML("TenantSure is a Licensed Broker. Below please find your TenantSure insurance details. "
                + "If you have any claims, you can reach TenanSure's claim department at 1-888-1234-444")));

        panel.getFlexCellFormatter().setColSpan(row, 0, 3);
        panel.setWidget(1, 0, new StrangeWidget(i18n.tr("Insurance Sertificate")));
        panel.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
        panel.setWidget(1, 1, new StrangeWidget(i18n.tr("Insurance Policy")));
        panel.getFlexCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
        panel.setWidget(1, 2, new StrangeWidget(i18n.tr("FAQ")));
        panel.getFlexCellFormatter().setHorizontalAlignment(1, 2, HasHorizontalAlignment.ALIGN_CENTER);

        panel.setWidget(2, 0, new StrangeWidget(i18n.tr("Make a Claim")));
        panel.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
        panel.setWidget(2, 1, new StrangeWidget(i18n.tr("Total $ Content Insurance")));
        panel.getFlexCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER);
        panel.setWidget(2, 2, new StrangeWidget(i18n.tr("Change Payment Method")));
        panel.getFlexCellFormatter().setHorizontalAlignment(2, 2, HasHorizontalAlignment.ALIGN_CENTER);

    }

    @Override
    public Widget asWidget() {

        return panel;
    }

    public static class StrangeWidget implements IsWidget {

        private final VerticalPanel strangeWidget;

        public StrangeWidget(String label) {
            strangeWidget = new VerticalPanel();
            strangeWidget.getElement().getStyle().setProperty("margin", "5px");

            Widget button = new HTML("&nbsp");
            button.getElement().getStyle().setProperty("borderStyle", "outset");
            button.getElement().getStyle().setProperty("borderWidth", "1px");
            button.getElement().getStyle().setProperty("borderWidth", "1px");
            button.getElement().getStyle().setProperty("borderRadius", "1em");
            button.getElement().getStyle().setProperty("width", "30px");
            button.getElement().getStyle().setProperty("height", "30px");
            button.getElement().getStyle().setProperty("margin", "auto");

            strangeWidget.add(button);
            HTML labelHolder = new HTML(label);
            strangeWidget.add(labelHolder);

//            HTML daysLeftCountdown = new HTML(//@formatter:off
//                    "<div style='color:#E6E6E6;float:left;'>"
//                            + " <div style='border-style:outset;border-width:1px;border-radius:0.5em;text-align:center;font-size:1.5em;font-weight:bold;width:2.5em;line-height:2.5em;margin:auto;vertical-align:center'>15</div>"
//                            + " <div style='margin:auto;text-align:center;'>Days Until Move-In Date</div>"
//                            + "</div>");//@formatter:on
//
//            daysLeftCountdown.getElement().getStyle().setPosition(Position.ABSOLUTE);
//            daysLeftCountdown.getElement().getStyle().setTop(30, Unit.PX);
//            daysLeftCountdown.getElement().getStyle().setLeft(10, Unit.PX);

        }

        @Override
        public Widget asWidget() {
            return strangeWidget;
        }

    }

}
