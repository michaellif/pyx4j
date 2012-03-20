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
package com.propertyvista.crm.client.ui.crud.building.catalog;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureLister extends ListerBase<Feature> {

    public FeatureLister() {
        super(Feature.class, CrmSiteMap.Properties.Feature.class, false, true);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(),
            new MemberColumnDescriptor.Builder(proto().version().type(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().name(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().mandatory(), true).build(),
            new MemberColumnDescriptor.Builder(proto().version().recurring(), true).build()
        );//@formatter:on
    }

    @Override
    protected EntityListCriteria<Feature> updateCriteria(EntityListCriteria<Feature> criteria) {
        criteria.setVersionedCriteria(VersionedCriteria.finalizedOrDraft);
        return super.updateCriteria(criteria);
    }
}
