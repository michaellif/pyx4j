/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.domain.security.CustomerSignature;

public class OriginalSignatureMock {

    public static void generateMockData(CustomerSignature signature) {
        switch (signature.signatureFormat().getValue(SignatureFormat.None)) {
        case AgreeBox:
            signature.agree().setValue(true);
            break;
        case AgreeBoxAndFullName:
            signature.fullName().setValue(ClientContext.getUserVisit().getName());
            break;
        case FullName:
            signature.agree().setValue(true);
            signature.fullName().setValue(ClientContext.getUserVisit().getName());
            break;
        case Initials:
            signature.initials().setValue(OriginalSignatureValidator.mockSignaturesInitials());
            break;
        case None:
            break;
        }
    }

}
