/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ISignature.SignatureType;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.policies.OnlineApplicationPolicy;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTabTitle;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTerm;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTermContent;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;
import com.propertyvista.shared.i18n.CompiledLocale;

public class MockupOnlineApplicationPolicyPreloader extends AbstractPolicyPreloader<OnlineApplicationPolicy> {

    private final static I18n i18n = I18n.get(MockupOnlineApplicationPolicyPreloader.class);

    private AvailableLocale defaultLocale;

    public MockupOnlineApplicationPolicyPreloader() {
        super(OnlineApplicationPolicy.class);
    }

    @Override
    protected OnlineApplicationPolicy createPolicy(StringBuilder log) {
        OnlineApplicationPolicy policy = EntityFactory.create(OnlineApplicationPolicy.class);

        // add Legal Tab title
        OnlineApplicationLegalTabTitle tabTitle = EntityFactory.create(OnlineApplicationLegalTabTitle.class);
        tabTitle.locale().set(getDefaultLocale());
        tabTitle.title().setValue(i18n.tr("Legal"));
        policy.customLegalTabTitle().add(tabTitle);

        // add legal terms
        policy.terms().add(randomTerm());
        policy.terms().add(randomTerm());

        return policy;
    }

    private OnlineApplicationLegalTerm randomTerm() {
        OnlineApplicationLegalTerm term = EntityFactory.create(OnlineApplicationLegalTerm.class);
        term.signatureType().setValue(RandomUtil.randomEnum(SignatureType.class));

        OnlineApplicationLegalTermContent content = EntityFactory.create(OnlineApplicationLegalTermContent.class);
        content.locale().set(getDefaultLocale());
        content.title().setValue(CommonsGenerator.lipsumShort());
        content.body().setValue(CommonsGenerator.lipsum());
        term.content().add(content);

        return term;
    }

    private AvailableLocale getDefaultLocale() {
        if (defaultLocale == null) {
            EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lang(), CompiledLocale.en));
            defaultLocale = Persistence.service().retrieve(criteria);
        }
        return defaultLocale;
    }
}
