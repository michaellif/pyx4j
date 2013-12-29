/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 11, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.BuildingDataModel;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.DepositPolicyDataModel;
import com.propertyvista.test.mock.models.GLCodeDataModel;
import com.propertyvista.test.mock.models.IdAssignmentPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.test.mock.models.LocationsDataModel;
import com.propertyvista.test.mock.models.PmcDataModel;
import com.propertyvista.test.mock.models.TaxesDataModel;
import com.propertyvista.test.mock.security.PasswordEncryptorFacadeMock;

/**
 * Create new lease in Yardi using PV lease application
 * ====================================================
 * NOTE: this is a system integration test that works via live connection to a configured YARDI environment.
 * - It is NOT expected to fail unless a runtime exception occurs.
 * - All checked exceptions only reported by logging ERROR message.
 */
public class YardiCreateNewLeaseTestManual extends IntegrationTestBase {
    private final static Logger log = LoggerFactory.getLogger(YardiCreateNewLeaseTestManual.class);

    private static PmcYardiCredential yc = getTestPmcYardiCredential();

    private Building building;

    private Lease lease;

    @Override
    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        models.add(CustomerDataModel.class);
        models.add(LocationsDataModel.class);
        models.add(TaxesDataModel.class);
        models.add(GLCodeDataModel.class);
        models.add(ARCodeDataModel.class);
        models.add(BuildingDataModel.class);
        models.add(IdAssignmentPolicyDataModel.class);
        models.add(DepositPolicyDataModel.class);
        models.add(ARPolicyDataModel.class);
        models.add(LeaseBillingPolicyDataModel.class);
        models.add(LeaseDataModel.class);
        return models;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        preloadData();

        ServerSideFactory.register(PasswordEncryptorFacade.class, PasswordEncryptorFacadeMock.class);
    }

    public void testCase() {
        createLease("01-Dec-2013", "30-Nov-2014");
        fixTenantName(null);
        setMoveIn(new LogicalDate());
        setPropertyCode("prvista2");
        setFloorplan("prv21b1", 1);
        setUnitNo("150");
        setCurrentAddress(getAddress());

        addOutdoorParking();
        addLargeLocker();

        Lease lease = getLease();
        try {
            YardiGuestManagementService.getInstance().createFutureLease(yc, lease);
        } catch (YardiServiceException e) {
            log.info("ERROR: {}", e.getMessage());
        }
    }

    protected BillableItem addOutdoorParking(String effectiveDate, String expirationDate) {
        return addBillableItem(ARCodeDataModel.Code.outdoorParking, effectiveDate, expirationDate);
    }

    protected BillableItem addOutdoorParking() {
        return addBillableItem(ARCodeDataModel.Code.outdoorParking);
    }

    protected BillableItem addLargeLocker(String effectiveDate, String expirationDate) {
        return addBillableItem(ARCodeDataModel.Code.largeLocker, effectiveDate, expirationDate);
    }

    protected BillableItem addLargeLocker() {
        return addBillableItem(ARCodeDataModel.Code.largeLocker);
    }

    protected Lease getLease() {
        return lease;
    }

    protected Building getBuilding() {
        if (building == null) {
            building = getDataModel(BuildingDataModel.class).addBuilding();
            Persistence.service().commit();
        }
        return building;
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo) {
        createLease(leaseDateFrom, leaseDateTo, null, null);
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance) {
        createLease(leaseDateFrom, leaseDateTo, agreedPrice, carryforwardBalance, getDataModel(CustomerDataModel.class).addCustomer());
    }

    protected void createLease(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance, Customer customer) {
        lease = getDataModel(LeaseDataModel.class).addLease(getBuilding(), leaseDateFrom, leaseDateTo, agreedPrice, carryforwardBalance,
                Arrays.asList(new Customer[] { customer }));
    }

    protected Lease retrieveLease() {
        return ServerSideFactory.create(LeaseFacade.class).load(getLease(), false);
    }

    protected Lease retrieveLeaseDraft() {
        return ServerSideFactory.create(LeaseFacade.class).load(getLease(), true);
    }

    private BillableItem addBillableItem(ARCodeDataModel.Code code) {
        Lease lease = retrieveLease();
        return addBillableItem(code, lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue());
    }

    private BillableItem addBillableItem(ARCodeDataModel.Code code, String effectiveDate, String expirationDate) {
        return addBillableItem(code, getDate(effectiveDate), getDate(expirationDate));
    }

    private BillableItem addBillableItem(ARCodeDataModel.Code code, LogicalDate effectiveDate, LogicalDate expirationDate) {
        Lease lease = retrieveLeaseDraft();

        ProductItem serviceItem = lease.currentTerm().version().leaseProducts().serviceItem().item();
        Persistence.service().retrieve(serviceItem.product());
        Service.ServiceV service = serviceItem.product().cast();
        Persistence.ensureRetrieve(service.features(), AttachLevel.Attached);

        ARCode arCode = getDataModel(ARCodeDataModel.class).getARCode(code);

        for (Feature feature : service.features()) {

            if (arCode.equals(feature.code())) {

                Persistence.ensureRetrieve(feature.version().items(), AttachLevel.Attached);
                for (ProductItem item : feature.version().items()) {

                    LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
                    BillableItem billableItem = leaseFacade.createBillableItem(lease, item, lease.unit().building());

                    billableItem.effectiveDate().setValue(effectiveDate);
                    billableItem.expirationDate().setValue(expirationDate);

                    lease.currentTerm().version().leaseProducts().featureItems().add(billableItem);

                    // correct agreed price for existing leases:
                    BigDecimal agreedPrice = null;
                    if (lease.status().getValue() == Lease.Status.ExistingLease) {
                        switch (code) {
                        case outdoorParking:
                            agreedPrice = new BigDecimal("80.00");
                            break;
                        case largeLocker:
                            agreedPrice = new BigDecimal("60.00");
                            break;
                        case catRent:
                            agreedPrice = new BigDecimal("20.00");
                            break;
                        case booking:
                            agreedPrice = new BigDecimal("30.00");
                            break;
                        default:
                            break;
                        }
                    }
                    if (agreedPrice != null) {
                        billableItem.agreedPrice().setValue(agreedPrice);
                    } else {
                        billableItem.agreedPrice().setValue(item.price().getValue());
                    }

                    leaseFacade.persist(lease.currentTerm());
                    Persistence.service().commit();
                    return billableItem;

                }
            }

        }

        return null;
    }

    void setMoveIn(LogicalDate date) {
        lease.expectedMoveIn().setValue(date);
        Persistence.service().persist(lease);
    }

    void setPropertyCode(String code) {
        Building building = lease.unit().building();
        building.propertyCode().setValue(code);
        Persistence.service().persist(building);
    }

    void setFloorplan(String code, int beds) {
        Persistence.ensureRetrieve(lease.unit().floorplan(), AttachLevel.Attached);
        Floorplan fp = lease.unit().floorplan();
        fp.code().setValue(code);
        fp.bedrooms().setValue(beds);
        fp.building().set(building);
        Persistence.service().persist(fp);
    }

    void setUnitNo(String unitNo) {
        AptUnit unit = lease.unit();
        unit.info().number().setValue(unitNo);
        Persistence.service().persist(unit);
    }

    private void setCurrentAddress(PriorAddress addr) {
        Persistence.service().retrieveMember(lease._applicant().customer().personScreening());
        lease._applicant().customer().personScreening().version().currentAddress().set(addr);
        Persistence.service().persist(lease._applicant().customer().personScreening());
    }

    private void fixTenantName(String fix) {
        if (fix == null) {
            fix = new SimpleDateFormat("ddHHmm").format(new Date());
        }
        String lastName = lease._applicant().customer().person().name().lastName().getValue();
        lease._applicant().customer().person().name().lastName().setValue(lastName + "-" + fix);
        Persistence.service().persist(lease._applicant().customer());
    }

    private PriorAddress getAddress() {
        PriorAddress addr = EntityFactory.create(PriorAddress.class);
        addr.county().setValue("US");
        addr.province().code().setValue("CA");
        addr.postalCode().setValue("98765");
        addr.city().setValue("Hometown");
        addr.streetName().setValue("Main");
        addr.streetNumber().setValue("123");
        addr.streetType().setValue(StreetType.street);
        return addr;
    }

    static PmcYardiCredential getTestPmcYardiCredential() {
        PmcYardiCredential cr = EntityFactory.create(PmcYardiCredential.class);
        cr.propertyListCodes().setValue("prvista2");
        cr.serviceURLBase().setValue("https://www.iyardiasp.com/8223third_17");
        cr.username().setValue("propertyvistadb");
        cr.password().number().setValue("52673");
        cr.serverName().setValue("aspdb04");
        cr.database().setValue("afqoml_live");
        cr.platform().setValue(PmcYardiCredential.Platform.SQL);
        return cr;
    }
}
