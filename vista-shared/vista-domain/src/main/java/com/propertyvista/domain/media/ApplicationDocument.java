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

import java.io.Serializable;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.IBoundToApplication;

public interface ApplicationDocument extends IEntity, IBoundToApplication {

    public static enum DocumentType implements Serializable {

        securityInfo,

        income;

    }

    // This is not shown in UI only defines where the document is attached.
    @MemberColumn(name = "tp")
    IPrimitive<DocumentType> type();

    IPrimitive<Key> dataId();

    IPrimitive<String> filename();

    IPrimitive<Integer> fileSize();

}
