/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.generator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.LegalQuestions;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.media.ProofOfEmploymentDocument;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy.BjccEntry;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.tenant.PersonCreditCheck;
import com.propertyvista.domain.tenant.PersonCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalAsset.AssetType;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class ScreeningGenerator {

    private List<IdentificationDocumentType> identificationDocumentTypes;

    ScreeningGenerator() {

    }

    PersonScreening createScreening() {
        PersonScreening screening = EntityFactory.create(PersonScreening.class);

        // Documents
        for (int i = 0; i < 2 + RandomUtil.randomInt(2); i++) {
            screening.documents().add(createIdentificationDocument());
        }

        // Address
        screening.version().currentAddress().set(createPriorAddress());
        screening.version().previousAddress().set(createPriorAddress());
        makeAddressValid(screening.version().currentAddress(), screening.version().previousAddress());

        // Questions
        screening.version().legalQuestions().set(createLegalQuestions());

        // Incomes
        screening.version().incomes().addAll(createIncomes());

        // Assets
        int minAssets = (screening.version().incomes().size() == 0) ? 1 : 0;
        screening.version().assets().addAll(createAssets(minAssets));

        screening.creditChecks().addAll(createPersonCreditCheck());

        return screening;
    }

    private void makeAddressValid(PriorAddress currentAddress, PriorAddress previousAddress) {
        currentAddress.moveOutDate().setValue(RandomUtil.randomLogicalDate(2013, 2014)); // this has to be in the future

        // moveOut date for previous address is 1 day before the moveIn date for current address
        Date moveOut = new Date();
        moveOut.setTime(currentAddress.moveInDate().getValue().getTime() - 86400000);
        // moveIn date for previous address is a few days/years back
        int years = RandomUtil.randomInt(10) + 1;
        years *= -1;
        Date moveIn = DateUtils.yearsAdd(moveOut, years);
        previousAddress.moveOutDate().setValue(new LogicalDate(moveOut.getTime()));
        previousAddress.moveInDate().setValue(new LogicalDate(moveIn.getTime()));
    }

    private PriorAddress createPriorAddress() {
        PriorAddress address = EntityFactory.create(PriorAddress.class);

        address.set(CommonsGenerator.createRandomAddress().duplicate(PriorAddress.class));

        address.moveInDate().setValue(RandomUtil.randomLogicalDate(2009, 2011));
        address.moveOutDate().setValue(RandomUtil.randomLogicalDate(2011, 2013));

        address.payment().setValue(new BigDecimal(1000 + RandomUtil.randomInt(1000)));

        address.phone().setValue(CommonsGenerator.createPhone());
        address.rented().setValue(RandomUtil.randomEnum(PriorAddress.OwnedRented.class));
        address.propertyCompany().setValue(DataGenerator.randomLastName() + " Inc.");
        address.managerName().setValue("Mr. " + DataGenerator.randomLastName());
        address.managerPhone().setValue(CommonsGenerator.createPhone());
        address.managerEmail().setValue(DataGenerator.randomFirstName().toLowerCase() + "@" + DataGenerator.random(PreloadData.EMAIL_DOMAINS));

        return address;
    }

    private LegalQuestions createLegalQuestions() {
        LegalQuestions lq = EntityFactory.create(LegalQuestions.class);

        lq.suedForDamages().setValue(RandomUtil.randomBoolean());
        lq.suedForRent().setValue(RandomUtil.randomBoolean());
        lq.defaultedOnLease().setValue(RandomUtil.randomBoolean());
        lq.convictedOfFelony().setValue(RandomUtil.randomBoolean());
        lq.everEvicted().setValue(RandomUtil.randomBoolean());
        lq.legalTroubles().setValue(RandomUtil.randomBoolean());
        lq.filedBankruptcy().setValue(RandomUtil.randomBoolean());

        return lq;
    }

    private Collection<PersonalIncome> createIncomes() {
        List<PersonalIncome> incomes = new ArrayList<PersonalIncome>();

        {
            PersonalIncome income = EntityFactory.create(PersonalIncome.class);
            income.incomeSource().setValue(IncomeSource.fulltime);
            income.details().set(createEmployer());
            income.documents().add(createProofOfEmploymentDocument());
            incomes.add(income);
        }
        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            PersonalIncome income = EntityFactory.create(PersonalIncome.class);
            income.incomeSource().setValue(IncomeSource.selfemployed);
            income.details().set(createSelfEmployed());
            income.documents().add(createProofOfEmploymentDocument());
            incomes.add(income);
        }

        return incomes;
    }

    public static IncomeInfoEmployer createEmployer() {
        IncomeInfoEmployer employer = EntityFactory.create(IncomeInfoEmployer.class);

        employer.address().set(CommonsGenerator.createAddress());

        employer.name().setValue(RandomUtil.random(PreloadData.EMPLOYER_NAMES));
        employer.supervisorName().setValue("Mr. " + DataGenerator.randomLastName());
        employer.supervisorPhone().setValue(RandomUtil.randomPhone());
        employer.monthlyAmount().setValue(new BigDecimal(1000 + RandomUtil.randomInt(4000)));
        employer.position().setValue(RandomUtil.random(PreloadData.OCCUPATIONS));

        int startYear = 1990 + RandomUtil.randomInt(20);
        int endYear = startYear + 1 + RandomUtil.randomInt(8);

        employer.starts().setValue(new LogicalDate(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));
        employer.ends().setValue(new LogicalDate(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));

        return employer;
    }

    public static IncomeInfoSelfEmployed createSelfEmployed() {
        IncomeInfoSelfEmployed selfEmpl = EntityFactory.create(IncomeInfoSelfEmployed.class);

        selfEmpl.address().set(CommonsGenerator.createAddress());

        selfEmpl.name().setValue(RandomUtil.random(PreloadData.EMPLOYER_NAMES));
        selfEmpl.supervisorName().setValue("Mr. " + DataGenerator.randomLastName());
        selfEmpl.supervisorPhone().setValue(RandomUtil.randomPhone());
        selfEmpl.monthlyAmount().setValue(new BigDecimal(1000 + RandomUtil.randomInt(4000)));
        selfEmpl.position().setValue(RandomUtil.random(PreloadData.OCCUPATIONS));

        int startYear = 1990 + RandomUtil.randomInt(20);
        int endYear = startYear + 1 + RandomUtil.randomInt(8);

        selfEmpl.starts().setValue(new LogicalDate(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));
        selfEmpl.ends().setValue(new LogicalDate(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));

        return selfEmpl;
    }

    private Collection<PersonalAsset> createAssets(int minAssets) {
        List<PersonalAsset> assets = new ArrayList<PersonalAsset>();
        for (int i = 0; i < 1 + minAssets + RandomUtil.randomInt(3); i++) {
            PersonalAsset asset = EntityFactory.create(PersonalAsset.class);

            asset.assetType().setValue(RandomUtil.random(AssetType.values()));
            asset.percent().setValue((double) RandomUtil.randomInt(100));
            asset.assetValue().setValue(BigDecimal.valueOf(500 + RandomUtil.randomDouble(500)));

            assets.add(asset);
        }
        return assets;
    }

    private Collection<PersonCreditCheck> createPersonCreditCheck() {
        List<PersonCreditCheck> list = new ArrayList<PersonCreditCheck>();
        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            PersonCreditCheck pcc = EntityFactory.create(PersonCreditCheck.class);

            pcc.creditCheckDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));

            pcc.backgroundCheckPolicy().bankruptcy().setValue(RandomUtil.randomEnum(BjccEntry.class));
            pcc.backgroundCheckPolicy().judgment().setValue(RandomUtil.randomEnum(BjccEntry.class));
            pcc.backgroundCheckPolicy().collection().setValue(RandomUtil.randomEnum(BjccEntry.class));
            pcc.backgroundCheckPolicy().chargeOff().setValue(RandomUtil.randomEnum(BjccEntry.class));

            pcc.amountCheked().setValue(BigDecimal.valueOf(500 + RandomUtil.randomDouble(500)));

            pcc.creditCheckResult().setValue(RandomUtil.randomEnum(CreditCheckResult.class));

            switch (pcc.creditCheckResult().getValue()) {
            case Accept:
                pcc.amountApproved().setValue(BigDecimal.valueOf(pcc.amountCheked().getValue().doubleValue() - RandomUtil.randomDouble(500)));
                break;
            case Decline:
            case SoftDecline:
                pcc.declineReason().setValue(CommonsGenerator.lipsumShort());
                break;
            }

            list.add(pcc);
        }
        return list;
    }

    private IdentificationDocument createIdentificationDocument() {
        IdentificationDocument document = EntityFactory.create(IdentificationDocument.class);
        document.idNumber().setValue(RandomUtil.randomLetters(10));
        if (identificationDocumentTypes == null) {
            identificationDocumentTypes = Persistence.service().query(EntityQueryCriteria.create(IdentificationDocumentType.class));
        }
        document.idType().set(RandomUtil.random(identificationDocumentTypes));
        document.documentPages().add(createDocumentPage("doc-security" + RandomUtil.randomInt(3) + ".jpg"));
        return document;
    }

    private ProofOfEmploymentDocument createProofOfEmploymentDocument() {
        ProofOfEmploymentDocument document = EntityFactory.create(ProofOfEmploymentDocument.class);
        document.description().setValue("proof of employment document " + RandomUtil.randomLetters(10));
        document.documentPages().add(createDocumentPage("doc-income" + RandomUtil.randomInt(3) + ".jpg"));
        return document;
    }

    public ApplicationDocumentFile createDocumentPage(String fileName) {
        ApplicationDocumentFile applicationDocument = EntityFactory.create(ApplicationDocumentFile.class);
        applicationDocument.fileName().setValue(fileName);
        return applicationDocument;
    }

    public static void attachDocumentData(PersonScreening screening) {
        for (ApplicationDocument document : screening.documents()) {
            attachDocumentData(document);
        }
        for (PersonalIncome income : screening.version().incomes()) {
            for (ApplicationDocument document : income.documents()) {
                attachDocumentData(document);
            }
        }
    }

    private static void attachDocumentData(ApplicationDocument document) {
        for (ApplicationDocumentFile applicationDocument : document.documentPages()) {
            String fileName = applicationDocument.fileName().getValue();
            ApplicationDocumentBlob applicationDocumentData;
            try {
                byte[] data = IOUtils.getBinaryResource("pt-docs/" + fileName, ScreeningGenerator.class);
                if (data == null) {
                    throw new Error("Could not find DocumentData [" + fileName + "] in classpath");
                }
                String contentType = MimeMap.getContentType(FilenameUtils.getExtension(fileName));
                applicationDocumentData = EntityFactory.create(ApplicationDocumentBlob.class);
                applicationDocumentData.data().setValue(data);
                applicationDocumentData.contentType().setValue(contentType);

            } catch (IOException e) {
                throw new Error("Failed to read the file [" + fileName + "]", e);
            }

            Persistence.service().persist(applicationDocumentData);
            applicationDocument.fileSize().setValue(applicationDocumentData.data().getValue().length);
            applicationDocument.blobKey().set(applicationDocumentData.id());
        }
    }

}
