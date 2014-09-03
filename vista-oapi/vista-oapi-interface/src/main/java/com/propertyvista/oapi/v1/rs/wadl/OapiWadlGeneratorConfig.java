/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 2, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.rs.wadl;

import java.util.List;

import org.glassfish.jersey.server.wadl.config.WadlGeneratorConfig;
import org.glassfish.jersey.server.wadl.config.WadlGeneratorDescription;
import org.glassfish.jersey.server.wadl.internal.generators.WadlGeneratorApplicationDoc;

public class OapiWadlGeneratorConfig extends WadlGeneratorConfig {

    @Override
    public List<WadlGeneratorDescription> configure() {

        return generator(WadlGeneratorApplicationDoc.class)//
                .prop("applicationDocsStream", "application-doc.xml")//
//                .generator(WadlGeneratorGrammarsSupport.class)//                
//                .prop("grammarsStream", "application-grammars.xml")//
//                .generator(WadlGeneratorResourceDocSupport.class)//
//                .prop("resourceDocStream", "resourcedoc.xml")//
                .descriptions();

    }

}