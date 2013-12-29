/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.domain;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.media.ThumbnailSize;

@RpcTransient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface FileImageThumbnailBlob extends IEntity {

    @Indexed(uniqueConstraint = true, group = { "b,1" })
    IPrimitive<Key> blobKey();

    @Indexed(group = { "b,2" })
    IPrimitive<ThumbnailSize> thumbnailSize();

    @Length(1024 * 1024)
    IPrimitive<byte[]> content();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

}
