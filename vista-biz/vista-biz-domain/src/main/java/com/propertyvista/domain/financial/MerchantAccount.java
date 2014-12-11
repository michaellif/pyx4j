/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.property.asset.building.Building;

@ToStringFormat("{1}-{0}: {2} {3}")
@DiscriminatorValue("MerchantAccount")
@SecurityEnabled
public interface MerchantAccount extends AbstractMerchantAccount, HasNotesAndAttachments {

    @EmbeddedEntity
    public interface MerchantElectronicPaymentSetup extends IEntity {

        IPrimitive<Boolean> acceptedEcheck();

        IPrimitive<Boolean> acceptedDirectBanking();

        IPrimitive<Boolean> acceptedCreditCard();

        IPrimitive<Boolean> acceptedCreditCardConvenienceFee();

        IPrimitive<Boolean> acceptedCreditCardVisaDebit();

        IPrimitive<Boolean> acceptedInterac();
    }

    @I18n
    enum MerchantAccountActivationStatus {

        PendindAppoval,

        Rejected,

        PendindAcknowledgement,

        Active,

        Suspended,

        Cancelled;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @I18n
    enum MerchantAccountPaymentsStatus {

        ElectronicPaymentsAllowed,

        NoElectronicPaymentsAllowed,

        Invalid;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @Length(8)
    @RpcTransient
    IPrimitive<String> merchantTerminalIdConvenienceFee();

    IPrimitive<MerchantAccountActivationStatus> status();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> operationsNotes();

    @ToString
    @Caption(description = "Internal account name, visible only in CRM")
    IPrimitive<String> accountName();

    /**
     * Calculated base on terminal_id before sending it to GWT
     */
    @Transient
    @Editor(type = Editor.EditorType.label)
    IPrimitive<MerchantAccountPaymentsStatus> paymentsStatus();

    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> invalid();

    MerchantElectronicPaymentSetup setup();

    @ReadOnly
    @Format("yyyy-MM-dd HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Format("yyyy-MM-dd HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @JoinTable(value = BuildingMerchantAccount.class)
    @Detached(level = AttachLevel.Detached)
    ISet<Building> _buildings();
}
