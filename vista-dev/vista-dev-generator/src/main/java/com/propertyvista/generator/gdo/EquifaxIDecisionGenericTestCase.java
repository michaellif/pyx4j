/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.generator.gdo;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;
import com.pyx4j.i18n.annotations.I18n;

/**
 * see resource iDecision-GenericTestCases.xlsx
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface EquifaxIDecisionGenericTestCase extends IEntity {

    @ImportColumn(names = { "LAST NAME" })
    IPrimitive<String> lastName();

    @ImportColumn(names = { "FIRST NAME" })
    IPrimitive<String> firstName();

    @ImportColumn(names = { "STREET NUMBER" })
    IPrimitive<String> streetNumber();

    @ImportColumn(names = { "ADDRESS" })
    IPrimitive<String> streetName();

    @ImportColumn(names = { "CITY" })
    IPrimitive<String> city();

    @ImportColumn(names = { "PR" })
    IPrimitive<String> province();

    @ImportColumn(names = { "POSTAL CODE" })
    IPrimitive<String> postalCode();

    @ImportColumn(names = { "BIRTHDATE YYYY-MM-DD" })
    IPrimitive<LogicalDate> birthDate();

}
