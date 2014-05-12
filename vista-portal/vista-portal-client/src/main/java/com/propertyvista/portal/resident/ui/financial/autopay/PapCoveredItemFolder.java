/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-16
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.autopay;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class PapCoveredItemFolder extends PortalBoxFolder<AutopayAgreement.AutopayAgreementCoveredItem> {

    private static final I18n i18n = I18n.get(PapCoveredItemFolder.class);

    public PapCoveredItemFolder() {
        this(false);
    }

    public PapCoveredItemFolder(boolean editable) {
        super(AutopayAgreement.AutopayAgreementCoveredItem.class, editable);
    }

    @Override
    protected CForm<AutopayAgreementCoveredItem> createItemForm(IObject<?> member) {
        return new CoveredItemViewer();
    }

    class CoveredItemViewer extends CForm<AutopayAgreementCoveredItem> {

        public CoveredItemViewer() {
            super(AutopayAgreementCoveredItem.class);

            setViewable(true);
            inheritViewable(false);
        }

        @Override
        protected IsWidget createContent() {
            PortalFormPanel formPanel = new PortalFormPanel(this);

            formPanel.append(Location.Left, proto().billableItem(), new PapBillableItemLabel()).decorate().componentWidth(200)
                    .customLabel(i18n.tr("Lease Charge"));
            formPanel.append(Location.Left, proto().billableItem().agreedPrice(), new CMoneyLabel()).decorate().componentWidth(100)
                    .customLabel(i18n.tr("Price"));
            formPanel.append(Location.Left, proto().amount(), new CMoneyLabel()).decorate().componentWidth(100).customLabel(i18n.tr("Payment"));

            get(proto().amount()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);

            return formPanel;
        }
    }
}