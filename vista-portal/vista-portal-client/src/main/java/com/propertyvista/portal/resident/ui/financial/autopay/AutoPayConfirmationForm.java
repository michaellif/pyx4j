/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 */
package com.propertyvista.portal.resident.ui.financial.autopay;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.CCurrencyMoneyLabel;

public class AutoPayConfirmationForm extends CPortalEntityForm<AutoPayDTO> {

    private static final I18n i18n = I18n.get(AutoPayConfirmationForm.class);

    public AutoPayConfirmationForm() {
        super(AutoPayDTO.class, null, i18n.tr("Your Auto Pay has been successfully setup and will be processed automatically every month."), new Button(
                i18n.tr("Continue"), new Command() {

                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
                    }
                }), ThemeColor.contrast4);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()).decorate();
        formPanel.append(Location.Left, proto().coveredItems(), new PapCoveredItemFolder());
        formPanel.append(Location.Left, proto().total(), new CCurrencyMoneyLabel(i18n.tr("CAD $"))).decorate();
        formPanel.br();
        formPanel.append(Location.Left, proto().nextPaymentDate(), new CDateLabel()).decorate().customLabel(i18n.tr("Your first payment will be processed on"))
                .componentWidth(100);

        return formPanel;
    }
}
