/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 28, 2015
 * @author stanp
 */
package com.propertyvista.server.common.util;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4LeaseData;

public class N4DataConverter {

    public static void copyN4BatchToLeaseData(N4Batch n4batch, N4LeaseData n4data) {
        Persistence.ensureRetrieve(n4batch, AttachLevel.Attached);
        n4data.terminationDateOption().set(n4batch.terminationDateOption());
        n4data.serviceDate().set(n4batch.serviceDate());
        n4data.deliveryMethod().set(n4batch.deliveryMethod());
        n4data.deliveryDate().set(n4batch.deliveryDate());
        n4data.companyLegalName().set(n4batch.companyLegalName());
        n4data.companyAddress().set(n4batch.companyAddress());
        n4data.phoneNumber().set(n4batch.phoneNumber());
        n4data.faxNumber().set(n4batch.faxNumber());
        n4data.emailAddress().set(n4batch.emailAddress());
        n4data.phoneNumberCS().set(n4batch.phoneNumberCS());
        n4data.isLandlord().set(n4batch.isLandlord());
        n4data.signatureDate().set(n4batch.signatureDate());
        n4data.signingAgent().set(n4batch.signingAgent());
        n4data.servicingAgent().set(n4batch.servicingAgent());
    }
}
