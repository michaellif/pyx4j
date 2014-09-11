/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductDeposit;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.generator.util.RandomUtil;

public class ProductCatalogGenerator {

    private static final I18n i18n = I18n.get(ProductCatalogGenerator.class);

    private static final String ConcessionId = "ConcessionId";

    private static final String ConcessionTypeId = "Concession.Type.Id";

    public ProductCatalogGenerator(long seed) {
        if (seed != 0) {
            DataGenerator.setRandomSeed(seed);
        }
    }

    public void generateProductCatalog(ProductCatalog catalog) {

        DataGenerator.cleanRandomDuplicates(ConcessionId);
        DataGenerator.cleanRandomDuplicates(ConcessionTypeId);

        catalog.services().addAll(createServices(catalog));
        catalog.features().addAll(createFeatures(catalog));
        catalog.concessions().addAll(createConcessions(catalog));

        buildEligibilityMatrix(catalog);
    }

    public List<Service> createServices(ProductCatalog catalog) {
        List<Service> items = new ArrayList<Service>();
        for (ARCode.Type type : ARCode.Type.services()) {
            List<ARCode> arCodes = getARCodes(type);
            for (ARCode arCode : arCodes) {
                items.add(createService(catalog, arCode, false));
                items.add(createService(catalog, arCode, true));
            }
        }
        return items;
    }

    public List<Feature> createFeatures(ProductCatalog catalog) {
        List<Feature> items = new ArrayList<Feature>();
        for (ARCode.Type type : ARCode.Type.features()) {
            List<ARCode> arCodes = getARCodes(type);
            for (ARCode arCode : arCodes) {
                items.add(createFeature(catalog, arCode));
            }
        }
        return items;
    }

    public List<Concession> createConcessions(ProductCatalog catalog) {
        List<Concession> items = new ArrayList<Concession>(3);
        for (int i = 0; i < 3; ++i) {
            items.add(createConcession(catalog));
        }
        return items;
    }

    public void buildEligibilityMatrix(ProductCatalog catalog) {
        for (Service service : catalog.services()) {
            if (ARCode.Type.services().contains(service.code().type().getValue()) && !service.defaultCatalogItem().getValue(false)) {
                for (Feature feature : catalog.features()) {
                    if (!feature.defaultCatalogItem().getValue(false)) {
                        service.version().features().add(feature);
                    }
                }
            }

            int count = Math.min(2, catalog.concessions().size());
            for (int i = 0; i < count; ++i) {
                service.version().concessions().add(RandomUtil.random(catalog.concessions(), ConcessionId, count));
            }
        }
    }

    public void fillUnitServices(ProductCatalog catalog, AptUnit unit) {
        for (ARCode.Type type : ARCode.Type.unitRelatedServices()) {
            fillBuildingElementServices(catalog, unit, RandomUtil.random(getARCodes(type)), createUnitMarketRent(unit));
        }
    }

// internals:

    private ARCode getARCode(ARCode.Type type) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), type);
        return Persistence.service().retrieve(criteria);
    }

    private List<ARCode> getARCodes(ARCode.Type type) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), type);
        return Persistence.service().query(criteria);
    }

    private Service createService(ProductCatalog catalog, ARCode arCode, boolean onlineUse) {
        Service service = EntityFactory.create(Service.class);
        service.catalog().set(catalog);
        service.defaultCatalogItem().setValue(false);

        service.code().set(arCode);
        service.version().name().setValue(RandomUtil.randomLetters(6));
        service.version().description().setValue("Service description");
        service.version().price().setValue(new BigDecimal(1000.10));
        service.version().availableOnline().setValue(onlineUse);

        service.version().depositLMR().enabled().setValue(RandomUtil.randomBoolean());
        service.version().depositLMR().depositType().setValue(DepositType.LastMonthDeposit);
        service.version().depositLMR().chargeCode().set(getARCode(ARCode.Type.Deposit));
        service.version().depositLMR().valueType().setValue(ValueType.Percentage);
        setDepositValue(service.version().depositLMR(), BigDecimal.ONE);
        service.version().depositLMR().description().setValue(DepositType.LastMonthDeposit.toString());

        service.version().depositMoveIn().enabled().setValue(RandomUtil.randomBoolean());
        service.version().depositMoveIn().depositType().setValue(DepositType.MoveInDeposit);
        service.version().depositMoveIn().chargeCode().set(getARCode(ARCode.Type.Deposit));
        service.version().depositMoveIn().valueType().setValue(ValueType.Percentage);
        setDepositValue(service.version().depositMoveIn(), new BigDecimal(0.66));
        service.version().depositMoveIn().description().setValue(DepositType.MoveInDeposit.toString());

        service.version().depositSecurity().enabled().setValue(RandomUtil.randomBoolean());
        service.version().depositSecurity().depositType().setValue(DepositType.SecurityDeposit);
        service.version().depositSecurity().chargeCode().set(getARCode(ARCode.Type.Deposit));
        service.version().depositSecurity().valueType().setValue(ValueType.Monetary);
        setDepositValue(service.version().depositSecurity(), new BigDecimal(333.3));
        service.version().depositSecurity().description().setValue(DepositType.SecurityDeposit.toString());

        return service;

    }

    private Feature createFeature(ProductCatalog catalog, ARCode arCode) {
        Feature feature = EntityFactory.create(Feature.class);
        feature.catalog().set(catalog);
        feature.defaultCatalogItem().setValue(false);

        feature.code().set(arCode);
        feature.version().name().setValue(RandomUtil.randomLetters(6));
        feature.version().description().setValue("Feature description");

        feature.version().recurring().setValue(RandomUtil.randomBoolean() && !ARCode.Type.nonReccuringFeatures().contains(arCode.type()));
        feature.version().mandatory().setValue(RandomUtil.randomBoolean("FeatureMandatory", 20) && !ARCode.Type.nonMandatoryFeatures().contains(arCode.type()));
        feature.version().availableOnline().setValue(RandomUtil.randomBoolean());
        feature.version().price().setValue(new BigDecimal(100.10));

        feature.version().depositLMR().enabled().setValue(RandomUtil.randomBoolean());
        feature.version().depositLMR().depositType().setValue(DepositType.LastMonthDeposit);
        feature.version().depositLMR().chargeCode().set(getARCode(ARCode.Type.Deposit));
        feature.version().depositLMR().valueType().setValue(ValueType.Percentage);
        setDepositValue(feature.version().depositLMR(), BigDecimal.ONE);
        feature.version().depositLMR().description().setValue(DepositType.LastMonthDeposit.toString());

        feature.version().depositMoveIn().enabled().setValue(RandomUtil.randomBoolean());
        feature.version().depositMoveIn().depositType().setValue(DepositType.MoveInDeposit);
        feature.version().depositMoveIn().chargeCode().set(getARCode(ARCode.Type.Deposit));
        feature.version().depositMoveIn().valueType().setValue(ValueType.Percentage);
        setDepositValue(feature.version().depositMoveIn(), new BigDecimal(0.33));
        feature.version().depositMoveIn().description().setValue(DepositType.MoveInDeposit.toString());

        feature.version().depositSecurity().enabled().setValue(RandomUtil.randomBoolean());
        feature.version().depositSecurity().depositType().setValue(DepositType.SecurityDeposit);
        feature.version().depositSecurity().chargeCode().set(getARCode(ARCode.Type.Deposit));
        feature.version().depositSecurity().valueType().setValue(ValueType.Monetary);
        setDepositValue(feature.version().depositSecurity(), new BigDecimal(33.3));
        feature.version().depositSecurity().description().setValue(DepositType.SecurityDeposit.toString());

        feature.version().items().add(createFeatureItem(feature));

        return feature;
    }

    private ProductItem createFeatureItem(Feature feature) {
        ProductItem item = EntityFactory.create(ProductItem.class);

        item.name().setValue(feature.code().name().getValue());
        item.description().setValue(feature.code().type().getStringView() + " description");

        switch (feature.code().type().getValue()) {
        case Parking:
            item.price().setValue(new BigDecimal(5 + RandomUtil.randomInt(50)));
            break;
        case Locker:
            item.price().setValue(new BigDecimal(5 + RandomUtil.randomInt(10)));
            break;
        case Pet:
            item.price().setValue(new BigDecimal(20 + RandomUtil.randomInt(20)));
            break;
        case AddOn:
            item.price().setValue(new BigDecimal(30 + RandomUtil.randomInt(50)));
            break;
        case OneTime:
            item.price().setValue(new BigDecimal(20 + RandomUtil.randomInt(20)));
            break;
        case Utility:
            item.price().setValue(new BigDecimal(80 + RandomUtil.randomInt(50)));
            break;
        default:
            // all other types are not feature-relevant!
            assert (!ARCode.Type.features().contains(feature.code().type().getValue()));
            break;
        }

        item.depositLMR().setValue(getDepositAmount(feature.version().depositLMR(), item));
        item.depositMoveIn().setValue(getDepositAmount(feature.version().depositMoveIn(), item));
        item.depositSecurity().setValue(getDepositAmount(feature.version().depositSecurity(), item));

        return item;
    }

    private Concession createConcession(ProductCatalog catalog) {
        Concession item = EntityFactory.create(Concession.class);
        item.catalog().set(catalog);

        item.version().fromDate().setValue(DateUtils.detectDateformat("2012-01-01"));
        item.version().type().setValue(RandomUtil.random(Concession.Type.values(), ConcessionTypeId, Concession.Type.values().length));

        if (item.version().type().getValue() == Concession.Type.percentageOff) {
            item.version().value().percent().setValue(new BigDecimal(RandomUtil.randomDouble(1.0)));
            item.version()
                    .description()
                    .setValue(
                            i18n.tr("Special Promotion Applies, {0}% Off The Value Of The Service",
                                    item.version().value().percent().getValue().floatValue() * 100));
        } else if (item.version().type().getValue() == Concession.Type.monetaryOff) {
            item.version().value().amount().setValue(new BigDecimal(50d + RandomUtil.randomDouble(50)));
            item.version().description()
                    .setValue(i18n.tr("Special Promotion Applies, ${0} Off The Value Of The Service", item.version().value().amount().getValue().floatValue()));
        } else if (item.version().type().getValue() == Concession.Type.promotionalItem) {
            item.version().value().amount().setValue(new BigDecimal(100d + RandomUtil.randomDouble(100)));
            item.version()
                    .description()
                    .setValue(
                            i18n.tr("Special Promotion Applies, ${0} In Promotional Items Or Services", item.version().value().amount().getValue().floatValue()));
        } else if (item.version().type().getValue() == Concession.Type.free) {
            item.version().value().amount().setValue(new BigDecimal(200d + RandomUtil.randomDouble(100)));
            item.version().description().setValue(i18n.tr("Special Promotion Applies, Everything Completely Free"));
        }

        item.version().term().setValue(RandomUtil.random(Concession.Term.values()));
        item.version().condition().setValue(RandomUtil.random(Concession.Condition.values()));
        item.version().mixable().setValue(RandomUtil.randomBoolean());

        item.version().effectiveDate().setValue(DataGenerator.randomDateInLastYearMonthShifted(2));
        item.version().expirationDate().setValue(DataGenerator.randomDateInLastYearMonthShifted(4));

        return item;
    }

    private void fillBuildingElementServices(ProductCatalog catalog, BuildingElement buildingElement, ARCode arCode, BigDecimal price) {
        for (Service service : getServices(catalog, arCode)) {
            ProductItem item = EntityFactory.create(ProductItem.class);

            item.element().set(buildingElement);
            item.name().setValue(arCode.name().getValue());
            item.description().setValue(arCode.type().getStringView() + " description");
            item.price().setValue(price); // This value may not be used in all cases and overridden later in generator

            item.depositLMR().setValue(getDepositAmount(service.version().depositLMR(), item));
            item.depositMoveIn().setValue(getDepositAmount(service.version().depositMoveIn(), item));
            item.depositSecurity().setValue(getDepositAmount(service.version().depositSecurity(), item));

            Persistence.ensureRetrieve(service.version().items(), AttachLevel.Attached);
            service.version().items().add(item);
        }
    }

    private List<Service> getServices(ProductCatalog catalog, ARCode arCode) {
        List<Service> services = new ArrayList<Service>();

        for (Service service : catalog.services()) {
            if (service.code().equals(arCode) && !service.defaultCatalogItem().getValue(false)) {
                services.add(service);
            }
        }

        return services;
    }

    private void setDepositValue(ProductDeposit deposit, BigDecimal value) {
        if (ValueType.Percentage.equals(deposit.valueType().getValue())) {
            deposit.value().percent().setValue(value);
        } else {
            deposit.value().amount().setValue(value);
        }
    }

    private BigDecimal getDepositAmount(ProductDeposit deposit, ProductItem item) {
        if (RandomUtil.randomBoolean()) {
            if (ValueType.Percentage.equals(deposit.valueType().getValue())) {
                return DomainUtil.roundMoney(deposit.value().percent().getValue(BigDecimal.ZERO).multiply(item.price().getValue(BigDecimal.ZERO)));
            } else {
                return deposit.value().amount().getValue();
            }
        }
        return null;
    }

    private static BigDecimal createUnitMarketRent(AptUnit unit) {
        BigDecimal base = new BigDecimal(900);
        base = base.add(new BigDecimal(unit.info()._bedrooms().getValue() * 150));
        base = base.add(new BigDecimal(unit.info()._bathrooms().getValue() * 50));
        return base.add(new BigDecimal(RandomUtil.randomInt(200)));
    }
}
