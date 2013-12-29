/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.security.shared.Behavior;

public enum PortalResidentBehavior implements Behavior {

    LeaseSelectionRequired,

    HasMultipleLeases,

    Resident,

    LeaseSigningRequired,

    ResidentPrimary,

    ResidentSecondary,

    Guarantor /* is as well Prospective see VistaPortalAccessControlList */;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    };
}
