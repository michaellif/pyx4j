/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.admin.domain.legal.LegalDocument;
import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.crm.rpc.services.PmcTermsOfServiceService;
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.server.jobs.TaskRunner;

public class PmcTermsOfServiceServiceImpl implements PmcTermsOfServiceService {

    @Override
    public void retrieveLegalTerms(AsyncCallback<LegalTermsContent> callback) {
        LegalTermsContent legalTerms = EntityFactory.create(LegalTermsContent.class);
        String terms = TaskRunner.runInAdminNamespace(new Callable<String>() {
            @Override
            public String call() {
                String result = null;
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), VistaTerms.Target.PMC);
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
        legalTerms.content().setValue(terms);
        callback.onSuccess(legalTerms);

    }

}
