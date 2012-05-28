/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.io.File;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.admin.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.payment.pad.CaledonPadReconciliationParser;

class PadCaledonReconciliation {

    PadReconciliationFile processFile(File file) {
        PadReconciliationFile reconciliationFile = new CaledonPadReconciliationParser().parsReport(file);
        Persistence.service().persist(reconciliationFile);

        // TODO Match transaction?

        for (PadReconciliationSummary summary : reconciliationFile.batches()) {

        }

        Persistence.service().commit();
        return reconciliationFile;
    }
}
