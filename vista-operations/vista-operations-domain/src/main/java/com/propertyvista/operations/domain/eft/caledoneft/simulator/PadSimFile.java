/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.caledoneft.simulator;

import java.util.Date;

import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.financial.FundsTransferType;

/**
 * e-Cheque or PAD (pre-authorized debit transactions) / EFT Batch payments
 * 
 * @see <a href="http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Caledon"/>
 * @see <a href="http://jira.birchwoodsoftwaregroup.com/wiki/download/attachments/4587553/CCS-PAD-File-Specifications.pdf"/>
 */
@RequireFeature(ApplicationDevelopmentFeature.class)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@ToStringFormat("{3,choice,null#|0#|1#Returns for} {0} {1} {2}")
public interface PadSimFile extends IEntity {

    public enum PadSimFileStatus {

        Acknowledged,

        ReconciliationSent,

        ReturnSent;

    };

    @ToString(index = 0)
    IPrimitive<String> fileName();

    @ToString(index = 1)
    @Editor(type = Editor.EditorType.label)
    IPrimitive<FundsTransferType> fundsTransferType();

    @ToString(index = 2)
    @Editor(type = Editor.EditorType.label)
    IPrimitiveSet<PadSimFileStatus> state();

    @ToString(index = 3)
    IPrimitive<Boolean> returns();

    PadSimFile originalFile();

    IPrimitive<String> companyId();

    IPrimitive<String> fileCreationNumber();

    IPrimitive<String> fileCreationDate();

    IPrimitive<String> fileType();

    IPrimitive<String> fileVersion();

    IPrimitive<Integer> batchRecordsCount();

    IPrimitive<Integer> recordsCount();

    IPrimitive<String> fileAmount();

    @Caption(name = "Acknowledgment Code", description = "If Empty Calculated base on batches.\n  '0000' - Accepted\n '0001' - File out of balance\n '0002' - Batch level reject\n '0003' - Transaction reject '0004' - Batch and transaction reject\n '0005' Detail Record Count out of balance\n '0006' - Invalid File Format\n '0007' - Invalid File Header")
    IPrimitive<String> acknowledgmentStatusCode();

    IPrimitive<String> acknowledgmentRejectReasonMessage();

    @Timestamp(Timestamp.Update.Created)
    @Format("yyyy-MM-dd HH:mm")
    @Editor(type = Editor.EditorType.label)
    IPrimitive<Date> received();

    @Timestamp(Timestamp.Update.Updated)
    @Format("yyyy-MM-dd HH:mm")
    @Editor(type = Editor.EditorType.label)
    IPrimitive<Date> updated();

    @Editor(type = Editor.EditorType.label)
    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> acknowledged();

    @Editor(type = Editor.EditorType.label)
    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> reconciliationSent();

    @Editor(type = Editor.EditorType.label)
    @Format("yyyy-MM-dd HH:mm")
    IPrimitive<Date> returnSent();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    @OrderBy(PrimaryKey.class)
    IList<PadSimBatch> batches();

}
