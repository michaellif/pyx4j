/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 15, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.misc;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.VistaTheme;

public class PapExpirationWarning {

    static final I18n i18n = I18n.get(PapExpirationWarning.class);

    private final TwoColumnFlexFormPanel expirationWarningPanel = new TwoColumnFlexFormPanel();

    private final HTML expirationWarningLabel = new HTML();

    private final String expirationWarningText = i18n.tr("This Pre-Authorized Payment is expiring on {0} - needs to be replaced with new one!");

    public PapExpirationWarning() {

        expirationWarningLabel.setStyleName(VistaTheme.StyleName.warningMessage.name());
        expirationWarningPanel.setWidget(0, 0, 2, expirationWarningLabel);
        expirationWarningPanel.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        expirationWarningPanel.setHR(1, 0, 2);
        expirationWarningPanel.setBR(2, 0, 2);
    }

    public TwoColumnFlexFormPanel getExpirationWarningPanel() {
        return expirationWarningPanel;
    }

    public void prepareView(IPrimitive<LogicalDate> expiring) {
        expirationWarningLabel.setHTML(SimpleMessageFormat.format(expirationWarningText, expiring.getStringView()));
        expirationWarningPanel.setVisible(!expiring.isNull());
    }
}
