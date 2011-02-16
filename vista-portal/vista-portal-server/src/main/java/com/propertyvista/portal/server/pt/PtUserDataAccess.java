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
package com.propertyvista.portal.server.pt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.rpc.pt.PtUserVisit;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

public class PtUserDataAccess {

    private final static Logger log = LoggerFactory.getLogger(PtUserDataAccess.class);

    private static I18n i18n = I18nFactory.getI18n();

    public static Long getCurrentUserPrimaryKey() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn()) || (v.getUserVisit().getPrincipalPrimaryKey() == null)) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("no session"));
        }
        return v.getUserVisit().getPrincipalPrimaryKey();
    }

    public static User getCurrentUser() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn()) || (v.getUserVisit().getPrincipalPrimaryKey() == null)) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("no session"));
        }
        User user = EntityFactory.create(User.class);
        user.setPrimaryKey(v.getUserVisit().getPrincipalPrimaryKey());
        user.name().setValue(v.getUserVisit().getName());
        user.email().setValue(v.getUserVisit().getEmail());
        return user;
    }

    public static Long getCurrentUserApplicationPrimaryKey() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn())) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("no session"));
        }
        if (((PtUserVisit) v.getUserVisit()).getApplicationPrimaryKey() == null) {
            log.trace("no application selected");
            throw new UserRuntimeException(i18n.tr("no application selected"));
        }
        return ((PtUserVisit) v.getUserVisit()).getApplicationPrimaryKey();
    }

    public static Application getCurrentUserApplication() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn())) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("no session"));
        }
        if (((PtUserVisit) v.getUserVisit()).getApplicationPrimaryKey() == null) {
            log.trace("no application selected");
            throw new UserRuntimeException(i18n.tr("no application selected"));
        }
        Application application = EntityFactory.create(Application.class);
        application.setPrimaryKey(((PtUserVisit) v.getUserVisit()).getApplicationPrimaryKey());
        return application;
    }

    public static void setCurrentUserApplication(Application application) {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn())) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("no session"));
        }
        ((PtUserVisit) v.getUserVisit()).setApplicationPrimaryKey(application.getPrimaryKey());
    }

}
