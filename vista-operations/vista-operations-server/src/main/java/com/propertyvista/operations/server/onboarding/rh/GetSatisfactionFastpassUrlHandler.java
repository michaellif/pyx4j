/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.onboarding.rh;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.onboarding.GetSatisfactionFastpassUrlRequestIO;
import com.propertyvista.onboarding.GetSatisfactionFastpassUrlResponseIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.server.common.security.UserAccessUtils;
import com.propertyvista.server.getsatisfaction.GetSatisfactionUrl;

public class GetSatisfactionFastpassUrlHandler extends AbstractRequestHandler<GetSatisfactionFastpassUrlRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(GetSatisfactionFastpassUrlHandler.class);

    public GetSatisfactionFastpassUrlHandler() {
        super(GetSatisfactionFastpassUrlRequestIO.class);
    }

    @Override
    public ResponseIO execute(GetSatisfactionFastpassUrlRequestIO request) {
        log.info("User {} requested {}", new Object[] { request.email().getValue(), "GetSatisfactionFastpassUrl" });

        GetSatisfactionFastpassUrlResponseIO response = EntityFactory.create(GetSatisfactionFastpassUrlResponseIO.class);

        boolean isSecure = true;
        if (!request.secureUrl().isNull()) {
            isSecure = request.secureUrl().getValue(Boolean.TRUE);
        }

        String uid;
        String name;

        EntityQueryCriteria<OnboardingUserCredential> criteria = EntityQueryCriteria.create(OnboardingUserCredential.class);
        String email = EmailValidator.normalizeEmailAddress(request.email().getValue());
        criteria.add(PropertyCriterion.eq(criteria.proto().user().email(), email));
        List<OnboardingUserCredential> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            response.success().setValue(Boolean.FALSE);
            return response;
        } else {
            OnboardingUserCredential credential = users.get(0);
            Persistence.service().retrieve(credential.user());
            name = credential.user().name().getValue();

            uid = UserAccessUtils.getUserUUID(credential.pmc(), credential);
        }

        String url;
        try {
            url = GetSatisfactionUrl.url(email, name, uid, isSecure);
        } catch (Throwable e) {
            log.error("Error", e);
            throw new UserRuntimeException("Feedback Service unavailable");
        }
        response.success().setValue(Boolean.TRUE);
        response.fastpassAuthenticationUrl().setValue(url);
        return response;
    }
}
