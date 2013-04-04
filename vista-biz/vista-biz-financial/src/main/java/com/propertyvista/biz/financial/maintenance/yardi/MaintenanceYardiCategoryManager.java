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
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance.yardi;

import com.propertyvista.domain.maintenance.IssueElement;

public class MaintenanceYardiCategoryManager {

    private static class SingletonHolder {
        public static final MaintenanceYardiCategoryManager INSTANCE = new MaintenanceYardiCategoryManager();
    }

    static MaintenanceYardiCategoryManager instance() {
        return SingletonHolder.INSTANCE;
    }

    protected IssueElement getMaintenanceRequestCategoryMeta() {

        // TODO map categories we get from Yardi into this? Still work in progress, either returns IssueElement or something else

        return null;
    }

}
