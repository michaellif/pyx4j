/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-24
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.prospect;

import java.io.Serializable;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface ApplicationStepDescriptor extends IEntity {

    public static final com.pyx4j.i18n.shared.I18n i18n = com.pyx4j.i18n.shared.I18n.get(ApplicationStepDescriptor.class);

    public static enum StepId implements Serializable {

        //To be presented to co-tenant and guarantor with description of lease including unit, options and people
        lease(i18n.tr("Lease Info")),

        //To be presented to main applicant for unit selection
        unit(i18n.tr("Unit Selection")),

        //For term and features selection
        options(i18n.tr("Lease Options")),

        personalInfoA(i18n.tr("About You")),

        personalInfoB(i18n.tr("Additional Information")),

        financial(i18n.tr("Financial")),

        people(i18n.tr("People")),

        contacts(i18n.tr("Contacts")),

        pmcCustom(i18n.tr("PMC Custom")),

        summary(i18n.tr("Summary")),

        payment(i18n.tr("Payment"));

        private final String caption;

        private StepId(String caption) {
            this.caption = caption;
        }

        public String getCaption() {
            return caption;
        }
    }

    public enum Status implements Serializable {

        notVisited, visited, complete, invalid

    }

    IPrimitive<StepId> stepId();

    IPrimitive<Status> status();

}
