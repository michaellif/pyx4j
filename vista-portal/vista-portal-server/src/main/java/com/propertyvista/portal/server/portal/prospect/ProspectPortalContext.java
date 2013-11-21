/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

public class ProspectPortalContext extends PortalVistaContext {

    private final static Logger log = LoggerFactory.getLogger(ProspectPortalContext.class);

    private static final I18n i18n = I18n.get(ProspectPortalContext.class);

    public static ProspectPortalAttributes getVisitAttributes() {
        Visit visit = Context.getVisit();
        if ((visit == null) || (!visit.isUserLoggedIn())) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("No Session"));
        }
        ProspectPortalAttributes attr = (ProspectPortalAttributes) visit.getAttribute("pt-visit");
        if (attr == null) {
            attr = new ProspectPortalAttributes();
            visit.setAttribute("pt-visit", attr);
        }
        return attr;
    }

    public static void setCurrentUserApplication(OnlineApplication application) {
        getVisitAttributes().setApplicationPrimaryKey(application.getPrimaryKey());
    }

    public static Key getCurrentUserApplicationPrimaryKey() {
        Key key = getVisitAttributes().getApplicationPrimaryKey();
        if (key == null) {
            log.trace("no application selected");
            throw new UserRuntimeException(i18n.tr("No Application Has Been Selected"));
        }
        return key;
    }

    public static OnlineApplication retrieveCurrentUserApplication() {
        return Persistence.service().retrieve(OnlineApplication.class, getCurrentUserApplicationPrimaryKey());
    }

}
