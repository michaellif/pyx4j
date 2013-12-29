/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.rpc.encryption;

import java.util.Date;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Transient
public interface EncryptedStorageKeyDTO extends IEntity {

    IPrimitive<String> name();

    IPrimitive<Boolean> isCurrent();

    IPrimitive<Boolean> decryptionEnabled();

    IPrimitive<Integer> recordsCount();

    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> created();

    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> expired();

}
