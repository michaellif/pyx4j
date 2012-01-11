/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface BillInfoDTO extends IEntity {

    IPrimitive<Boolean> paid();

    @Format("MMM dd, yyyy")
    IPrimitive<LogicalDate> dueDate();

    @Format("MMM dd, yyyy")
    IPrimitive<LogicalDate> receivedOn();

    IPrimitive<String> message();

    @Caption(name = "Current Balance")
    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<Double> ammount();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<Double> lastPayment();

}
