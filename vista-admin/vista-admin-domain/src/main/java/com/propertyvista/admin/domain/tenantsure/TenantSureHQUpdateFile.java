/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-01
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.tenantsure;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface TenantSureHQUpdateFile extends IEntity {

    IPrimitive<String> fileName();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    IList<TenantSureHQUpdateRecord> batches();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> received();
}
