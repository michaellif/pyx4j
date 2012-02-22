/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertvista.generator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.propertvista.generator.gdo.ApplicationSummaryGDO;
import com.propertvista.generator.gdo.TenantSummaryGDO;
import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.CompanyVendor;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lease.Lease;

public class TenantsGenerator {

    public TenantsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
    }

    public List<Tenant> createTenants(int num) {
        List<Tenant> items = new ArrayList<Tenant>();
        for (int i = 0; i < num; i++) {
            items.add(createTenant());
        }
        return items;
    }

    public Tenant createTenant() {
        Tenant item = EntityFactory.create(Tenant.class);

// TODO currently create just person tenants!         
        item.type().setValue(Tenant.Type.person);
//        item.type().setValue(RandomUtil.random(Tenant.Type.values()));
        switch (item.type().getValue()) {
        case person:
            item.person().set(CommonsGenerator.createPerson());
            break;
        case company:
            item.company().set(CompanyVendor.createCompany());
            break;
        }

        return item;
    }

    public ApplicationSummaryGDO createLease(Tenant tenant, AptUnit selectedUnit) {
        ApplicationSummaryGDO summary = EntityFactory.create(ApplicationSummaryGDO.class);

        // lease:
        summary.lease().leaseID().setValue(RandomUtil.randomLetters(8));

        // This is actually updated during save to match real unit data
        summary.lease().type().setValue(Service.Type.residentialUnit);
        summary.lease().status().setValue(Lease.Status.Active);
        summary.lease().unit().set(selectedUnit);
        summary.lease().leaseFrom().setValue(RandomUtil.randomLogicalDate(2010, 2011));
        summary.lease().leaseTo().setValue(RandomUtil.randomLogicalDate(2012, 2014));
        summary.lease().expectedMoveIn().setValue(summary.lease().leaseFrom().getValue());
        summary.lease().actualMoveIn().setValue(summary.lease().expectedMoveIn().getValue());
        summary.lease().approvalDate().setValue(summary.lease().leaseFrom().getValue());
        summary.lease().createDate().setValue(RandomUtil.randomLogicalDate(2010, 2010));

        if (RandomUtil.randomBoolean()) {
            LogicalDate date = new LogicalDate(summary.lease().leaseTo().getValue());
            date.setTime(date.getTime() - 31 * 24 * 60 * 60 * 1000L);
            summary.lease().moveOutNotice().setValue(date);

            date = new LogicalDate(summary.lease().leaseTo().getValue());
            date.setTime(date.getTime() - 3 * 24 * 60 * 60 * 1000L);
            summary.lease().expectedMoveOut().setValue(date);

            summary.lease().status().setValue(Lease.Status.OnNotice);
        }

        TenantSummaryGDO tenantSummary = EntityFactory.create(TenantSummaryGDO.class);
        summary.tenants().add(tenantSummary);
        tenantSummary.tenant().set(tenant);
        tenantSummary.tenantInLease().tenant().set(tenantSummary.tenant());
        tenantSummary.tenantInLease().role().setValue(TenantInLease.Role.Applicant);
        tenantSummary.tenantInLease().lease().set(summary.lease());

        return summary;
    }

    public List<PaymentMethod> createPaymentMethods(Tenant tenant) {
        List<PaymentMethod> l = new Vector<PaymentMethod>();

        for (int i = 0; i < 2; i++) {
            PaymentMethod m = EntityFactory.create(PaymentMethod.class);
            m.type().setValue(PaymentType.Visa);
            if (i == 0) {
                m.primary().setValue(Boolean.TRUE);
            }
            m.creditCard().numberRefference().setValue(CommonsStringUtils.d00(RandomUtil.randomInt(99)) + CommonsStringUtils.d00(RandomUtil.randomInt(99)));
            m.creditCard().name().setValue(tenant.person().name().getStringView());
            m.creditCard().expiryDate().setValue(RandomUtil.randomLogicalDate(2012, 2015));
            m.tenant().set(tenant);
            l.add(m);
        }

        return l;
    }

    public List<Lead> createLeads(int num) {
        List<Lead> items = new ArrayList<Lead>();
        for (int i = 0; i < num; i++) {
            items.add(createLead());
        }
        return items;
    }

    public Lead createLead() {
        Lead item = EntityFactory.create(Lead.class);
        item.createDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.status().setValue(Lead.Status.active);

        item.person().set(CommonsGenerator.createPerson());

        item.moveInDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.leaseTerm().setValue(RandomUtil.randomEnum(Lead.LeaseTerm.class));

        item.comments().setValue(CommonsGenerator.lipsum());
        item.refSource().setValue(RandomUtil.randomEnum(Lead.RefSource.class));

        item.appointmentDate1().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.appointmentTime1().setValue(RandomUtil.randomEnum(Lead.DayPart.class));

        item.appointmentDate2().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.appointmentTime2().setValue(RandomUtil.randomEnum(Lead.DayPart.class));

        return item;
    }

    public List<Appointment> createAppointments(int num) {
        List<Appointment> items = new ArrayList<Appointment>();
        for (int i = 0; i < num; ++i) {
            items.add(createAppointment());
        }
        return items;
    }

    public Appointment createAppointment() {
        Appointment item = EntityFactory.create(Appointment.class);

        item.date().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.time().setValue(RandomUtil.randomTime());
        item.address().setValue("Appointment address here...");
        item.phone().setValue(CommonsGenerator.createPhone());
        item.email().setValue(RandomUtil.randomLetters(5).toLowerCase() + "@sympatico.ca");
        item.status().setValue(RandomUtil.randomEnum(Appointment.Status.class));

        return item;
    }

    public List<Showing> createShowings(int num) {
        List<Showing> items = new ArrayList<Showing>();
        for (int i = 0; i < num; ++i) {
            items.add(createShowing());
        }
        return items;
    }

    public Showing createShowing() {
        Showing item = EntityFactory.create(Showing.class);

        item.status().setValue(RandomUtil.randomEnum(Showing.Status.class));
        item.result().setValue(RandomUtil.randomEnum(Showing.Result.class));
        item.reason().setValue(RandomUtil.randomEnum(Showing.Reason.class));

        return item;
    }

    public List<MaintenanceRequest> createMntRequests(int num) {
        List<MaintenanceRequest> items = new ArrayList<MaintenanceRequest>();
        for (int i = 0; i < num; ++i) {
            items.add(createMntRequest());
        }
        return items;
    }

    private static String[] MntReqDescription = {

    "Leaking Kitchen Tap", "Broken Blinds", "Door Lock is Broken", "A/C not working",

    "Door hinges squeaking", "Electric stove out of order", "Balcony door glass cracked" };

    public MaintenanceRequest createMntRequest() {
        MaintenanceRequest req = EntityFactory.create(MaintenanceRequest.class);
        req.status().setValue(RandomUtil.randomEnum(MaintenanceRequestStatus.class));
        req.description().setValue(MntReqDescription[RandomUtil.randomInt(MntReqDescription.length - 1)]);
        if (RandomUtil.randomInt(5) > 2) {
            req.surveyResponse().rating().setValue(RandomUtil.randomInt(6));
            req.surveyResponse().description().setValue(CommonsGenerator.lipsumShort());
        }
        // generate dates
        Calendar cal = Calendar.getInstance();
        // get date within 60 days before and after now
        int daySpan = 120;
        int curDay = cal.get(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.DAY_OF_YEAR, curDay + (RandomUtil.randomInt(daySpan) - daySpan / 2));
        req.submitted().setValue(new LogicalDate(cal.getTime()));
        // add 10 min to 10 days
        cal.setTimeInMillis(cal.getTimeInMillis() + 600000 * (1 + RandomUtil.randomInt(24 * 60)));
        req.updated().setValue(new LogicalDate(cal.getTime()));
        return req;
    }
}
