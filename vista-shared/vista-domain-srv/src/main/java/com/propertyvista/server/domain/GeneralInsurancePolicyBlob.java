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

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@RpcTransient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface GeneralInsurancePolicyBlob extends IFileBlob {

    /**
     * This is actual BLOB of the Image or PDF stored on server
     */
    @Override
    @RpcTransient
    @Length(15 * 1024 * 1024)
    IPrimitive<byte[]> data();

}
