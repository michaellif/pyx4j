/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-18
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease.print;

import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;

class PrintableSignatureChecker {

    public static boolean isPrintable(ISignature signature) {//@formatter:off
        return signature.signatureFormat().getValue() == SignatureFormat.AgreeBoxAndFullName
                || signature.signatureFormat().getValue() == SignatureFormat.FullName
                || signature.signatureFormat().getValue() == SignatureFormat.Initials;
    }//@formatter:on

    public static boolean needsPlaceholder(ISignature signature) {
        return needsPlaceholder(signature.signatureFormat().getValue());
    }

    public static boolean needsPlaceholder(SignatureFormat signatureFormat) {
        return (signatureFormat == SignatureFormat.FullName || signatureFormat == SignatureFormat.AgreeBoxAndFullName);
    }
}
