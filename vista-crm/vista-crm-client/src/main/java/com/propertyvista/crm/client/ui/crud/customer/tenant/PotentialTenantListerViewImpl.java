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
 */
package com.propertyvista.crm.client.ui.crud.customer.tenant;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;

import com.propertyvista.dto.TenantDTO;

public class PotentialTenantListerViewImpl extends AbstractListerView<TenantDTO> implements PotentialTenantListerView {

    public PotentialTenantListerViewImpl() {
        setDataTablePanel(new TenantLister() {
            @Override
            protected EntityListCriteria<TenantDTO> updateCriteria(EntityListCriteria<TenantDTO> criteria) {
                return updateListCriteriaForPotentialLeaseParticipants(criteria);
            }

            @Override
            public List<Sort> getDefaultSorting() {
                return Arrays.asList(new Sort(proto().lease().leaseId(), true), new Sort(proto().customer().person().name(), false));
            }
        });
    }
}
