/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 28, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IHasFile;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.blob.MediaFileBlob;

@ToStringFormat("{0}")
public interface MediaFile extends IHasFile<MediaFileBlob> {

    @Override
    @ToString(index = 0)
    IFile<MediaFileBlob> file();

    IPrimitive<String> caption();

    IPrimitive<String> description();

    @NotNull
    IPrimitive<PublicVisibilityType> visibility();
}
