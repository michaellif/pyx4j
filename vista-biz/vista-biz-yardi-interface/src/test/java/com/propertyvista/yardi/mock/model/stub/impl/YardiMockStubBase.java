/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.stub.impl;

import java.util.HashSet;
import java.util.Set;

public class YardiMockStubBase {
    private final Set<String> noAccess = new HashSet<>();

    public void enablePropertyAccess(String propertyCode, boolean enable) {
        if (enable) {
            noAccess.remove(propertyCode);
        } else {
            noAccess.add(propertyCode);
        }
    }

    public boolean hasAccess(String propertyCode) {
        return !noAccess.contains(propertyCode);
    }
}
