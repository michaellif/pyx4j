/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.blob;

import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.i18n.annotations.I18n;

/**
 * This file would be stored in file system or in database.
 * 
 * DO Not use directly! @see com.propertyvista.server.common.blob.BlobService
 * 
 */
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface IFileBlob extends AbstractIFileBlob {

    IPrimitive<String> name();

    @Length(15 * 1024 * 1024)
    @RpcTransient
    IPrimitive<byte[]> data();

    IPrimitive<String> contentType();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

}
