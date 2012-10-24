/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ApplicationVersion;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.pmc.ReservedPmcNames;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.server.onboarding.PmcNameValidator;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.portal.server.preloader.PmcCreator;

public class PmcFacadeImpl implements PmcFacade {

    @Override
    public void create(Pmc pmc) {
        pmc.status().setValue(PmcStatus.Created);
        pmc.schemaVersion().setValue(ApplicationVersion.getProductVersion());
        Persistence.service().persist(pmc);
    }

    @Override
    public boolean isOnboardingEnabled(Pmc pmc) {
        return EnumSet.of(PmcStatus.Created, PmcStatus.Active, PmcStatus.Suspended).contains(pmc.status().getValue());
    }

    @Override
    public void cancelPmc(Pmc pmc) {
        if (pmc.status().getValue() == PmcStatus.Created) {
            remove(pmc);
        } else {
            pmc.status().setValue(PmcStatus.Cancelled);
            pmc.termination().setValue(DateUtils.monthAdd(new Date(), 1));
            Persistence.service().persist(pmc);
        }
    }

    private void remove(Pmc pmc) {
        EntityQueryCriteria<OnboardingUserCredential> criteria = EntityQueryCriteria.create(OnboardingUserCredential.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), pmc));
        for (OnboardingUserCredential credential : Persistence.service().query(criteria)) {
            Persistence.service().delete(credential);
            Persistence.service().delete(credential.user());
        }

        Persistence.service().delete(pmc);
    }

    @Override
    public void terminateCancelledPmc(Pmc pmcId) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, pmcId.getPrimaryKey());

        pmc.status().setValue(PmcStatus.Terminated);

        //TODO unreserve names
        if (false) {
            pmc.namespace().setValue("__" + pmc.getPrimaryKey());
            pmc.dnsName().setValue("__" + pmc.getPrimaryKey());
        }

        //remove all values
        pmc.name().setValue(null);
        pmc.dnsNameAliases().clear();
        pmc.features().set(null);
        pmc.equifaxInfo().set(null);
        pmc.paymentTypeInfo().set(null);

        Persistence.service().persist(pmc);

        Persistence.service().retrieveMember(pmc.merchantAccounts());
        for (OnboardingMerchantAccount merchantAccount : pmc.merchantAccounts()) {
            merchantAccount.bankId().setValue(null);
            merchantAccount.branchTransitNumber().setValue(null);
            merchantAccount.accountNumber().setValue(null);
            merchantAccount.chargeDescription().setValue(null);
            merchantAccount.merchantTerminalId().setValue(null);
            Persistence.service().persist(merchantAccount);
        }

        Persistence.service().commit();
    }

    @Override
    public boolean reservedDnsName(String dnsName, String onboardingAccountId) {
        if (!PmcNameValidator.canCreatePmcName(dnsName, onboardingAccountId)) {
            return false;
        } else {
            EntityQueryCriteria<ReservedPmcNames> criteria = EntityQueryCriteria.create(ReservedPmcNames.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().onboardingAccountId(), onboardingAccountId));
            ReservedPmcNames prevReservation = Persistence.service().retrieve(criteria);
            if (prevReservation != null) {
                Persistence.service().delete(prevReservation);
            }

            ReservedPmcNames resDnsName = EntityFactory.create(ReservedPmcNames.class);
            resDnsName.dnsName().setValue(dnsName.toLowerCase(Locale.ENGLISH));
            resDnsName.onboardingAccountId().setValue(onboardingAccountId);
            Persistence.service().persist(resDnsName);
            return true;
        }
    }

    @Override
    public void activatePmc(Pmc pmcId) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, pmcId.getPrimaryKey());
        // First time create preload
        if (pmc.status().getValue() == PmcStatus.Created) {
            EntityQueryCriteria<OnboardingUserCredential> credentialCrt = EntityQueryCriteria.create(OnboardingUserCredential.class);
            credentialCrt.add(PropertyCriterion.eq(credentialCrt.proto().pmc(), pmc));
            List<OnboardingUserCredential> creds = Persistence.service().query(credentialCrt);

            if (creds.size() == 0) {
                throw new UserRuntimeException("No users for PMC " + pmc.name().getValue());
            }

            OnboardingUserCredential onbUserCred = creds.get(0);

            OnboardingUser onbUser = Persistence.service().retrieve(OnboardingUser.class, onbUserCred.user().getPrimaryKey());

            List<OnboardingMerchantAccount> onbMrchAccs;
            if (pmc.onboardingAccountId().getValue() != null) {
                EntityQueryCriteria<OnboardingMerchantAccount> onbMrchAccCrt = EntityQueryCriteria.create(OnboardingMerchantAccount.class);
                onbMrchAccCrt.add(PropertyCriterion.eq(onbMrchAccCrt.proto().onboardingAccountId(), pmc.onboardingAccountId().getValue()));
                onbMrchAccs = Persistence.service().query(onbMrchAccCrt);
            } else {
                onbMrchAccs = new ArrayList<OnboardingMerchantAccount>();
            }
            try {
                Persistence.service().startBackgroundProcessTransaction();
                PmcCreator.preloadPmc(pmc, onbUser, onbUserCred, onbMrchAccs);
                pmc.status().setValue(PmcStatus.Active);
                Persistence.service().persist(pmc);
                onbUserCred.behavior().setValue(VistaOnboardingBehavior.Client);
                Persistence.service().persist(onbUserCred);
                Persistence.service().persist(onbMrchAccs);

                Persistence.service().commit();
            } finally {
                Persistence.service().endTransaction();
            }
        } else {
            pmc.status().setValue(PmcStatus.Active);
            Persistence.service().persist(pmc);
            Persistence.service().commit();
        }
        CacheService.reset();
    }

}
