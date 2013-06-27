/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.factories.autopay;

import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.dto.payment.AutoPayReviewDTO;

public class AutoPayChangesReviewDataGrid extends Composite {

    private static final I18n i18n = I18n.get(AutoPayChangesReviewDataGrid.class);

    private final DataGrid<AutoPayReviewDTO> dataGrid = new DataGrid<AutoPayReviewDTO>(Integer.MAX_VALUE);

    private class HeaderBuilder extends AbstractHeaderOrFooterBuilder<AutoPayReviewDTO> {

        //@formatter:off
        private final Header<String> buildingHeader = new TextHeader(i18n.tr("Building"));
        private final Header<String> unitHeader = new TextHeader(i18n.tr("Unit"));
        private final Header<String> leaseHeader = new TextHeader(i18n.tr("Lease ID"));
        private final Header<String> expectedMoveOutHeader = new TextHeader(i18n.tr("Lease ID"));        
        private final Header<String> tenantNameHeader = new TextHeader(i18n.tr("Tenant Name"));
        private final Header<String> chargeCodeHeader = new TextHeader(i18n.tr("Charge Code"));
        
        // suspended               
        private final Header<String> suspendedTotalPriceHeader = new TextHeader(i18n.tr("Total Price"));        
        private final Header<String> suspendedPaymentHeader = new TextHeader(i18n.tr("Payment"));        
        private final Header<String> suspendedPercentOfTotalHeader = new TextHeader(i18n.tr("% of Total"));
        
        // suggested              
        private final Header<String> suggestedTotalPriceHeader = new TextHeader(i18n.tr("Total Price"));        
        private final Header<String> suggestedPaymentHeader = new TextHeader(i18n.tr("Payment"));        
        private final Header<String> suggestedPercentOfTotalHeader = new TextHeader(i18n.tr("% of Total"));
        
        private final Header<String> paymentDueHeader = new TextHeader(i18n.tr("Payment Due"));
        //@formatter:on

        public HeaderBuilder() {
            super(dataGrid, false);
        }

        @Override
        protected boolean buildHeaderOrFooterImpl() {
            // TODO Auto-generated method stub
            return false;
        }
    }

    public AutoPayChangesReviewDataGrid() {
        initWidget(dataGrid);
    }
}
