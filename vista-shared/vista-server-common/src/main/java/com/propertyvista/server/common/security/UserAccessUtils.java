/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class UserAccessUtils {

    private static final I18n i18n = I18n.get(UserAccessUtils.class);

    private static String getPmcInterfaceUidBase(Pmc pmc) {
        if (pmc.interfaceUidBase().isNull()) {
            return pmc.namespace().getStringView();
        } else {
            return pmc.interfaceUidBase().getStringView();
        }
    }

    public static String getCrmUserInterfaceUid(CrmUserCredential credential) {
        if (credential.interfaceUid().isNull()) {
            return credential.getPrimaryKey().toString();
        } else {
            return credential.interfaceUid().getStringView();
        }
    }

    public static String getCrmUserUUID(Key principalPrimaryKey) {
        CrmUserCredential credential = Persistence.service().retrieve(CrmUserCredential.class, principalPrimaryKey);
        if (credential == null) {
            throw new UserRuntimeException(i18n.tr("Invalid Login Or Password"));
        }
        StringBuilder uid = new StringBuilder();
        if (ApplicationMode.isDevelopment()) {
            uid.append("$$");
        }

        Pmc pmc = VistaDeployment.getCurrentPmc();
        uid.append(getPmcInterfaceUidBase(pmc));
        uid.append(getCrmUserInterfaceUid(credential));
        return uid.toString();
    }
}
