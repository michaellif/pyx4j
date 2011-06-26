/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

@Transient
public interface MaintenanceRequestDTO extends IEntity {

    @Translatable
    public enum MaintenanceType {

        Plumbing, Electrical, Heating, Cooling, Other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum StatusType {

        New, Pending, Scheduled, Resolved;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Caption(name = "Select a category")
    IPrimitive<MaintenanceType> maintenanceType();

    @Editor(type = EditorType.textarea)
    @Caption(name = "Describe the problem")
    IPrimitive<String> problemDescription();

    IPrimitive<LogicalDate> whenRequested();

    @Caption(name = "Last updated")
    IPrimitive<LogicalDate> updated();

    IPrimitive<StatusType> status();

}
