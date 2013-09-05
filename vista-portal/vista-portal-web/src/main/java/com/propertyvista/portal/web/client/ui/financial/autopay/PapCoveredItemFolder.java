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
package com.propertyvista.portal.web.client.ui.financial.autopay;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class PapCoveredItemFolder extends VistaBoxFolder<PreauthorizedPayment.PreauthorizedPaymentCoveredItem> {

    private static final I18n i18n = I18n.get(PapCoveredItemFolder.class);

    public PapCoveredItemFolder() {
        this(false);
    }

    public PapCoveredItemFolder(boolean editable) {
        super(PreauthorizedPayment.PreauthorizedPaymentCoveredItem.class, editable);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PreauthorizedPaymentCoveredItem) {
            return new CoveredItemViewer();
        }
        return super.create(member);
    }

    class CoveredItemViewer extends CEntityForm<PreauthorizedPaymentCoveredItem> {

        public CoveredItemViewer() {
            super(PreauthorizedPaymentCoveredItem.class);

            setViewable(true);
            inheritViewable(false);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel(i18n.tr("Details"));
            int row = -1;

            content.setWidget(++row, 0,
                    new FormDecoratorBuilder(inject(proto().billableItem(), new PapBillableItemLabel()), 200).customLabel(i18n.tr("Lease Charge")).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billableItem().agreedPrice()), 100).customLabel(i18n.tr("Price")).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amount()), 100).customLabel(i18n.tr("Payment")).build());

            get(proto().amount()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);

            return content;
        }
    }
}