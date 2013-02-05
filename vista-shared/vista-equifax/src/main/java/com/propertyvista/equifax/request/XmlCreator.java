/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.equifax.request;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.equifax.uat.from.EfxTransmit;
import ca.equifax.uat.to.CNConsAndCommRequestType;
import ca.equifax.uat.to.CNOutputParametersType.OutputParameter;
import ca.equifax.uat.to.CNOutputParametersType.OutputParameter.GenericOutputCode;
import ca.equifax.uat.to.ObjectFactory;
import ca.equifax.uat.to.ParameterType.Value;
import ca.equifax.uat.to.ScoringProductType;
import ca.equifax.uat.to.ScoringProductType.Parameters;
import ca.equifax.uat.to.ScoringProductType.Parameters.Parameter;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.equifax.model.EquifaxParameter;

public class XmlCreator {

    private final static Logger log = LoggerFactory.getLogger(XmlCreator.class);

    private static ObjectFactory factory = new ObjectFactory();

    public static void addParameter(EquifaxParameter ep, Parameters parameters) {
        parameters.getParameter().add(createParameter(ep));
    }

    public static Parameter createParameter(EquifaxParameter ep) {
        Parameter parameter = factory.createScoringProductTypeParametersParameter();

        parameter.setId(ep.getId());
        Value value = factory.createParameterTypeValue();
        value.setValue(ep.getValue());
        parameter.setValue(value);

        return parameter;
    }

    public static ScoringProductType createScoringProduct() {
        ScoringProductType scoringProduct = factory.createScoringProductType();

        // type
//        Possible values are :
//         MODL  = Decision Power model
//         RISK     = Risk
//         CARD   =  ScoreCard
//         MDAD  = Advanced Decisionning
        scoringProduct.setProductType("MODL");

        //10301 for iDecision Power Consumer
        scoringProduct.setScoringNumber(EquifaxConsts.scoringProductId_iDecisionPower);
        scoringProduct.setReportAttribute("N");

        return scoringProduct;
    }

    public static OutputParameter createOutputParameter() {
        OutputParameter outputParameter = factory.createCNOutputParametersTypeOutputParameter();

        // Type: CONS - Consumer, COMM - Commercial, COMB - Combined
        outputParameter.setOutputParameterType("CONS");

        // X = is probably XML (VladS)
        //        02= Full-File Fixed Output
        //        06= Print Image Output (81 char per line)
        //        07= Print Image Output (80 char per line/V4.1 and greater)
        //        09= National Credit File
        //        0B= Combination of Print Image & FFF Output with Plain Language
        //        0C= Full-File Fixed Human/Machine Readable Output
        //        0E= Full-File Fixed Output with Plain Language
        //        0F= FFF H/R Output with Plain Language
        //        0G= Full-File Fixed Output with Multiple Files
        //        0H= FFF Output with Plain Language and Multiple Files
        //        0I= FFF H/R Output with Multiple Files
        //        0J= FFF H/R Output with P/L and Multiple Files
        //        0K= Print Image Output with Multiple Files (81 char per line)
        //        0L= Print Image Output with Multiple Files (80 char per line/V4.1 and greater)
        //        0M= Full-File Fixed Human/Machine Readable Output with Multiple Files and Print Image Output with Multiple Files (V4.1 and greater)
        //        0N= National Credit File with Multiple Files
        //        0P= Service 222 (ID portion only - V4.1 and greater)
        //        0R= National Credit File (Equifax Direct)
        //        0T= Service 222 Print Image Format (packaging)
        GenericOutputCode genCode = factory.createCNOutputParametersTypeOutputParameterGenericOutputCode();
        genCode.setValue("X");
        outputParameter.setGenericOutputCode(genCode);

        if (false) {
            outputParameter.setCustomizationCode("VVVVVVVV");
        }
        return outputParameter;
    }

    public static String devToXMl(CNConsAndCommRequestType requestMessage) {
        try {
            QName qname = new QName("http://www.equifax.ca/XMLSchemas/CustToEfx", "CNCustTransmitToEfx");
            JAXBElement<CNConsAndCommRequestType> element = new JAXBElement<CNConsAndCommRequestType>(qname, CNConsAndCommRequestType.class, requestMessage);

            JAXBContext context = JAXBContext.newInstance(CNConsAndCommRequestType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter xml = new StringWriter();
            m.marshal(element, xml);
            return xml.toString();
        } catch (Throwable e) {
            log.error("to XML Error", e);
            return e.getMessage();
        }
    }

    public static String devToXMl(EfxTransmit efxResponse) {
        try {
            QName qname = new QName("http://www.equifax.ca/XMLSchemas/EfxToCust", "CNEfxTransmitToCust");
            JAXBElement<EfxTransmit> element = new JAXBElement<EfxTransmit>(qname, EfxTransmit.class, efxResponse);

            JAXBContext context = JAXBContext.newInstance(EfxTransmit.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter xml = new StringWriter();
            m.marshal(element, xml);
            return xml.toString();
        } catch (Throwable e) {
            log.error("to XML Error", e);
            return e.getMessage();
        }
    }

    // TODO VladS, why doesn't this work?

    public static String toStorageXMl1(EfxTransmit efxResponse) {
        try {
            QName qname = new QName("http://www.equifax.ca/XMLSchemas/EfxToCust", "CNEfxTransmitToCust");
            JAXBElement<EfxTransmit> element = new JAXBElement<EfxTransmit>(qname, EfxTransmit.class, efxResponse);

            JAXBContext context = JAXBContext.newInstance(EfxTransmit.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            StringWriter xml = new StringWriter();
            m.marshal(element, xml);
            return xml.toString();
        } catch (JAXBException e) {
            throw new Error(e);
        }
    }

    public static String toStorageXMl(EfxTransmit efxResponse) {
        try {
            return MarshallUtil.marshall(efxResponse);
        } catch (JAXBException e) {
            throw new Error(e);
        }
    }

    public static EfxTransmit fromStorageXMl(String xml) {
        try {
            return MarshallUtil.unmarshal(EfxTransmit.class, xml);
        } catch (JAXBException e) {
            throw new Error(e);
        }
    }
}
