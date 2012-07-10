/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.essentials.server.xml.XMLStringWriter;

import com.propertyvista.admin.server.onboarding.OnboardingXMLUtils;
import com.propertyvista.admin.server.onboarding.rh.OnboardingRequestHandlerFactory;
import com.propertyvista.admin.server.onboarding.rhf.RequestHandler;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityNamingConvention;

public class OnboardingXMLBindingTest {

    private final static Logger log = LoggerFactory.getLogger(OnboardingXMLBindingTest.class);

    @Test
    public void testRequestsParsing() {
        List<String> allClasses = EntityClassFinder.getEntityClassesNames();
        for (String className : allClasses) {
            Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
            if (!RequestIO.class.isAssignableFrom(entityClass) || (entityClass == RequestIO.class)) {
                continue;
            }

            if (entityClass.getAnnotation(Deprecated.class) != null) {
                log.debug("ignore not implemented classes {}", entityClass);
                continue;
            }

            log.debug("testing class {}", entityClass);

            RequestIO request = (RequestIO) EntityFactory.create(entityClass);
            RequestMessageIO r = EntityFactory.create(RequestMessageIO.class);
            r.requests().add(request);

            // Test the XML deserialization
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityNamingConvention());
            xmlWriter.setEmitId(false);
            xmlWriter.write(r);

            RequestMessageIO requestMessage = OnboardingXMLUtils.parse(RequestMessageIO.class, new InputSource(new StringReader(xml.toString())));

            log.debug("test request message {}", requestMessage);

            // Test handlers binding
            RequestHandler<RequestIO> requestHandler = new OnboardingRequestHandlerFactory().createRequestHandler(request);
            if (requestHandler == null) {
                throw new Error("RequestHandler nod bound for " + entityClass);
            }
        }
    }
}
