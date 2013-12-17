/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.media;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IHasFile;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.blob.ProofOfEmploymentDocumentBlob;

public interface ProofOfEmploymentDocumentFile extends IHasFile<ProofOfEmploymentDocumentBlob> {

    @Owner
    @JoinColumn
    @Detached
    @ReadOnly
    ProofOfEmploymentDocumentFolder owner();

    @OrderColumn
    IPrimitive<Integer> orderInOwner();

}
