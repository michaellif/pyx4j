/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.ptapp.OnlineApplication;

/**
 * Mark that IEntity is only bound to one PotentialTenant Application.
 * 
 * Use for security and retrieval of data by application
 * 
 * TODO suggest better name
 * 
 * Strictly speaking this should not extends IEntity but used here for convenience of
 * query criteria creations.
 * 
 * example:
 * 
 * <pre>
 * EntityQueryCriteria&lt;IBoundToApplication&gt; criteria = EntityQueryCriteria.create((Class&lt;IBoundToApplication&gt;) request.getEntityClass());
 * </pre>
 */
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface IBoundToApplication extends IEntity {

    @NotNull
    @Indexed
    @Detached
    OnlineApplication application();
}
