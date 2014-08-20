/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 5, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.util.Collection;
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;

public interface LeaseApplication extends IEntity {

    @I18n(context = "Lease Application")
    @XmlType(name = "LeaseApplicationStatus")
    public enum Status {

        InProgress, // Mapped to Lease status Application

        Submitted,

        PendingFurtherInformation,

        PendingDecision,

        Approved, // Mapped to Lease status Approved

        Declined, // Mapped to Lease status Closed

        Cancelled; // Mapped to Lease status Cancelled

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        // state sets:

        public static Collection<Status> draft() {
            return EnumSet.of(InProgress, Submitted, PendingFurtherInformation, PendingDecision);
        }

        public static Collection<Status> current() {
            return EnumSet.of(Approved);
        }

        public static Collection<Status> processed() {
            return EnumSet.of(Approved, Declined, Cancelled);
        }

        // states:

        public boolean isDraft() {
            return draft().contains(this);
        }

        public boolean isCurrent() {
            return current().contains(this);
        }

        public boolean isProcessed() {
            return processed().contains(this);
        }

        public static boolean isOnlineApplication(LeaseApplication app) {
            assert (!app.onlineApplication().isValueDetached());
            return (!app.onlineApplication().status().isNull() && app.onlineApplication().status().getValue().isInProgress());
        }
    }

    @Owner
    @Detached
    @JoinColumn
    Lease lease();

    @Owned(cascade = {})
    MasterOnlineApplication onlineApplication();

    @NotNull
    @ReadOnly
    @ToString
    @Length(14)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @MemberColumn(sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> applicationId();

    @Editor(type = EditorType.label)
    IPrimitive<String> yardiApplicationId();

    @ToString(index = 0)
    IPrimitive<Status> status();

    /**
     * no
     * if empty - application has been created by prospect
     */
    Employee createdBy();

    interface DecisionInfo extends IEntity {

        @Editor(type = EditorType.label)
        Employee decidedBy();

        @Editor(type = EditorType.label)
        IPrimitive<LogicalDate> decisionDate();

        @Editor(type = EditorType.textarea)
        IPrimitive<String> decisionReason();
    }

    @EmbeddedEntity
    DecisionInfo submission();

    @EmbeddedEntity
    DecisionInfo validation();

    @EmbeddedEntity
    DecisionInfo approval();
}
