/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.crm.rpc.services.FeedbackService;
import com.propertyvista.server.common.security.UserAccessUtils;
import com.propertyvista.server.getsatisfaction.GetSatisfactionUrl;

public class FeedbackServiceImpl implements FeedbackService {

    private final static Logger log = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    private static final I18n i18n = I18n.get(FeedbackServiceImpl.class);

    @Override
    public void obtainSetsatisfactionLoginUrl(AsyncCallback<String> callback) {
        boolean isSecure = "https".equals(ServletUtils.getRequestProtocol(Context.getRequest()));
        Visit visit = Context.getVisit();
        String url;
        try {
            url = GetSatisfactionUrl.url(visit.getUserVisit().getEmail(), visit.getUserVisit().getName(),
                    UserAccessUtils.getCrmUserUUID(visit.getUserVisit().getPrincipalPrimaryKey()), isSecure);
        } catch (Throwable e) {
            log.error("Error", e);
            throw new UserRuntimeException(i18n.tr("Feedback Service unavailable"));
        }
        callback.onSuccess(url);
    }
}
