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
package com.propertyvista.server.domain;

import java.util.Date;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * This file would be stored in file system or in database.
 * 
 * DO Not use directly! @see com.propertyvista.server.common.blob.BlobService
 * 
 */
@RpcTransient
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface FileImageThumbnailBlobDTO extends IEntity {

    @Length(32 * 1024)
    IPrimitive<byte[]> xsmall();

    @Length(32 * 1024)
    IPrimitive<byte[]> small();

    @Length(64 * 1024)
    IPrimitive<byte[]> medium();

    @Length(256 * 1024)
    IPrimitive<byte[]> large();

    @Length(512 * 1024)
    IPrimitive<byte[]> xlarge();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

}
