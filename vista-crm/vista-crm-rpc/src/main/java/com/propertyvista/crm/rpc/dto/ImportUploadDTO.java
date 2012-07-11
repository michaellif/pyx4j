/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.dto.ImportDataFormatType;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface ImportUploadDTO extends IEntity {

    @I18n
    public static enum ImportType {

        newData,

        updateData,

        updateUnitAvailability,

        flatFloorplanAndUnits;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    @NotNull
    IPrimitive<ImportType> type();

    @NotNull
    IPrimitive<ImportDataFormatType> dataFormat();

    IPrimitive<Boolean> ignoreMissingMedia();
}
