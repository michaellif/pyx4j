/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 6, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.util;

import java.util.Locale;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.server.I18nManager;

import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.policy.PolicyManager;

public class LegalStuffUtils {

    public static LeaseTermsPolicy retrieveLegalTermsPolicy() {
        LeaseTermsPolicy termsPolicy = (LeaseTermsPolicy) PolicyManager.effectivePolicy(PtAppContext.getCurrentUserLease().unit(), LeaseTermsPolicy.class);
        if (termsPolicy == null) {
            throw new Error("There is no Legal Policy!?.");
        }

        return termsPolicy;
    }

    public static LegalTermsDescriptorDTO formLegalTerms(LegalTermsDescriptor terms) {
        LegalTermsDescriptorDTO ltd = EntityFactory.create(LegalTermsDescriptorDTO.class);
        ltd.name().set(terms.name());
        ltd.description().set(terms.description());

        Locale locale = I18nManager.getThreadLocale();
        for (LegalTermsContent content : terms.content()) {
            if (locale.getLanguage().equals(content.locale().lang().getValue().name())) {
                ltd.content().set(content);
                break;
            }
        }
        if (ltd.content().isEmpty()) {
            // Locale not found, select the first one:
            if (!terms.content().isEmpty()) {
                ltd.content().set(terms.content().get(0));
            } else {
                throw new Error("Empty Legal Content!?.");
            }
        }

        return ltd;
    }

}
