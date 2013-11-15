/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.dashboard;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.resident.dto.insurance.status.InsuranceStatusDTO;

public abstract class InsuranceToolbar extends Toolbar {

    static final I18n i18n = I18n.get(InsuranceToolbar.class);

    private final Button purchaseButton;

    private final Button proofButton;

    public InsuranceToolbar() {

        purchaseButton = new Button(i18n.tr("Purchase Insurance"), new Command() {

            @Override
            public void execute() {
                onPurchaseClicked();
            }
        });
        purchaseButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
        addItem(purchaseButton);

        proofButton = new Button("", new Command() {

            @Override
            public void execute() {
                onProofClicked();
            }
        });
        proofButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 0.8));
        addItem(proofButton);

        recalculateState(null);
    }

    protected abstract void onPurchaseClicked();

    protected abstract void onProofClicked();

    public void recalculateState(InsuranceStatusDTO insuranceStatus) {

        if (insuranceStatus == null) {
            purchaseButton.setVisible(false);
            proofButton.setVisible(false);
        } else {
            switch (insuranceStatus.status().getValue()) {
            case noInsurance:
                proofButton.setCaption(i18n.tr("Provide Proof of my Insurance"));
                purchaseButton.setVisible(true);
                proofButton.setVisible(true);
                break;
            case hasOtherInsurance:
                proofButton.setCaption(i18n.tr("Update Proof of my Insurance"));
                purchaseButton.setVisible(true);
                proofButton.setVisible(true);
                break;
            case hasTenantSure:
                purchaseButton.setVisible(false);
                proofButton.setVisible(false);
                break;
            }
        }
    }
}