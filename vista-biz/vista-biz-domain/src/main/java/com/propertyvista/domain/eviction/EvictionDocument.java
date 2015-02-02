/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 18, 2014
 * @author stanp
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.domain.eviction;

import java.util.Date;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IHasFile;

import com.propertyvista.domain.blob.EvictionDocumentBlob;
import com.propertyvista.domain.tenant.lease.Lease;

@SecurityEnabled
@ToStringFormat("Document Added On {0}: {1}")
public interface EvictionDocument extends IHasFile<EvictionDocumentBlob> {

    @Owner
    @JoinColumn
    @ReadOnly
    @Detached
    EvictionStatusRecord record();

    @JoinColumn
    @ReadOnly
    @Detached
    Lease lease();

    @ReadOnly
    @Timestamp(Update.Created)
    @ToString(index = 0)
    IPrimitive<Date> addedOn();

    @ToString(index = 1)
    IPrimitive<String> title();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> note();

    // --------- internals --------
    interface PrintOrderId extends ColumnId {
    }

    @OrderColumn(PrintOrderId.class)
    IPrimitive<Integer> printOrder();
}
