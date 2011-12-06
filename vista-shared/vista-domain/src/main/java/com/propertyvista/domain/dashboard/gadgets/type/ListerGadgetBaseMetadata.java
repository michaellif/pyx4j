/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2011
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;

@DiscriminatorValue("ListerGadgetBaseSettings")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface ListerGadgetBaseMetadata extends GadgetMetadata {
    IPrimitive<Integer> pageSize();

    IPrimitive<Integer> pageNumber();

    @Owned
    IList<ColumnDescriptorEntity> columnDescriptors();

    @EmbeddedEntity
    ColumnDescriptorEntity primarySortColumn();
}
