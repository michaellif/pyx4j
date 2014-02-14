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
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.biz.system.yardi.YardiApplicationFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.VistaNamespace;
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
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.test.integration.IntegrationTestBase;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel;
import com.propertyvista.test.mock.models.ARPolicyDataModel;
import com.propertyvista.test.mock.models.AgreementLegalPolicyDataModel;
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

    private PmcYardiCredential yc;

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
        models.add(AgreementLegalPolicyDataModel.class);
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
        setPropertyCode("gran0002");
        setFloorplan("2bdrm", 2);
        setUnitNo("0001");
        setCurrentAddress(getAddress());

        addOutdoorParking();
        addLargeLocker();

        Lease lease = retrieveLease();

        try {
            ServerSideFactory.create(YardiApplicationFacade.class).validateApplicationAcceptance(lease.unit().building());
            // clear guests if added earlier
            ServerSideFactory.create(YardiApplicationFacade.class).createApplication(lease);
            log.info("Created Guest: {}", retrieveLease().leaseApplication().yardiApplicationId().getValue());

            addCoTenant();
            addGuarantor();

            ServerSideFactory.create(YardiApplicationFacade.class).addLeaseParticipants(lease);
            Persistence.ensureRetrieve(lease.leaseParticipants(), AttachLevel.Attached);
            for (LeaseParticipant<?> p : lease.leaseParticipants()) {
                Persistence.ensureRetrieve(p.leaseTermParticipants(), AttachLevel.Attached);
                log.info("  Participant {} = {}", p.leaseTermParticipants().iterator().next().role().getValue().name(), p.yardiApplicantId().getValue());
            }

            if (false) {
                ServerSideFactory.create(YardiApplicationFacade.class).holdUnit(lease);
                log.info("Unit held for: {}", lease.leaseApplication().yardiApplicationId().getValue());

                ServerSideFactory.create(YardiApplicationFacade.class).approveApplication(lease);
                log.info("Signed lease: {}", lease.leaseId().getValue());
            }
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(e.getMessage(), e);
        } catch (UserRuntimeException e) {
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

            // yardi credential setup
            building.integrationSystemId().setValue(getTestPmcYardiCredential().getPrimaryKey());

            Persistence.service().persist(building);
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

    protected void addCoTenant() {
        Lease lease = retrieveLeaseDraft();
        LeaseTermTenant coTenant = EntityFactory.create(LeaseTermTenant.class);
        coTenant.leaseParticipant().customer().set(getDataModel(CustomerDataModel.class).addCustomer());
        coTenant.role().setValue(LeaseTermParticipant.Role.CoApplicant);
        lease.currentTerm().version().tenants().add(coTenant);
        ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());
        Persistence.service().commit();
    }

    protected void addGuarantor() {
        Lease lease = retrieveLeaseDraft();
        LeaseTermGuarantor guarantor = EntityFactory.create(LeaseTermGuarantor.class);
        guarantor.leaseParticipant().customer().set(getDataModel(CustomerDataModel.class).addCustomer());
        guarantor.role().setValue(LeaseTermParticipant.Role.Guarantor);
        lease.currentTerm().version().guarantors().add(guarantor);
        ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());
        Persistence.service().commit();
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
        addr.country().name().setValue("Canada");
        addr.province().name().setValue("Ontario");
        addr.postalCode().setValue("M5H 1A1");
        addr.city().setValue("Toronto");
        addr.streetName().setValue("King");
        addr.streetNumber().setValue("100");
        addr.streetType().setValue(StreetType.street);
        return addr;
    }

    private PmcYardiCredential getTestPmcYardiCredential() {
        if (yc == null) {
            yc = EntityFactory.create(PmcYardiCredential.class);
            if (false) {
                yc.propertyListCodes().setValue("prvista2");
                yc.serviceURLBase().setValue("https://www.iyardiasp.com/8223third_17");
                yc.username().setValue("propertyvistadb");
                yc.password().number().setValue("52673");
                yc.serverName().setValue("aspdb04");
                yc.database().setValue("afqoml_live");
                yc.platform().setValue(PmcYardiCredential.Platform.SQL);
            } else {
                yc.propertyListCodes().setValue("gran0002");
                yc.serviceURLBase().setValue("http://yardi.birchwoodsoftwaregroup.com/Voyager60");
                yc.serviceURLBase().setValue("http://yardi.birchwoodsoftwaregroup.com:8080/voyager6008sp17");
                yc.serviceURLBase().setValue("http://192.168.50.100/voyager6008sp17");
                yc.username().setValue("vista_dev");
                yc.password().number().setValue("vista_dev");
                yc.serverName().setValue("WIN-CO5DPAKNUA4\\YARDI");
                yc.database().setValue("vista_dev");
                yc.platform().setValue(PmcYardiCredential.Platform.SQL);
            }
            yc.pmc().set(getDataModel(PmcDataModel.class).getItem(0));
            String namespace = NamespaceManager.getNamespace();
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            Persistence.service().persist(yc);
            Persistence.service().commit();
            NamespaceManager.setNamespace(namespace);
        }
        return yc;
    }
}
