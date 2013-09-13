/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.ws;

import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.screening.Applicant;
import com.yardi.entity.screening.ApplicantScreening;
import com.yardi.entity.screening.Request;
import com.yardi.entity.screening.Response;
import com.yardi.ws.ServiceResponse;
import com.yardi.ws.ServiceResponse.ServiceResponseResult;
import com.yardi.ws.WSScreeningService;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.server.domain.CustomerCreditCheckReport;
import com.propertyvista.yardi.bean.Message;
import com.propertyvista.yardi.bean.Message.MessageType;
import com.propertyvista.yardi.bean.Messages;
import com.propertyvista.yardi.mapper.ApplicantScreeningMapper;

@WebService(endpointInterface = "com.yardi.ws.WSScreeningService")
public class WSScreeningServiceImpl implements WSScreeningService {

    private final static Logger log = LoggerFactory.getLogger(WSScreeningServiceImpl.class);

    private static I18n i18n = I18n.get(WSScreeningServiceImpl.class);

    private static final String ORIGINATOR_ID = "Yardi";

    private static final String USER_ACCOUNT = "123456";

    @Override
    public ServiceResponse getScreeningReport(String requestXml) {
        ServiceResponse response = null;
        log.info("GetScreeningReport operation processing...");

        try {
            if (StringUtils.isEmpty(requestXml)) {
                return createMessageResponse(MessageType.Error, i18n.tr("Invalid request: can not be empty or null"));
            }

            ApplicantScreening screeningRequest = MarshallUtil.unmarshal(ApplicantScreening.class, requestXml);
            validateRequest(screeningRequest);

            //return existing report for VIEW request
            if (isViewRequestType(screeningRequest.getRequest().getRequestType())) {
                CustomerCreditCheckReport report = getExistingReport(screeningRequest.getRequest().getReportID());

                log.info("GetScreeningReport returned existing report.");
                return createResponse(screeningRequest, report);
            }

            //TODO how can we create request for multiple applicants?
            Applicant mainApplicant = screeningRequest.getApplicant().get(0);
            Customer customer = new ApplicantScreeningMapper().map(mainApplicant);

            //run credit check

            log.info("GetScreeningReport returned new report.");
        } catch (YardiServiceException ye) {
            log.error("Errors during operation processing", ye);
            response = createMessageResponse(MessageType.Error, ye.getMessage());
        } catch (Throwable e) {
            log.error("Errors during operation processing", e);
            response = createMessageResponse(MessageType.Error, i18n.tr("Internal processing error."));
        }

        return response;
    }

    private CustomerCreditCheckReport getExistingReport(String reportId) throws YardiServiceException {
        EntityQueryCriteria<CustomerCreditCheckReport> criteria = EntityQueryCriteria.create(CustomerCreditCheckReport.class);
        criteria.eq(criteria.proto().id(), new Key(reportId));
        List<CustomerCreditCheckReport> reports = Persistence.service().query(criteria);
        if (reports.isEmpty()) {
            throw new YardiServiceException(i18n.tr("No reports found for reportID {0}", reportId));
        }
        return reports.get(0);
    }

    private ServiceResponse createResponse(ApplicantScreening screeningRequest, CustomerCreditCheckReport report) throws JAXBException {
        screeningRequest.setResponse(new Response());
        screeningRequest.getResponse().setReportDate(report.created().getValue());
        //screeningRequest.getResponse().setBackgroundReport(EquifaxEncryptedStorage.decrypt(report.data().getValue()));
        screeningRequest.getResponse().setStatus("Complete");

        String screeningResponse = MarshallUtil.marshall(screeningRequest);
        return createResponse(screeningResponse);
    }

    private ServiceResponse createMessageResponse(MessageType msgType, String msg) {
        try {
            Messages msgs = Messages.create(new Message(msgType, msg));
            String content = MarshallUtil.marshall(msgs);
            return createResponse(content);
        } catch (JAXBException e) {
            return createResponse(msg);
        }
    }

    private ServiceResponse createResponse(String content) {
        ServiceResponse response = new ServiceResponse();
        response.setServiceResponseResult(new ServiceResponseResult());
        response.getServiceResponseResult().getContent().add(content);
        return response;
    }

    private void validateRequest(ApplicantScreening screeningRequest) throws YardiServiceException {
        Request request = screeningRequest.getRequest();
        if (request == null) {
            throw new YardiServiceException(i18n.tr("Invalid request parameters: Request section missing"));
        }
        if (!ORIGINATOR_ID.equals(request.getOriginatorID())) {
            throw new YardiServiceException(i18n.tr("Invalid request parameters: OriginatorID"));
        }
        if (!USER_ACCOUNT.equals(request.getUserAccount())) {
            throw new YardiServiceException(i18n.tr("Invalid request parameters: UserAccount"));
        }
        if (!isValidRequestType(request.getRequestType())) {
            throw new YardiServiceException(i18n.tr("Invalid request parameters: RequestType is not specified"));
        }
        if (screeningRequest.getApplicant().isEmpty()) {
            throw new YardiServiceException(i18n.tr("Invalid request parameters: no applicants specified"));
        }

        Applicant applicant = screeningRequest.getApplicant().get(0);
        if (applicant.getASInformation() == null) {
            throw new YardiServiceException(i18n.tr("Invalid request parameters: ASInformation section missing"));
        }
        String sin = applicant.getASInformation().getSocSecNumber();
        if (StringUtils.isBlank(sin)) {
            throw new YardiServiceException(i18n.tr("Invalid request parameters: SocSecNumber is not specified"));
        }

    }

    private boolean isValidRequestType(String requestType) {
        return "New".equalsIgnoreCase(requestType) || isViewRequestType(requestType);
    }

    private boolean isViewRequestType(String requestType) {
        return "View".equalsIgnoreCase(requestType);
    }

}
