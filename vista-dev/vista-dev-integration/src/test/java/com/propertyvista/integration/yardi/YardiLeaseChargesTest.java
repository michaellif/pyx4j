/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.yardi.entity.mits.Customerinfo;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.test.integration.BillableItemTester;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentAgreementTester;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.integration.PreauthorizedPaymentBuilder;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.YardiTestBase;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.updater.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.updater.PropertyUpdater;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdater;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

@Category(FunctionalTests.class)
public class YardiLeaseChargesTest extends YardiTestBase {

    public static final String PROPERTY_CODE = "prop123";

    public static final String UNIT_NUMBER = "0111";

    public static final String CUSTOOMER_ID = "t000111";

    /*
     * scope:
     * - Adding a new lease product: expect product added to new term; PAD suspended
     * - Changing product amount: new charge appear in new term; PAD suspended
     * - Changing product expiration date to a past date: expect product removed from new term; PAD suspended
     * - Removing existing lease product: expect product removed from new term; PAD suspended
     * - Any other update: reflected in new term; no changes to PAD
     * - Terminating lease (no data received): lease charge = 0; features removed; PAD suspended
     */

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        {
            // @formatter:off
            PropertyUpdater updater = new PropertyUpdater(PROPERTY_CODE).
            set(PropertyUpdater.ADDRESS.Address1, "11 prop123 str").
            set(PropertyUpdater.ADDRESS.PostalCode, "A1B 2C3").
            set(PropertyUpdater.ADDRESS.State, "ON").
            set(PropertyUpdater.ADDRESS.Country, "Canada");
            // @formatter:on
            MockEventBus.fireEvent(new PropertyUpdateEvent(updater));
        }

        //Add RtCustomer, main tenant and Unit
        {
            // @formatter:off
            RtCustomerUpdater updater = new RtCustomerUpdater(PROPERTY_CODE, CUSTOOMER_ID).
            set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT).
            set(RtCustomerUpdater.YCUSTOMER.CustomerID, CUSTOOMER_ID).
            set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "John").
            set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Smith").
            set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1234.56")).
            set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true).
            set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.UnitBedrooms, new BigDecimal("2")).
            set(RtCustomerUpdater.UNITINFO.UnitBathrooms, new BigDecimal("1")).
            set(RtCustomerUpdater.UNITINFO.UnitRent, new BigDecimal("1300.00")).
            set(RtCustomerUpdater.UNITINFO.FloorPlanID, "2bdrm").
            set(RtCustomerUpdater.UNITINFO.FloorplanName, "2 Bedroom");
            // @formatter:on
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "rent").
            set(LeaseChargeUpdater.Name.Description, "Rent").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rrent").
            set(LeaseChargeUpdater.Name.GLAccountNumber, "40000301").
            set(LeaseChargeUpdater.Name.Amount, "1234.56").
            set(LeaseChargeUpdater.Name.Comment, "Rent (05/2013)");
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }
        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "parkA").
            set(LeaseChargeUpdater.Name.Description, "Parking A").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("01-Jun-2012")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rpark").
            set(LeaseChargeUpdater.Name.Amount, "50.00").
            set(LeaseChargeUpdater.Name.Comment, "Parking A");
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }
        setSysDate("2013-05-25");

        yardiImportAll(getYardiCredential(PROPERTY_CODE));

        // @formatter:off
        new BillableItemTester(getLease().currentTerm().version().leaseProducts().serviceItem()).
        effectiveDate("01-Jun-2012").
        expirationDate("31-Jul-2014").
        description("Regular Residential Unit").
        agreedPrice("1234.56");
        // @formatter:on

        // create PAP for test lease
        createPap(getLease());

        // prevent table lock on PAP suspension
        Persistence.service().commit();
    }

    /**
     * Changing product amount: new charge appear in new term; PAD suspended
     */
    @Test
    public void testChangingAmount() throws Exception {
        // Ensure PAP is active
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1) //
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        BillableItem parking = getLease().currentTerm().version().leaseProducts().featureItems().get(0);

        // add BillableItemExtraData - ensure it stays intact after price change
        Vehicle vehicleData = EntityFactory.create(Vehicle.class);
        vehicleData.plateNumber().setValue("12345");
        vehicleData.make().setValue("honda");
        vehicleData.model().setValue("accord");
        vehicleData.color().setValue("black");
        parking.extraData().set(vehicleData);
        Persistence.service().persist(parking);
        Persistence.service().commit();

        // @formatter:off
        new BillableItemTester(parking).
        uid("rpark:1").
        description("Parking A").
        agreedPrice("50.00");
        // @formatter:on

        // modify amount
        LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "parkA");
        updater.set(LeaseChargeUpdater.Name.Amount, "55.00");
        MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));

        yardiImportAll(getYardiCredential(PROPERTY_CODE));

        // @formatter:off
        parking = getLease().currentTerm().version().leaseProducts().featureItems().get(0);
        new BillableItemTester(parking).
        uid("rpark:1").
        description("Parking A").
        agreedPrice("55.00");
        // @formatter:on

        // ensure BillableItemExtraData is intact
        vehicleData = parking.extraData().cast();
        assertEquals("Invalid PlateNo", "12345", vehicleData.plateNumber().getValue());
        assertEquals("Invalid Make", "honda", vehicleData.make().getValue());
        assertEquals("Invalid Model", "accord", vehicleData.model().getValue());
        assertEquals("Invalid Color", "black", vehicleData.color().getValue());

        // Ensure PAP NOT suspended
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 55"));
    }

    /**
     * Changing product expiration date to a past date: expect product removed from new term; PAD suspended
     */
    @Test
    public void testExpiredProduct() throws Exception {
        // Ensure PAP is active
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        // @formatter:off
        new BillableItemTester(getLease().currentTerm().version().leaseProducts().featureItems().get(0)).
        uid("rpark:1").
        description("Parking A");
        // @formatter:on

        // set expiration as of yesterday
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(getSysDate());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "parkA");
        updater.set(LeaseChargeUpdater.Name.ServiceToDate, yesterday);
        MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));

        yardiImportAll(getYardiCredential(PROPERTY_CODE));
        // Ensure feature removed
        assertEquals(0, getLease().currentTerm().version().leaseProducts().featureItems().size());

        // Ensure PAP NOT suspended
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56"));
    }

    /**
     * Removing existing lease product: expect product removed from new term; PAD suspended
     */
    @Test
    public void testRemovingFeature() throws Exception {
        // Ensure PAP is active
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "parkA").remove();
        MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));

        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential(PROPERTY_CODE), new ExecutionMonitor());
        // check feature removed
        assertEquals(0, getLease().currentTerm().version().leaseProducts().featureItems().size());

        // Ensure PAP not suspended
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56"));
    }

    @Test
    public void testAddingSecondRent() throws Exception {
        // Ensure PAP is active
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1) //
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        new BillableItemTester(getLease().currentTerm().version().leaseProducts().featureItems().get(0)). //
                uid("rpark:1"). //
                agreedPrice("50.00");

        {
            // @formatter:off
            LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "rent2").
            set(LeaseChargeUpdater.Name.Description, "Second Rent").
            set(LeaseChargeUpdater.Name.ServiceFromDate, DateUtils.detectDateformat("01-Aug-2012")).
            set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Jul-2014")).
            set(LeaseChargeUpdater.Name.ChargeCode, "rrent").
            set(LeaseChargeUpdater.Name.GLAccountNumber, "40000301").
            set(LeaseChargeUpdater.Name.Amount, "500.00").
            set(LeaseChargeUpdater.Name.Comment, "Rent (05/2013)");
            // @formatter:on
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        yardiImportAll(getYardiCredential(PROPERTY_CODE), 0, 1);

        new BillableItemTester(getLease().currentTerm().version().leaseProducts().serviceItem()). //
                uid("rrent:1"). //
                agreedPrice("1234.56");

        new BillableItemTester(getLease().currentTerm().version().leaseProducts().featureItems().get(0)). //
                uid("rpark:1"). //
                agreedPrice("50.00");

        new BillableItemTester(getLease().currentTerm().version().leaseProducts().featureItems().get(1)). //
                uid("rrent:2"). //
                agreedPrice("500.00");

        // Ensure PAP NOT suspended
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));
    }

    /**
     * Any other update: reflected in new term; no changes to PAD
     */
    @Test
    public void testExtendingTerm() throws Exception {
        Lease originalLease = getLease();

        // Ensure PAP is active
        new PaymentAgreementTester(originalLease.billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        new BillableItemTester(originalLease.currentTerm().version().leaseProducts().featureItems().get(0)) //
                .uid("rpark:1")//
                .description("Parking A");

        Persistence.ensureRetrieve(originalLease.leaseParticipants(), AttachLevel.Attached);
        Persistence.ensureRetrieve(originalLease.currentTerm().version().tenants(), AttachLevel.Attached);
        LeaseParticipant<?> originalFirstTenant = originalLease.leaseParticipants().iterator().next();

        // set expiration date one month forward
        LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "parkA");
        updater.set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("31-Aug-2014"));
        MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));

        yardiImportAll(getYardiCredential(PROPERTY_CODE));

        Lease updatedLease = getLease();

        new BillableItemTester(updatedLease.currentTerm().version().leaseProducts().featureItems().get(0))//
                .uid("rpark:1")//
                .description("Parking A")//
                .expirationDate("31-Aug-2014");

        // Ensure PAP is active
        new PaymentAgreementTester(updatedLease.billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        Persistence.ensureRetrieve(updatedLease.leaseParticipants(), AttachLevel.Attached);
        Persistence.ensureRetrieve(updatedLease.currentTerm().version().tenants(), AttachLevel.Attached);
        LeaseParticipant<?> updatedFirstTenant = updatedLease.leaseParticipants().iterator().next();
        Assert.assertEquals("Tenant is still the same", originalFirstTenant.getPrimaryKey(), updatedFirstTenant.getPrimaryKey());
        Assert.assertEquals("Tenant is in current term", originalFirstTenant.id(), updatedLease.currentTerm().version().tenants().get(0).leaseParticipant()
                .id());
    }

    public void testNewTermUnitMove() throws Exception {
        Lease originalLease = getLease();

        // Ensure PAP is active
        new PaymentAgreementTester(originalLease.billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        new BillableItemTester(originalLease.currentTerm().version().leaseProducts().featureItems().get(0))//
                .uid("rpark:1").description("Parking A");

        Persistence.ensureRetrieve(originalLease.leaseParticipants(), AttachLevel.Attached);
        LeaseParticipant<?> originalFirstTenant = originalLease.leaseParticipants().iterator().next();
        Assert.assertNotNull("original Tenant is Ok", originalFirstTenant.getPrimaryKey());

        Persistence.ensureRetrieve(originalLease.currentTerm().version().tenants(), AttachLevel.Attached);
        Assert.assertEquals("Tenant is in current term", originalFirstTenant.id(), originalLease.currentTerm().version().tenants().get(0).leaseParticipant()
                .id());

        {
            RtCustomerUpdater updater = new RtCustomerUpdater(PROPERTY_CODE, CUSTOOMER_ID) //
                    .set(RtCustomerUpdater.UNITINFO.UnitID, "222");

            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }

        yardiImportAll(getYardiCredential(PROPERTY_CODE));

        Lease updatedLease = getLeaseById(CUSTOOMER_ID);

        Persistence.ensureRetrieve(updatedLease.leaseParticipants(), AttachLevel.Attached);
        LeaseParticipant<?> updatedFirstTenant = updatedLease.leaseParticipants().iterator().next();
        Assert.assertNotNull("updated Tenant is Ok", updatedFirstTenant.getPrimaryKey());
        Assert.assertEquals("Tenant is still the same", originalFirstTenant.getPrimaryKey(), updatedFirstTenant.getPrimaryKey());
        Assert.assertEquals("Tenants are still the same", updatedLease.leaseParticipants(), updatedLease.leaseParticipants());

        Persistence.ensureRetrieve(updatedLease.currentTerm().version().tenants(), AttachLevel.Attached);
        Assert.assertEquals("Tenant is in current term", updatedFirstTenant.id(), updatedLease.currentTerm().version().tenants().get(0).leaseParticipant().id());

        new BillableItemTester(updatedLease.currentTerm().version().leaseProducts().featureItems().get(0))//
                .uid("rpark:1")//
                .description("Parking A")//
                .effectiveDate("01-Jun-2012")//
                .expirationDate("31-Jul-2014");

        // Ensure PAP is active
        new PaymentAgreementTester(updatedLease.billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        new PaymentAgreementTester((Tenant) updatedFirstTenant)//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("1234.56 + 50"));

        setPaymentBatchProcess();

        advanceSysDate("2013-06-01");

        new PaymentRecordTester(updatedLease.billingAccount())//
                .count(1) //
                .lastRecordStatus(PaymentStatus.Canceled)//  No Merchant Account
                .lastRecordAmount(eval("1234.56 + 50"));
    }

    /*
     * Terminating lease (no data received): lease charge = 0; features removed; PAD suspended
     */
    @Test
    public void testLeaseTermination() throws Exception {
        // create another lease to ensure non-empty transaction list for the building
        RtCustomerUpdater customer = new RtCustomerUpdater(PROPERTY_CODE, "t000222");
        customer.set(RtCustomerUpdater.YCUSTOMER.Type, Customerinfo.CURRENT_RESIDENT);
        customer.set(RtCustomerUpdater.YCUSTOMER.CustomerID, "t000222");
        customer.set(RtCustomerUpdater.YCUSTOMERNAME.FirstName, "Another");
        customer.set(RtCustomerUpdater.YCUSTOMERNAME.LastName, "Resident");
        customer.set(RtCustomerUpdater.UNITINFO.UnitType, "2bdrm");
        customer.set(RtCustomerUpdater.YLEASE.CurrentRent, new BigDecimal("1000.00"));
        customer.set(RtCustomerUpdater.YLEASE.LeaseFromDate, DateUtils.detectDateformat("01-Jun-2012"));
        customer.set(RtCustomerUpdater.YLEASE.ResponsibleForLease, true);
        MockEventBus.fireEvent(new RtCustomerUpdateEvent(customer));
        LeaseChargeUpdater lease = new LeaseChargeUpdater(PROPERTY_CODE, "t000222", "rent");
        lease.set(LeaseChargeUpdater.Name.Amount, "1000.00");
        MockEventBus.fireEvent(new LeaseChargeUpdateEvent(lease));

        // Ensure PAP is active
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1)//
                .activeCount(1);

        // expire test lease
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(getSysDate());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        // expire service
        LeaseChargeUpdater updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "rent");
        updater.set(LeaseChargeUpdater.Name.ServiceToDate, yesterday);
        MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        // expire feature
        updater = new LeaseChargeUpdater(PROPERTY_CODE, CUSTOOMER_ID, "parkA");
        updater.set(LeaseChargeUpdater.Name.ServiceToDate, yesterday);
        MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));

        yardiImportAll(getYardiCredential(PROPERTY_CODE));

        // Ensure rent = 0
        // @formatter:off
        new BillableItemTester(getLease().currentTerm().version().leaseProducts().serviceItem()).
        description("Regular Residential Unit").
        agreedPrice("0.00");
        // @formatter:on

        // Ensure features removed
        assertEquals(0, getLease().currentTerm().version().leaseProducts().featureItems().size());

        // Ensure PAP suspended
        new PaymentAgreementTester(getLease().billingAccount())//
                .count(1)//
                .activeCount(1)//
                .lastRecordAmount(eval("0"));
    }

    // ---------- private -----------------

    private Lease getLease() {
        Building building = getBuilding(PROPERTY_CODE);
        AptUnit unit = getUnit(building, UNIT_NUMBER);
        return getCurrentLease(unit);
    }

    private AutopayAgreement createPap(Lease lease) {
        AutopayAgreement pap = EntityFactory.create(AutopayAgreement.class);
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        Tenant tenant = lease.currentTerm().version().tenants().get(0).leaseParticipant();
        pap.paymentMethod().set(createPaymentMethod(tenant.customer(), getLease().unit().building()));

        PreauthorizedPaymentBuilder pab = new PreauthorizedPaymentBuilder();
        pab.add(lease.currentTerm().version().leaseProducts().serviceItem());
        for (BillableItem feature : lease.currentTerm().version().leaseProducts().featureItems()) {
            pab.add(feature);
        }

        pap.coveredItems().addAll(pab.build());
        ServerSideFactory.create(PaymentMethodFacade.class).persistAutopayAgreement(pap, tenant);
        return pap;
    }

    private LeasePaymentMethod createPaymentMethod(Customer customer, Building building) {
        LeasePaymentMethod paymentMethod = EntityFactory.create(LeasePaymentMethod.class);
        paymentMethod.customer().set(customer);
        paymentMethod.type().setValue(PaymentType.Echeck);
        paymentMethod.isProfiledMethod().setValue(Boolean.TRUE);
        EcheckInfo details = EntityFactory.create(EcheckInfo.class);
        details.accountNo().newNumber().setValue("12345678");
        paymentMethod.details().set(details);
        ServerSideFactory.create(PaymentMethodFacade.class).persistLeasePaymentMethod(paymentMethod, building);
        return paymentMethod;
    }

}
