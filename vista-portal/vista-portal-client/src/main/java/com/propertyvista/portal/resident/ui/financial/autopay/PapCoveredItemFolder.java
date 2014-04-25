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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
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
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0,
                    inject(proto().billableItem(), new PapBillableItemLabel(), new FieldDecoratorBuilder(200).customLabel(i18n.tr("Lease Charge")).build()));
            content.setWidget(++row, 0,
                    inject(proto().billableItem().agreedPrice(), new CMoneyLabel(), new FieldDecoratorBuilder(100).customLabel(i18n.tr("Price")).build()));
            content.setWidget(++row, 0, inject(proto().amount(), new CMoneyLabel(), new FieldDecoratorBuilder(100).customLabel(i18n.tr("Payment")).build()));

            get(proto().amount()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);

            return content;
        }
    }
}