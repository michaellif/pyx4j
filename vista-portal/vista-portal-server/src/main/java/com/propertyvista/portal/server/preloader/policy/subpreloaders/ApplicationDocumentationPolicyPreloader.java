/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Type;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class ApplicationDocumentationPolicyPreloader extends AbstractPolicyPreloader<ApplicationDocumentationPolicy> {

    public ApplicationDocumentationPolicyPreloader() {
        super(ApplicationDocumentationPolicy.class);
    }

    private final static I18n i18n = I18n.get(ApplicationDocumentationPolicyPreloader.class);

    @Override
    protected ApplicationDocumentationPolicy createPolicy(StringBuilder log) {
        ApplicationDocumentationPolicy policy = EntityFactory.create(ApplicationDocumentationPolicy.class);
        policy.numberOfRequiredIDs().setValue(2);

        IdentificationDocumentType id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue(i18n.tr("SIN"));
        id.required().setValue(true);
        id.type().setValue(Type.canadianSIN);
        policy.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue(i18n.tr("Passport"));
        id.type().setValue(Type.passport);
        policy.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue(i18n.tr("Citizenship Card"));
        id.type().setValue(Type.citizenship);
        policy.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue(i18n.tr("Driver License"));
        id.type().setValue(Type.license);
        policy.allowedIDs().add(id);

        return policy;
    }
}
