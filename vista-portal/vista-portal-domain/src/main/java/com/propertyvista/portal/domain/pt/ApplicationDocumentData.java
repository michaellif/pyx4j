/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@RpcTransient
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED, expands = ApplicationDocument.class)
public interface ApplicationDocumentData extends IEntity {

    /**
     * This is actual BLOB of the Image or PDF stored on server
     */
    @RpcTransient
    @Length(5 * 1024 * 1024)
    IPrimitive<byte[]> data();

}
