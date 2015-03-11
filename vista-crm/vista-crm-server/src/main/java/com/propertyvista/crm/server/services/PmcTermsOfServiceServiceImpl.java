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
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.crm.rpc.services.PmcTermsOfServiceService;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.server.VistaTermsUtils;
import com.propertyvista.shared.rpc.LegalTermTO;

//TODO use the same way as PortalVistaTermsService
public class PmcTermsOfServiceServiceImpl implements PmcTermsOfServiceService {

    @Override
    public void retrieveLegalTerms(AsyncCallback<LegalTermTO> callback) {
        LegalTermTO result = null;

        VistaTerms terms = VistaTermsUtils.retrieveVistaTerms(VistaTerms.Target.PmcPropertyVistaService);
        for (LegalDocument doc : terms.version().document()) {
            if (doc.locale().getValue().getLanguage().startsWith("en")) {
                result = EntityFactory.create(LegalTermTO.class);
                result.caption().setValue(terms.version().caption().getValue());
                result.content().setValue(doc.content().getValue());
                break;
            }
        }

        if (result == null) {
            throw new RuntimeException("Terms not found");
        }
        callback.onSuccess(result);
    }
}
