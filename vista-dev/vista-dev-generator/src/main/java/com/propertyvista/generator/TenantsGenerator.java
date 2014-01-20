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
package com.propertyvista.generator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Guest;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.shared.util.CreditCardFormatter;

public class TenantsGenerator {

    public TenantsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
    }

    public List<Customer> createTenants(int num) {
        List<Customer> items = new ArrayList<Customer>();
        for (int i = 0; i < num; i++) {
            items.add(createTenant());
        }
        return items;
    }

    public Customer createTenant() {
        Customer item = EntityFactory.create(Customer.class);
        item.person().set(CommonsGenerator.createPerson());
        return item;
    }

    public List<LeasePaymentMethod> createPaymentMethods(Customer customer) {
        List<LeasePaymentMethod> l = new Vector<LeasePaymentMethod>();

        for (int i = 0; i < 2; i++) {
            LeasePaymentMethod m = EntityFactory.create(LeasePaymentMethod.class);
            m.type().setValue(PaymentType.CreditCard);

            // create new payment method details:
            CreditCardInfo details = EntityFactory.create(CreditCardInfo.class);
            details.cardType().setValue(CreditCardType.Visa);
            details.card().newNumber().setValue("00" + CommonsStringUtils.d00(RandomUtil.randomInt(99)) + CommonsStringUtils.d00(RandomUtil.randomInt(99)));
            details.card().obfuscatedNumber().setValue(new CreditCardFormatter().obfuscate(details.card().newNumber().getValue()));
            details.nameOn().setValue(customer.person().name().getStringView());
            details.expiryDate().setValue(RandomUtil.randomLogicalDate(2012, 2015));
            m.details().set(details);

            m.customer().set(customer);
            m.sameAsCurrent().setValue(Boolean.FALSE);
            m.billingAddress().set(CommonsGenerator.createAddressSimple());

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

        for (int i = 0; i < 1 + RandomUtil.randomInt(2); ++i) {
            Guest guest = EntityFactory.create(Guest.class);
            guest.person().set(CommonsGenerator.createPerson());
            item.guests().add(guest);
        }

        item.moveInDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.leaseTerm().setValue(RandomUtil.randomEnum(Lead.LeaseTerm.class));
        item.leaseType().setValue(RandomUtil.randomEnum(ARCode.Type.class));

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
        if (item.result().getValue() == Showing.Result.notInterested) {
            item.reason().setValue(RandomUtil.randomEnum(Showing.Reason.class));
        }

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
        req.updated().setValue(cal.getTime());
        req.permissionToEnter().setValue(true);
        return req;
    }
}
