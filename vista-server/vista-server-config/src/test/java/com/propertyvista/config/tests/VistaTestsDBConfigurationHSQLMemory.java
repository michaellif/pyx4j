/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author vlads
 */
package com.propertyvista.config.tests;

import com.pyx4j.entity.rdb.cfg.ConfigurationHSQL;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;
import com.pyx4j.entity.rdb.dialect.NamingConvention;

import com.propertyvista.config.VistaDBNamingConvention;
import com.propertyvista.domain.financial.PaymentRecord;

public class VistaTestsDBConfigurationHSQLMemory extends ConfigurationHSQL {

    @Override
    public String dbName() {
        return "vista_tst";
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return MultitenancyType.SharedSchema;
    }

    @Override
    public int tablesIdentityOffset() {
        return 1000;
    }

    @Override
    public Integer tableIdentityOffset(String entityShortName) {
        if (entityShortName.equals(PaymentRecord.class.getSimpleName())) {
            return 1;
        } else {
            return null;
        }
    }

    @Override
    public NamingConvention namingConvention() {
        return new VistaDBNamingConvention(true);
    }

    @Override
    public ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionPoolType connectionType) {
        return new VistaTestsConnectionPoolConfiguration(connectionType);
    }
}
