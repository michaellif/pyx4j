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
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.domain.imports;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.operationsNamespace)
@Caption(name = "OAPI Conversion")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface OapiConversion extends IEntity {

    public enum Type {

        Base,

    }

    @ReadOnly
    @Format("yyyy-MM-dd HH:mm")
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<LogicalDate> created();

    @NotNull
    @ToString
    IPrimitive<String> name();

    @MemberColumn(name = "tp")
    IPrimitive<Type> type();

    IPrimitive<String> description();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<OapiConversionFile> conversionFiles();

}
