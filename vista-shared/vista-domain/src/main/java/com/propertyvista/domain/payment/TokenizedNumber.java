/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@ToStringFormat(value = "{0,choice,null#XXXX XXXX XXXX|!null#XXXX XXXX {0}}", nil = "XXXX XXXX XXXX")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@EmbeddedEntity
public interface TokenizedNumber extends IEntity {

    //TODO for pyx merge number @UpdatedBy(NumberUpdateValue.class)
    @RpcTransient
    IPrimitive<String> number();

    interface NumberUpdateValue extends ColumnId {
    }

    //TODO for pyx @UpdateValueColumn(NumberUpdateValue.class)
    @Transient(logTransient = true)
    IPrimitive<String> newNumberValue();

    // Number fragment presented to user
    @ToString
    @Length(4)
    IPrimitive<String> reference();
}
