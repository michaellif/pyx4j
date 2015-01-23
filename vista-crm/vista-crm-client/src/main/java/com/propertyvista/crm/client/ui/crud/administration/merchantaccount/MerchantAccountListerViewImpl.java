/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.administration.merchantaccount;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.admin.MerchantAccountCrudService;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.shared.config.VistaFeatures;

public class MerchantAccountListerViewImpl extends AbstractListerView<MerchantAccount> implements MerchantAccountListerView {

    public MerchantAccountListerViewImpl() {
        setDataTablePanel(new MerchantAccountLister());
    }

    public static class MerchantAccountLister extends SiteDataTablePanel<MerchantAccount> {

        public MerchantAccountLister() {
            super(MerchantAccount.class, GWT.<AbstractListCrudService<MerchantAccount>> create(MerchantAccountCrudService.class), true);

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().accountName()).filterAlwaysShown(true).build(),//
                    new ColumnDescriptor.Builder(proto().bankId()).filterAlwaysShown(true).build(),//
                    new ColumnDescriptor.Builder(proto().branchTransitNumber()).build(),//
                    new ColumnDescriptor.Builder(proto().accountNumber()).build(),//
                    new ColumnDescriptor.Builder(proto().paymentsStatus()).searchable(false).sortable(false).build());

            setDataTableModel(new DataTableModel<MerchantAccount>());
        }

        @Override
        public boolean canCreateNewItem() {
            return super.canCreateNewItem() && VistaFeatures.instance().yardiIntegration() && super.canCreateNewItem();
        }
    }
}
