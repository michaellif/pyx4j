/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.IBoundToApplication;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.SummaryPotentialTenantFinancial;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.dev.DataDump;

public class ApplicationDebug {

    public static void dumpApplicationSummary(Application application) {
        Summary summary = EntityFactory.create(Summary.class);
        summary.application().set(application);
        retrieveApplicationEntity(summary.unitSelection(), application);
        retrieveApplicationEntity(summary.tenants(), application);
        retrieveApplicationEntity(summary.pets(), application);
        retrieveApplicationEntity(summary.charges(), application);

        EntityQueryCriteria<PotentialTenantFinancial> financialCriteria = EntityQueryCriteria.create(PotentialTenantFinancial.class);
        financialCriteria.add(PropertyCriterion.eq(financialCriteria.proto().application(), application));
        for (PotentialTenantFinancial fin : PersistenceServicesFactory.getPersistenceService().query(financialCriteria)) {
            SummaryPotentialTenantFinancial sf = summary.tenantFinancials().$();
            sf.tenantFinancial().set(fin);
            summary.tenantFinancials().add(sf);
        }
        DataDump.dump("app-summary", summary);
    }

    private static <T extends IBoundToApplication> void retrieveApplicationEntity(T entity, Application application) {
        @SuppressWarnings("unchecked")
        EntityQueryCriteria<T> criteria = (EntityQueryCriteria<T>) EntityQueryCriteria.create(entity.getValueClass());
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        entity.set(PersistenceServicesFactory.getPersistenceService().retrieve(criteria));
    }
}
