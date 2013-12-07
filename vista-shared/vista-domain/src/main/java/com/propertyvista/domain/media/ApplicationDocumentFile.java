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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.shared.adapters.ApplicationDocumentUploadedBlobSecurityAdapter;

public interface ApplicationDocumentFile extends IFile {

    @Override
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    //TODO review What it was doing it does
    @MemberColumn(modificationAdapters = { ApplicationDocumentUploadedBlobSecurityAdapter.class })
    IPrimitive<Key> blobKey();

    @Owner
    @JoinColumn
    @Detached
    @ReadOnly
    ApplicationDocument owner();

    @OrderColumn
    IPrimitive<Integer> orderInOwner();

}
