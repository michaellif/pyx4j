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
package com.propertyvista.portal.server.portal.prospect;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.DigitalSignature;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.server.portal.prospect.ProspectApplicationContext;

public class DigitalSignatureMgr {

    static public void update() {
        update(ProspectApplicationContext.retrieveCurrentUserApplication());
    }

    static public void update(OnlineApplication application) {
        Persistence.service().retrieve(application.masterOnlineApplication().leaseApplication().lease());
        Persistence.service().retrieve(application.masterOnlineApplication().leaseApplication().lease().currentTerm().version().tenants());
        update(application, application.masterOnlineApplication().leaseApplication().lease().currentTerm().version().tenants());
    }

    static public void update(OnlineApplication application, IList<LeaseTermTenant> tenants) {
        List<DigitalSignature> existingSignatures = new Vector<DigitalSignature>(application.signatures());
        application.signatures().clear();

        // check/create signature for every tenant which needs it: 
        for (LeaseTermTenant tenant : tenants) {
            if (ApplicationProgressMgr.shouldEnterInformation(tenant)) {
                boolean isExist = false;
                for (Iterator<DigitalSignature> it = existingSignatures.iterator(); it.hasNext();) {
                    DigitalSignature sig = it.next();
                    if (sig.person().equals(tenant.leaseParticipant().customer())) {
                        isExist = true;
                        application.signatures().add(sig);
                        it.remove();
                        break;
                    }
                }

                if (!isExist) { // create signature if absent: 
                    createDigitalSignature(application, tenant.leaseParticipant().customer());
                    ApplicationProgressMgr.invalidateSummaryStep(application);
                }
            }
        }

        Persistence.service().merge(application);
    }

    static public void update(OnlineApplication application, Customer customer) {
        boolean isExist = false;
        for (Iterator<DigitalSignature> it = application.signatures().iterator(); it.hasNext();) {
            DigitalSignature sig = it.next();
            if (sig.person().equals(customer)) {
                isExist = true;
                break;
            }
        }

        if (!isExist) { // create signature if absent: 
            createDigitalSignature(application, customer);
            ApplicationProgressMgr.invalidateSummaryStep(application);
            Persistence.service().merge(application);
        }
    }

    static public void resetAll() {
        resetAll(ProspectApplicationContext.retrieveCurrentUserApplication());
    }

    static public void resetAll(OnlineApplication application) {
        application.signatures().clear();
        update(application);
    }

    static public void reset(Customer person) {
        reset(ProspectApplicationContext.retrieveCurrentUserApplication(), person);
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

    static private DigitalSignature createDigitalSignature(OnlineApplication application, Customer person) {
        DigitalSignature sig = EntityFactory.create(DigitalSignature.class);
        sig.person().set(person);
        application.signatures().add(sig);
        return sig;
    }

}
