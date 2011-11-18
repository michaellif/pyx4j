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
package com.propertyvista.portal.server.ptapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.server.common.security.VistaContext;

public class PtAppContext extends VistaContext {

    private final static Logger log = LoggerFactory.getLogger(PtAppContext.class);

    private static I18n i18n = I18n.get(PtAppContext.class);

    public static PtVisitAttributes getVisitAttributes() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn())) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("No Session"));
        }
        PtVisitAttributes attr = (PtVisitAttributes) v.getAttribute("pt-visit");
        if (attr == null) {
            attr = new PtVisitAttributes();
            v.setAttribute("pt-visit", attr);
        }
        return attr;
    }

    public static void setCurrentUserApplication(Application application) {
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

    public static Application getCurrentUserApplication() {
        return Persistence.service().retrieve(Application.class, getCurrentUserApplicationPrimaryKey());
    }

    public static Key getCurrentUserLeasePrimaryKey() {
        return PtAppContext.getCurrentUserApplication().lease().getPrimaryKey();
    }

    public static Lease getCurrentUserLease() {
        return Persistence.service().retrieve(Lease.class, PtAppContext.getCurrentUserApplication().lease().getPrimaryKey());
    }
}
