/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common.deposit;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.dto.DepositLifecycleDTO;

public class DepositLifecycleLister extends AbstractLister<DepositLifecycleDTO> {

    private final static I18n i18n = I18n.get(DepositLifecycleLister.class);

    public DepositLifecycleLister() {
        super(DepositLifecycleDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().deposit().billableItem()).searchable(false).sortable(false).columnTitle(i18n.tr("Service/Feature")).build(),
            new MemberColumnDescriptor.Builder(proto().deposit().type()).searchable(false).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().deposit().amount()).searchable(false).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().status()).build(),
            new MemberColumnDescriptor.Builder(proto().depositDate(), false).build(),
            new MemberColumnDescriptor.Builder(proto().refundDate()).build(),
            new MemberColumnDescriptor.Builder(proto().currentAmount()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().depositDate(), false));
    }
}
