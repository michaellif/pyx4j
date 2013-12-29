/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.rpc.dto.PmcDTO;

public class PmcLister extends AbstractLister<PmcDTO> {

    protected static final I18n i18n = I18n.get(PmcLister.class);

    public PmcLister() {
        super(PmcDTO.class, true);

        setColumnDescriptors( //@formatter:off
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().dnsName()).build(),
            new MemberColumnDescriptor.Builder(proto().namespace()).visible(false).build(),
            new MemberColumnDescriptor.Builder(proto().status()).build(),
            new MemberColumnDescriptor.Builder(proto().created()).build(),
            new MemberColumnDescriptor.Builder(proto().updated()).build(),
            new MemberColumnDescriptor.Builder(proto().features().yardiIntegration()).visible(false).build(),
            new MemberColumnDescriptor.Builder(proto().features().tenantSureIntegration()).visible(false).build(),
            new MemberColumnDescriptor.Builder(proto().features().countryOfOperation()).visible(false).build()
        );//@formatter:on

        addActionItem(new Button(i18n.tr("Upload Merchant Accounts"), new Command() {
            @Override
            public void execute() {
                ((PmcListerView.Presenter) getPresenter()).uploadMerchantAccounts();
            }
        }));

    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
