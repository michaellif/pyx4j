/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2013
 * @author yuriyl
 */
package com.propertyvista.biz.financial.maintenance.internal;

import com.propertyvista.biz.financial.maintenance.MaintenanceAbstractManager;

public class MaintenanceInternalManager extends MaintenanceAbstractManager {

    private static class SingletonHolder {
        public static final MaintenanceInternalManager INSTANCE = new MaintenanceInternalManager();
    }

    static MaintenanceInternalManager instance() {
        return SingletonHolder.INSTANCE;
    }
}
