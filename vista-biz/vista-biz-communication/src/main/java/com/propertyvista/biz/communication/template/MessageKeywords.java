/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2014
 * @author ernestog
 */
package com.propertyvista.biz.communication.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

public class MessageKeywords {

    private static final Logger log = LoggerFactory.getLogger(MessageKeywords.class);

    private final static Map<Class<? extends IEntity>, String> entityKeywords;

    static {
        entityKeywords = new HashedMap<Class<? extends IEntity>, String>();
        entityKeywords.put(Building.class, "B");
        entityKeywords.put(Lease.class, "L");
        entityKeywords.put(AutopayAgreement.class, "AP");
        entityKeywords.put(PaymentRecord.class, "PR");
        entityKeywords.put(LeaseParticipant.class, "LP");
    }

    private MessageKeywords() {
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity> String getAbbr(E entity) {
        Class<E> clazz = (Class<E>) entity.getEntityMeta().getEntityClass();

        if (entityKeywords.containsKey(clazz)) {
            return entityKeywords.get(clazz);
        }

        return "";
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity> void addToKeywords(MailMessage email, E... entities) {
        for (E entityElem : entities) {
            addToKeywords(email, entityElem);
        }
    }

    public static <E extends IEntity> void addToKeywords(MailMessage email, E entity) {
        if (entity == null) {
            return;
        }
        try {
            List<String> keywords = new ArrayList<String>();
            String prefix = getAbbr(entity);

// Building
            if (entity instanceof Building) {
                keywords.add(prefix + ((Building) entity).propertyCode().getStringView());

// Lease
            } else if (entity instanceof Lease) {
                keywords.add(prefix + ((Lease) entity).getPrimaryKey());
                keywords.add(prefix + ((Lease) entity).leaseId().getStringView());

// AutopayAgreement
            } else if (entity instanceof AutopayAgreement) {
                keywords.add(prefix + ((AutopayAgreement) entity).getPrimaryKey());

// EmailTemplate
            } else if (entity instanceof EmailTemplate) {
                keywords.add(prefix + ((EmailTemplate) entity).templateType().getStringView());

// PaymentRecord
            } else if (entity instanceof PaymentRecord) {
                keywords.add(prefix + ((PaymentRecord) entity).getPrimaryKey());

// LeaseParticipant
            } else if (entity instanceof LeaseParticipant) {
                keywords.add(prefix + ((LeaseParticipant<?>) entity).id().getStringView());
                keywords.add(prefix + ((LeaseParticipant<?>) entity).participantId().getStringView());

// Default
            } else {
                keywords.add(prefix + entity.getPrimaryKey());
            }

            email.addKeywords(keywords);
        } catch (Throwable e) {
            log.error("(TODO report a bug): MessageKeywords infrastructure failed", e);
        }

    }
}
