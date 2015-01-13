/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author stanp
 */
package com.propertyvista.domain;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.shared.i18n.CompiledLocale;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface ILocalizedEntity extends IEntity {

    @NotNull
    IPrimitive<CompiledLocale> locale();
}
