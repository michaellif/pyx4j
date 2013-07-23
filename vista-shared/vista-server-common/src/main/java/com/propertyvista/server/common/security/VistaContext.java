/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.OperationsUser;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaUserType;

public class VistaContext {

    private final static Logger log = LoggerFactory.getLogger(VistaContext.class);

    private static final I18n i18n = I18n.get(VistaContext.class);

    private final static String userAttr = "vistaUser";

    public static Key getCurrentUserPrimaryKey() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn()) || (v.getUserVisit().getPrincipalPrimaryKey() == null)) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("No Session"));
        }
        return v.getUserVisit().getPrincipalPrimaryKey();
    }

    public static void setCurrentUser(AbstractUser abstractUser) {
        Context.getVisit().setAttribute(userAttr, abstractUser);
    }

    public static AbstractUser getUserFromVisit(Visit visit) {
        if (visit != null) {
            return (AbstractUser) visit.getAttribute(userAttr);
        } else {
            return null;
        }
    }

    public static AbstractUser getCurrentUser() {
        AbstractUser user = getCurrentUserIfAvalable();
        if (user == null) {
            log.trace("no session");
            throw new UnRecoverableRuntimeException(i18n.tr("No Session"));
        } else {
            return user;
        }
    }

    public static AbstractUser getCurrentUserIfAvalable() {
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn()) || (v.getUserVisit().getPrincipalPrimaryKey() == null)) {
            return null;
        } else {
            return (AbstractUser) v.getAttribute(userAttr);
        }
    }

    public static Class<? extends AbstractUser> getVistaUserClass(VistaUserType userType) {
        switch (userType) {
        case crm:
            return CrmUser.class;
        case customer:
            return CustomerUser.class;
        case operations:
            return OperationsUser.class;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static VistaUserType getVistaUserType(AbstractUser abstractUser) {
        if (abstractUser.isAssignableFrom(CrmUser.class)) {
            return VistaUserType.crm;
        } else if (abstractUser.isAssignableFrom(CustomerUser.class)) {
            return VistaUserType.customer;
        } else if (abstractUser.isAssignableFrom(OperationsUser.class)) {
            return VistaUserType.operations;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
