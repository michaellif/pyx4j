/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 9, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.util;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonScreeningHolder;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.ptapp.DigitalSignature;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class DigitalSignatureMgr {

    static public void update() {
        update(PtAppContext.getCurrentUserApplication());
    }

    static public void update(OnlineApplication application) {
        Persistence.service().retrieve(application.lease());
        Persistence.service().retrieve(application.lease().version().tenants());
        update(application, application.lease().version().tenants());
    }

    static public void update(OnlineApplication application, IList<Tenant> tenants) {
        List<DigitalSignature> existingSignatures = new Vector<DigitalSignature>(application.signatures());
        application.signatures().clear();

        // check/create signature for every tenant which needs it: 
        for (Tenant tenant : tenants) {
            if (ApplicationProgressMgr.shouldEnterInformation(tenant)) {
                boolean isExist = false;
                for (Iterator<DigitalSignature> it = existingSignatures.iterator(); it.hasNext();) {
                    DigitalSignature sig = it.next();
                    if (sig.person().equals(tenant.customer())) {
                        isExist = true;
                        application.signatures().add(sig);
                        it.remove();
                        break;
                    }
                }

                if (!isExist) { // create signature if absent: 
                    createDigitalSignature(application, tenant.customer());
                    ApplicationProgressMgr.invalidateSummaryStep(application);
                }
            }
        }

        Persistence.service().merge(application);
    }

    static public void update(OnlineApplication application, PersonScreeningHolder person) {
        boolean isExist = false;
        for (Iterator<DigitalSignature> it = application.signatures().iterator(); it.hasNext();) {
            DigitalSignature sig = it.next();
            if (sig.person().equals(person)) {
                isExist = true;
                break;
            }
        }

        if (!isExist) { // create signature if absent: 
            createDigitalSignature(application, person);
            ApplicationProgressMgr.invalidateSummaryStep(application);
            Persistence.service().merge(application);
        }
    }

    static public void resetAll() {
        resetAll(PtAppContext.getCurrentUserApplication());
    }

    static public void resetAll(OnlineApplication application) {
        application.signatures().clear();
        update(application);
    }

    static public void reset(Customer person) {
        reset(PtAppContext.getCurrentUserApplication(), person);
    }

    static public void reset(OnlineApplication application, Customer person) {
        for (Iterator<DigitalSignature> it = application.signatures().iterator(); it.hasNext();) {
            DigitalSignature sig = it.next();
            if (sig.person().equals(person)) {
                it.remove();
                break;
            }
        }

        createDigitalSignature(application, person);
        ApplicationProgressMgr.invalidateSummaryStep(application);

        Persistence.service().merge(application);
    }

    static private DigitalSignature createDigitalSignature(OnlineApplication application, PersonScreeningHolder person) {
        DigitalSignature sig = EntityFactory.create(DigitalSignature.class);
        sig.person().set(person);
        application.signatures().add(sig);
        return sig;
    }

}
