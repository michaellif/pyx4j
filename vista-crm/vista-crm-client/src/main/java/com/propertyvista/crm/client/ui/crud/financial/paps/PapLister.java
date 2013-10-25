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
package com.propertyvista.crm.client.ui.crud.financial.paps;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.payment.AutopayAgreement;

public class PapLister extends AbstractLister<AutopayAgreement> {

    private static final I18n i18n = I18n.get(PapLister.class);

    public PapLister() {
        super(AutopayAgreement.class, false);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().tenant()).build(),
                new MemberColumnDescriptor.Builder(proto().paymentMethod()).build(), 
                new MemberColumnDescriptor.Builder(proto().effectiveFrom()).build(),
                new MemberColumnDescriptor.Builder(proto().expiredFrom()).build(),
                
                new MemberColumnDescriptor.Builder(proto().createdBy(), false).build(),
                new MemberColumnDescriptor.Builder(proto().creationDate()).build(),
                
                new MemberColumnDescriptor.Builder(proto().updatedByTenant(), false).build(),
                new MemberColumnDescriptor.Builder(proto().updatedBySystem(), false).build(),
                new MemberColumnDescriptor.Builder(proto().updated(), false).build(),
                
                new MemberColumnDescriptor.Builder(proto().isDeleted()).visible(false).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().effectiveFrom(), true), new Sort(proto().expiredFrom(), false));
    }
}
