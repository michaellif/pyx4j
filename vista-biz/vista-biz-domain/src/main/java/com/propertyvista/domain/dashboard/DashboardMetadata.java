/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-22
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.dashboard;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.security.CrmUser;

@Caption(name = "Dashboard")
public interface DashboardMetadata extends IEntity {

    @I18n
    public enum DashboardType {
        system, building;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18nComment("Layout Type")
    public enum LayoutType {

        //@Translate("One full width column")
        One,

        //@Translate("Two equal columns")
        Two11,

        //@Translate("Two columns (33/67)")
        Two12,

        //@Translate("Two columns (67/33)")
        Two21,

        //@Translate("Three equal columns")
        Three,

        Report;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @MemberColumn(name = "dashboardType")
    IPrimitive<DashboardType> type();

    @ToString(index = 0)
    @NotNull
    IPrimitive<String> name();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @ReadOnly
    @Detached
    @NotNull
    @MemberColumn(name = "owner_user_id")
    CrmUser ownerUser();

    @Caption(name = "Shared")
    IPrimitive<Boolean> isShared();

    /** Only used for transporting gadgets from server to client, when dashboard adds a new gadget it add it's id to: encoded layout */
    @Transient
    // TODO it's supposed to be a set
    IList<GadgetMetadata> gadgetMetadataList();

    /**
     * Holds the docking position of the gadgets and gadgetIds {@link GadgetMetadata#gadgetId()}
     */
    IPrimitive<String> encodedLayout();

}
