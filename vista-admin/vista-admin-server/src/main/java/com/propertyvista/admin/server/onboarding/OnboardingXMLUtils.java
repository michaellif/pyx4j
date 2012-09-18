/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.xml.XMLEntityParser;

import com.propertyvista.admin.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.onboarding.EquifaxReportType;
import com.propertyvista.onboarding.OnboardingRole;

public class OnboardingXMLUtils {

    public static <T extends IEntity> T parse(Class<T> entityClass, InputSource input) {
        XMLEntityParser parser = new XMLEntityParser(new OnboardingRequestXMLEntityFactory());
        return parser.parse(entityClass, newDocument(input).getDocumentElement());
    }

    public static Document newDocument(InputSource input) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
        builder.setErrorHandler(null);
        try {
            return builder.parse(input);
        } catch (SAXException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static OnboardingRole convertRole(VistaOnboardingBehavior behavior, boolean onboradingOnly) {
        if (behavior == null) {
            return null;
        }
        switch (behavior) {
        case OnboardingAdministrator:
            return OnboardingRole.OnboardingAdministrator;
        case Caledon:
            return OnboardingRole.Caledon;
        case Equifax:
            return OnboardingRole.Equifax;
        case Client:
            return OnboardingRole.Client;
        case ProspectiveClient:
            return OnboardingRole.ProspectiveClient;
        default:
            if (onboradingOnly) {
                throw new IllegalArgumentException();
            } else {
                return null;
            }
        }
    }

    public static PmcEquifaxInfo.EquifaxReportType convertEquifaxReportType(EquifaxReportType value) {
        if (value == null) {
            return null;
        }
        switch (value) {
        case LongReportForm:
            return PmcEquifaxInfo.EquifaxReportType.longReport;
        case ShortReportForm:
            return PmcEquifaxInfo.EquifaxReportType.shortReport;
        default:
            throw new IllegalArgumentException();
        }
    }
}
