/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.domain;

import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

/**
 * See "Backup Policy.doc", Backup Scope: Logical Database Backup of time-sensitive data (Equifax)
 * 
 * The data in this table will not bee stored in backup.
 * 
 */
@RpcTransient
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED, namespace = VistaNamespace.expiringNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CustomerCreditCheckReportNoBackup extends CustomerCreditCheckReport {

}
