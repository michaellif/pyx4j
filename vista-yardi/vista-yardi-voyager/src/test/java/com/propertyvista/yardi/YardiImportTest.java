/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi;

import org.junit.Ignore;
import org.junit.Test;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

@Ignore
public class YardiImportTest extends YardiTestBase {

    @Test
    public void testImport() throws Exception {
        preloadData();
        PmcYardiCredential yardiCredential = VistaDeployment.getPmcYardiCredential();
        StatisticsRecord dynamicStatisticsRecord = EntityFactory.create(StatisticsRecord.class);
        dynamicStatisticsRecord.total().setValue(0L);
        dynamicStatisticsRecord.failed().setValue(0L);
        dynamicStatisticsRecord.processed().setValue(0L);
        YardiResidentTransactionsService.getInstance().updateAll(yardiCredential, dynamicStatisticsRecord);
    }
}
