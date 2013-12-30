/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.dbp;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@EmbeddedEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface DirectDebitRecordTrace extends IEntity {

    //Location code assigned to each source (e.g. BMO is 1001)
    IPrimitive<String> locationCode();

    IPrimitive<String> collectionDate();

    //Identification of source of remittance information. Value of 14 represents telebanking/electronic bill payments.
    IPrimitive<String> sourceCode();

    // If the source location (e.g. TDBANK) sends a trace number for their incoming file, then that trace number is referenced here. Each source location can have a different trace number format.
    IPrimitive<String> traceNumber();

}
