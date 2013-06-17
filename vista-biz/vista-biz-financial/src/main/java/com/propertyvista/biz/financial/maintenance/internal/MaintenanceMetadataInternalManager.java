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
package com.propertyvista.biz.financial.maintenance.internal;

import com.propertyvista.biz.financial.maintenance.MaintenanceMetadataAbstractManager;

public class MaintenanceMetadataInternalManager extends MaintenanceMetadataAbstractManager {

    private static class SingletonHolder {
        public static final MaintenanceMetadataInternalManager INSTANCE = new MaintenanceMetadataInternalManager();
    }

    static MaintenanceMetadataInternalManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected String[] getLevels() {
        return new String[] { "IssueElement", "IssueRepairSubject", "IssueSubjectDetails", "IssueClassification" };
    }

    @Override
    protected String getRoot() {
        return "ROOT";
    }
}
