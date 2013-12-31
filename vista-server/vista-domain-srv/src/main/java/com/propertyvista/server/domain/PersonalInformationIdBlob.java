/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.domain;

import java.util.Date;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@RpcTransient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
/** Blob of the id documents PMCs upload for the Equifax, during Credit Check Setup Wizard */
public interface PersonalInformationIdBlob extends IEntity {

    IPrimitive<String> contentType();

    /**
     * This is actual BLOB of the Image or PDF stored on server
     */
    @RpcTransient
    @Length(15 * 1024 * 1024)
    IPrimitive<byte[]> data();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

}