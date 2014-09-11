/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.cards;

import java.util.Date;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CardsClearanceFile extends IEntity {

    IPrimitive<String> fileName();

    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> remoteFileDate();

    @Format("yyyy-MM-dd HH:mm")
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> received();

}
