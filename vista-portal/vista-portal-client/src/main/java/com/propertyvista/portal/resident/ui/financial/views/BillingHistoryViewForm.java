/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.views;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillDataDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.BillingHistoryDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class BillingHistoryViewForm extends CPortalEntityForm<BillingHistoryDTO> {

    private static final I18n i18n = I18n.get(BillingHistoryViewForm.class);

    public BillingHistoryViewForm(BillingHistoryView view) {
        super(BillingHistoryDTO.class, view, i18n.tr("Billing History"), ThemeColor.contrast4);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().bills(), new BillDataFolder()));

        return content;
    }

    class BillDataFolder extends PortalBoxFolder<BillDataDTO> {

        public BillDataFolder() {
            super(BillDataDTO.class, false);
        }

        @Override
        protected CForm<BillDataDTO> createItemForm(IObject<?> member) {
            return new BillDataViewer();
        }

        private class BillDataViewer extends CForm<BillDataDTO> {

            public BillDataViewer() {
                super(BillDataDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().referenceNo(), new CNumberLabel(), new FieldDecoratorBuilder(100).build()));
                content.setWidget(++row, 0, inject(proto().amount(), new CMoneyLabel(), new FieldDecoratorBuilder(100).build()));
                content.setWidget(++row, 0, inject(proto().fromDate(), new CDateLabel(), new FieldDecoratorBuilder(100).build()));
                content.setWidget(++row, 0, inject(proto().dueDate(), new CDateLabel(), new FieldDecoratorBuilder(100).build()));

                content.setWidget(++row, 0, new Anchor("View Details", new Command() {
                    @Override
                    public void execute() {
                        ((BillingHistoryView.Presenter) getView().getPresenter()).viewBill(EntityFactory.createIdentityStub(Bill.class, getValue()
                                .getPrimaryKey()));
                    }
                }));

                return content;
            }
        }
    }
}