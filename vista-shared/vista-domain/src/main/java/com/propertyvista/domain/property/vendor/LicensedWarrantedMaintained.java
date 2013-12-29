/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-28
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.vendor;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface LicensedWarrantedMaintained extends IEntity {

    @EmbeddedEntity
    License license();

    @EmbeddedEntity
    Warranty warranty();

    @EmbeddedEntity
    @Caption(name = "Maintenance Contract")
    Maintenance maintenance();
}
