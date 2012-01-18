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
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTableOrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.File;

public interface ApplicationDocument extends File {

    @Override
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    @ReadOnly
    IPrimitive<Key> blobKey();

    @Owner
    @JoinColumn
    //TODO @ReadOnly  assign once only  
    ApplicationDocumentHolder owner();

    interface OrderColumnId extends ColumnId {

    }

    @JoinTableOrderColumn(OrderColumnId.class)
    IPrimitive<Integer> orderInOwner();
}
