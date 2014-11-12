/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 11, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.domain.legal.n4cs;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface N4CSSignature extends IEntity {
    public enum SignedBy {

        PL, PT, RA, PO

    }

    IPrimitive<SignedBy> signedBy();

    IPrimitive<byte[]> signature();

    IPrimitive<LogicalDate> signatureDate();

    IPrimitive<String> firstname();

    IPrimitive<String> lastname();

    IPrimitive<String> phone();

}
