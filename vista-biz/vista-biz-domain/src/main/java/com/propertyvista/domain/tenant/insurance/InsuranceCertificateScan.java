/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 9, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.insurance;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IHasFile;

import com.propertyvista.domain.blob.InsuranceCertificateScanBlob;

@ToStringFormat("Scanned Document {0,choice,null#(No Description)|!null#({0})}")
public interface InsuranceCertificateScan extends IHasFile<InsuranceCertificateScanBlob> {

    @Owner
    @Detached
    @JoinColumn
    @ReadOnly
    InsuranceCertificate<?> certificate();

    @ToString(index = 0)
    IPrimitive<String> description();

    @Override
    @EmbeddedEntity
    @NotNull
    IFile<InsuranceCertificateScanBlob> file();

}
