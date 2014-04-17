/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.dashboard;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.person.Name;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.LatestActivitiesDTO.InvoicePaymentDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class LatestActivitiesGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(LatestActivitiesGadget.class);

    private final LatestActivitiesView view;

    LatestActivitiesGadget(FinancialDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.billingIcon(), i18n.tr("Latest Payments"), ThemeColor.contrast4, 1);

        view = new LatestActivitiesView();
        view.setViewable(true);
        view.init();

        asWidget().setWidth("100%");
        setContent(view);
    }

    protected void populate(LatestActivitiesDTO value) {
        view.populate(value);
    }

    class LatestActivitiesView extends CEntityForm<LatestActivitiesDTO> {

        public LatestActivitiesView() {
            super(LatestActivitiesDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0, inject(proto().payments(), new InvoiceLineItemFolder()));

            return content;
        }
    }

    private class InvoiceLineItemFolder extends PortalBoxFolder<InvoicePaymentDTO> {

        public InvoiceLineItemFolder() {
            super(InvoicePaymentDTO.class, false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof InvoicePaymentDTO) {
                return new InvoiceLineItemViewer();
            }
            return super.create(member);
        }

        private class InvoiceLineItemViewer extends CEntityForm<InvoicePaymentDTO> {

            public InvoiceLineItemViewer() {
                super(InvoicePaymentDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().id(), new CNumberLabel(), new FieldDecoratorBuilder().build()));
                content.setWidget(++row, 0, inject(proto().amount(), new FieldDecoratorBuilder().build()));
                content.setWidget(++row, 0, inject(proto().convenienceFee(), new FieldDecoratorBuilder().build()));
                content.setWidget(++row, 0, inject(proto().date(), new FieldDecoratorBuilder().build()));
                content.setWidget(++row, 0, inject(proto().status(), new FieldDecoratorBuilder().build()));
                content.setWidget(++row, 0, inject(proto().payer(), new CEntityLabel<Name>(), new FieldDecoratorBuilder().build()));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().convenienceFee()).setVisible(!getValue().convenienceFee().isNull());
            }
        }
    }

}
