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
 * @version $Id$
 */
package com.propertyvista.biz.communication.mail.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

public class MessageKeywords {

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
    public static <E extends IEntity> void addToKeywords(MailMessage email, E... entity) {
        List<E> entities = Arrays.asList(entity);
        for (E entityElem : entities) {
            addToKeywords(email, entityElem);
        }
    }

    public static <E extends IEntity> void addToKeywords(MailMessage email, E entity) {
        if (entity == null)
            return;

        List<String> keywords = new ArrayList<String>();
        String keyword = getAbbr(entity);

// Building
        if (entity instanceof Building) {
            keyword += ((Building) entity).propertyCode().getStringView();
            keywords.add(keyword);

// Lease
        } else if (entity instanceof Lease) {
            keyword += ((Lease) entity).id().getStringView();
            keywords.add(keyword);

            keyword = ((Lease) entity).leaseId().getStringView();
            keywords.add(keyword);

// AutopayAgreement
        } else if (entity instanceof AutopayAgreement) {
            keyword += ((AutopayAgreement) entity).id().getStringView();
            keywords.add(keyword);

// EmailTemplate
        } else if (entity instanceof EmailTemplate) {
            keyword += ((EmailTemplate) entity).type().getStringView();
            keywords.add(keyword);

// PaymentRecord
        } else if (entity instanceof PaymentRecord) {
            keyword += ((PaymentRecord) entity).id().getStringView();
            keywords.add(keyword);

// LeaseParticipant
        } else if (entity instanceof LeaseParticipant) {
            keyword += ((PaymentRecord) entity).id().getStringView();
            keywords.add(keyword);

// Default
        } else {
            keyword += entity.getPrimaryKey().toString();
            keywords.add(keyword);
        }

        email.addKeywords(keywords.toArray(new String[keywords.size()]));
    }
}
