/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.ci;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.xml.XMLStringWriter;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.server.jobs.TaskRunner;

class EnvLinksBuilder extends XMLStringWriter {

    EnvLinksBuilder() {
        startIdented("table", params("border", "1"));
        startIdented("tbody");

        startIdented("tr");
        th("PMC");
        th("App");
        th("Url");
        endIdented();

        {
            startIdented("tr");
            td("");
            td("Onboarding");

            startIdented("td");
            a(VistaDeployment.getBaseApplicationURL(VistaApplication.onboarding, true));
            endIdented();
            endIdented();
        }

        {
            startIdented("tr");
            td("");
            td("Operations");

            startIdented("td");
            a(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true));
            endIdented();
            endIdented();
        }

        {
            startIdented("tr");
            td("");
            td("DB Reset");

            startIdented("td");
            a("http://static" + ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).getApplicationURLNamespace(true) + "o/db-reset");
            endIdented();
            endIdented();
        }

        tr_hr();

        List<Pmc> pmcs = TaskRunner.runInOperationsNamespace(new Callable<List<Pmc>>() {
            @Override
            public List<Pmc> call() {
                EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                criteria.eq(criteria.proto().status(), Pmc.PmcStatus.Active);
                criteria.asc(criteria.proto().dnsName());
                return Persistence.service().query(criteria);
            }
        });

        for (Pmc pmc : pmcs) {
            writePmc(pmc);
        }

        endIdented();
        endIdented();
    }

    private void writePmc(Pmc pmc) {
        writeApp(pmc, pmc.namespace().getValue(), "CRM", VistaApplication.crm);
        if (false) { // TODO enable filed in future
            writeApp(pmc, null, "Field", VistaApplication.field);
        }
        writeApp(pmc, null, "Site", VistaApplication.site);
        writeApp(pmc, null, "Resident", VistaApplication.resident);
        writeApp(pmc, null, "Prospect", VistaApplication.prospect);

        tr_hr();
    }

    private void tr_hr() {
        startIdented("tr");
        startIdented("td", params("colspan", "3"));
        writeEmpty("hr", null);
        endIdented();
        endIdented();
    }

    private void writeApp(Pmc pmc, String c1, String name, VistaApplication application) {
        startIdented("tr");
        td(c1);

        td(name);

        startIdented("td");
        a(VistaDeployment.getBaseApplicationURL(pmc, application, true));
        endIdented();
        endIdented();
    }

    private void a(String url) {
        Map<String, String> params = params("href", url);
        params.put("target", "lh_" + url.replaceAll("[\\-\\./:]", "_"));
        startIdented("a", params);
        append(url);
        endIdented();
    }

    private void th(String string) {
        write("th", params("align", "left"), string);
    }

    private void td(String string) {
        write("td", string);
    }

    private static Map<String, String> params(String name, String value) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(name, value);
        return params;
    }

}