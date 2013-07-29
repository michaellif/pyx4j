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
package com.propertyvista.operations.domain.payment.dbp;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@EmbeddedEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface DirectDebitRecordTrace extends IEntity {

    IPrimitive<String> locationCode();

    IPrimitive<String> collectionDate();

    IPrimitive<String> sourceCode();

    IPrimitive<String> traceNumber();

}
