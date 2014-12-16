/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 31, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.config.VistaLocale;
import com.propertyvista.domain.policy.policies.LegalQuestionsPolicy;
import com.propertyvista.domain.policy.policies.domain.LegalQuestionsPolicyItem;
import com.propertyvista.shared.i18n.CompiledLocale;

public class LegalQuestionsPolicyPreloader extends AbstractPolicyPreloader<LegalQuestionsPolicy> {

    public LegalQuestionsPolicyPreloader() {
        super(LegalQuestionsPolicy.class);
    }

    @Override
    protected LegalQuestionsPolicy createPolicy(StringBuilder log) {
        LegalQuestionsPolicy policy = EntityFactory.create(LegalQuestionsPolicy.class);

        // get English locale
        CompiledLocale eng = VistaLocale.toCompiledLocale(VistaLocale.getPmcDefaultEnglishLocale());
        if (eng != null) {
            policy.enabled().setValue(true);

            LegalQuestionsPolicyItem item = policy.questions().$();
            item.locale().setValue(eng);
            item.question().setValue("Have you ever been sued for rent?");
            policy.questions().add(item);

            item = policy.questions().$();
            item.locale().setValue(eng);
            item.question().setValue("Have you ever been sued for damages?");
            policy.questions().add(item);

            item = policy.questions().$();
            item.locale().setValue(eng);
            item.question().setValue("Have you ever been evicted?");
            policy.questions().add(item);

            item = policy.questions().$();
            item.locale().setValue(eng);
            item.question().setValue("Have you ever defaulted on a lease?");
            policy.questions().add(item);

            item = policy.questions().$();
            item.locale().setValue(eng);
            item.question()
                    .setValue(
                            "Have you ever been convicted of a crime/felony that involved an offense against property, persons, government officials, or that involved firearms, illegal drugs, or sex or sex crimes?");
            policy.questions().add(item);

            item = policy.questions().$();
            item.locale().setValue(eng);
            item.question().setValue("Have you ever had any liens, court judgments or repossessions?");
            policy.questions().add(item);

            item = policy.questions().$();
            item.locale().setValue(eng);
            item.question().setValue("Have you ever filed for bankruptcy protection?");
            policy.questions().add(item);

        } else {
            policy.enabled().setValue(false);
        }

        return policy;
    }
}
