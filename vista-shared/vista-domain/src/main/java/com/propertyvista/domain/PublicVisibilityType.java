/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum PublicVisibilityType implements Serializable {

    internal,

    tenant,

    existingTenant,

    @Translate("Public")
    global;

    public static Collection<PublicVisibilityType> visibleToTenant() {
        return EnumSet.of(tenant, global);
    }

    public static Collection<PublicVisibilityType> visibleToExistingTenant() {
        return EnumSet.of(existingTenant, global);
    }

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }
}
