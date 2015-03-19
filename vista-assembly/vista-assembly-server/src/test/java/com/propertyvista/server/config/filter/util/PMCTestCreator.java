/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 19, 2015
 * @author ernestog
 */
package com.propertyvista.server.config.filter.util;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.pmc.PmcDnsName.DnsNameTarget;

public class PMCTestCreator {

    private PMCTestCreator(PMCTestCreatorBuilder builder) {
        Pmc pmc = EntityFactory.create(Pmc.class);

        // Set NOT_NULL property values
//        pmc.name().setValue("nameSpaceFor" + builder.namespace);
        pmc.name().setValue(builder.namespace);
        pmc.dnsName().setValue(builder.namespace);
        pmc.namespace().setValue(builder.namespace);
        pmc.status().setValue(builder.status);

        if (builder.aliases != null && !builder.aliases.isEmpty()) {
            for (PmcDnsName dnsAlias : builder.aliases) {
                dnsAlias.pmc().set(pmc);
                pmc.dnsNameAliases().add(dnsAlias);
            }
        }

        Persistence.service().persist(pmc);
    }

    public static PMCTestCreatorBuilder createPMC(String namespace, PmcStatus status) {
        return new PMCTestCreatorBuilder(namespace, status);
    }

    public static class PMCTestCreatorBuilder {
        private String namespace;

        private PmcStatus status;

        private List<PmcDnsName> aliases;

        private PMCTestCreatorBuilder(String namespace, PmcStatus status) {
            this.namespace = namespace;
            this.status = status;
        }

        public PMCTestCreatorBuilder addDNSAlias(String alias, DnsNameTarget targetApp, boolean enabled) {
            if (aliases == null) {
                aliases = new ArrayList<>();
            }

            PmcDnsName dns = EntityFactory.create(PmcDnsName.class);
            dns.enabled().setValue(enabled);
            dns.target().setValue(targetApp);
            dns.dnsName().setValue(alias);

            aliases.add(dns);

            return this;
        }

        public PMCTestCreator save() {
            return new PMCTestCreator(this);
        }
    }

}