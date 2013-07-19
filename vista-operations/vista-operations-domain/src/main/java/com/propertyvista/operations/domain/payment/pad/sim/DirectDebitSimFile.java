/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.payment.pad.sim;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface DirectDebitSimFile extends IEntity {

    public enum DirectDebitSimFileStatus {

        New,

        Sent

    };

    @Timestamp(Timestamp.Update.Created)
    @Format("yyyy-MM-dd HH:mm")
    @ToString
    IPrimitive<Date> creatationDate();

    @Editor(type = Editor.EditorType.label)
    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> sentDate();

    @ToString
    @Editor(type = Editor.EditorType.label)
    IPrimitive<DirectDebitSimFileStatus> status();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    @OrderBy(PrimaryKey.class)
    IList<DirectDebitSimRecord> records();

}
