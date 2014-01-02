/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.util.List;
import java.util.concurrent.Callable;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.server.TaskRunner;

public class TenantSureTextFacadeImpl implements TenantSureTextFacade {

    @Override
    public String getPersonalDisclaimerText() {
        return "THIS IS PERSONAL DISCLAIMER";
    }

    @Override
    public String getFaq() {
        //@formatter:off        
        return "THIS IS FAQ";
        //@formatter:on
    }

    @Override
    public String getPreAuthorizedAgreement() {
        /// TODO needs refactoring and to join with the rest of stuff
        String terms = TaskRunner.runInOperationsNamespace(new Callable<String>() {
            @Override
            public String call() {
                String result = null;
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), VistaTerms.Target.TenantSurePreAuthorizedPaymentsAgreement);
                List<VistaTerms> list = Persistence.service().query(criteria);
                if (!list.isEmpty()) {
                    VistaTerms terms = list.get(0);
                    for (LegalDocument doc : terms.version().document()) {
                        if (doc.locale().getValue().getLanguage().startsWith("en")) {
                            result = doc.content().getValue();
                            break;
                        }
                    }
                }
                return result;
            }
        });
        if (terms == null) {
            throw new RuntimeException("Terms not found");
        }
        return terms;
    }

}
