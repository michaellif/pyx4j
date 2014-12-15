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
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IHasFile;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.blob.IdentificationDocumentBlob;
import com.propertyvista.domain.blob.ProofOfAssetDocumentBlob;
import com.propertyvista.domain.blob.ProofOfIncomeDocumentBlob;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.ProofOfAssetDocumentFile;
import com.propertyvista.domain.media.ProofOfIncomeDocumentFile;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy.BjccEntry;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.CustomerScreeningLegalQuestion;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset.AssetType;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncomeInfo.AmountPeriod;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.misc.CreditCardNumberGenerator;

public class ScreeningGenerator {

    private List<IdentificationDocumentType> identificationDocumentTypes;

    ScreeningGenerator() {

    }

    CustomerScreening createScreening() {
        CustomerScreening screening = EntityFactory.create(CustomerScreening.class);

        // Documents
        screening.version().documents().add(createIdentificationDocument(IdentificationDocumentType.Type.canadianSIN));
        for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
            screening.version().documents().add(createIdentificationDocument(null));
        }

        // Address
        screening.version().currentAddress().set(createPriorAddress());
        screening.version().previousAddress().set(createPriorAddress());
        makeAddressValid(screening.version().currentAddress(), screening.version().previousAddress());

        // Questions
        screening.version().legalQuestions().addAll(createLegalQuestions());

        // Incomes
        screening.version().incomes().addAll(createIncomes());

        // Assets
        int minAssets = (screening.version().incomes().size() == 0) ? 1 : 0;
        screening.version().assets().addAll(createAssets(minAssets));

        screening.creditChecks().addAll(createPersonCreditCheck());

        return screening;
    }

    private void makeAddressValid(PriorAddress currentAddress, PriorAddress previousAddress) {
        currentAddress.moveOutDate().setValue(DataGenerator.randomDateFuture(24)); // this has to be in the future

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

        address.set(CommonsGenerator.createRandomInternationalAddress().duplicate(PriorAddress.class));

        address.moveInDate().setValue(RandomUtil.randomLogicalDate(2009, 2011));
        address.moveOutDate().setValue(RandomUtil.randomLogicalDate(2011, 2013));

        address.payment().setValue(new BigDecimal(1000 + RandomUtil.randomInt(1000)));

        address.rented().setValue(RandomUtil.randomEnum(PriorAddress.OwnedRented.class));
        address.propertyCompany().setValue(DataGenerator.randomLastName() + " Inc.");
        address.managerName().setValue("Mr. " + DataGenerator.randomLastName());
        address.managerPhone().setValue(CommonsGenerator.createPhone());
        address.managerEmail().setValue(DataGenerator.randomFirstName().toLowerCase() + "@" + DataGenerator.random(PreloadData.EMAIL_DOMAINS));

        return address;
    }

    private Collection<CustomerScreeningLegalQuestion> createLegalQuestions() {
        List<CustomerScreeningLegalQuestion> legalQuestion = new ArrayList<CustomerScreeningLegalQuestion>();

        //TODO: generate it somehow according the LegalQuestionsPolicy ?!

        CustomerScreeningLegalQuestion item = EntityFactory.create(CustomerScreeningLegalQuestion.class);
        item.question().setValue("Have you ever been sued for rent?");
        legalQuestion.add(item);

        item = EntityFactory.create(CustomerScreeningLegalQuestion.class);
        item.answer().setValue(RandomUtil.randomBoolean());
        item.question().setValue("Have you ever been sued for damages?");
        legalQuestion.add(item);

        item = EntityFactory.create(CustomerScreeningLegalQuestion.class);
        item.answer().setValue(RandomUtil.randomBoolean());
        item.question().setValue("Have you ever been evicted?");
        legalQuestion.add(item);

        item = EntityFactory.create(CustomerScreeningLegalQuestion.class);
        item.answer().setValue(RandomUtil.randomBoolean());
        item.question().setValue("Have you ever defaulted on a lease?");
        legalQuestion.add(item);

        item = EntityFactory.create(CustomerScreeningLegalQuestion.class);
        item.answer().setValue(RandomUtil.randomBoolean());
        item.question()
                .setValue(
                        "Have you ever been convicted of a crime/felony that involved an offense against property, persons, government officials, or that involved firearms, illegal drugs, or sex or sex crimes?");
        legalQuestion.add(item);

        item = EntityFactory.create(CustomerScreeningLegalQuestion.class);
        item.answer().setValue(RandomUtil.randomBoolean());
        item.question().setValue("Have you ever had any liens, court judgments or repossessions?");
        legalQuestion.add(item);

        item = EntityFactory.create(CustomerScreeningLegalQuestion.class);
        item.answer().setValue(RandomUtil.randomBoolean());
        item.question().setValue("Have you ever filed for bankruptcy protection");
        legalQuestion.add(item);

        return legalQuestion;
    }

    private Collection<CustomerScreeningIncome> createIncomes() {
        List<CustomerScreeningIncome> incomes = new ArrayList<CustomerScreeningIncome>();

        {
            CustomerScreeningIncome income = EntityFactory.create(CustomerScreeningIncome.class);
            income.incomeSource().setValue(IncomeSource.fulltime);
            income.details().set(createEmployer());
            income.files().add(createProofOfIncomeDocument());
            incomes.add(income);
        }
        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            CustomerScreeningIncome income = EntityFactory.create(CustomerScreeningIncome.class);
            income.incomeSource().setValue(IncomeSource.selfemployed);
            income.details().set(createSelfEmployed());
            income.files().add(createProofOfIncomeDocument());
            incomes.add(income);
        }

        return incomes;
    }

    public static IncomeInfoEmployer createEmployer() {
        IncomeInfoEmployer employer = EntityFactory.create(IncomeInfoEmployer.class);

        employer.address().set(CommonsGenerator.createInternationalAddress());

        employer.name().setValue(RandomUtil.random(PreloadData.EMPLOYER_NAMES));
        employer.supervisorName().setValue("Mr. " + DataGenerator.randomLastName());
        employer.supervisorPhone().setValue(RandomUtil.randomPhone());
        employer.incomeAmount().setValue(new BigDecimal(1000 + RandomUtil.randomInt(4000)));
        employer.amountPeriod().setValue(AmountPeriod.Monthly);
        employer.position().setValue(RandomUtil.random(PreloadData.OCCUPATIONS));

        int startYear = 1990 + RandomUtil.randomInt(20);
        int endYear = startYear + 1 + RandomUtil.randomInt(8);

        employer.starts().setValue(new LogicalDate(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));
        employer.ends().setValue(new LogicalDate(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));

        return employer;
    }

    public static IncomeInfoSelfEmployed createSelfEmployed() {
        IncomeInfoSelfEmployed selfEmpl = EntityFactory.create(IncomeInfoSelfEmployed.class);

        selfEmpl.address().set(CommonsGenerator.createInternationalAddress());

        selfEmpl.name().setValue(RandomUtil.random(PreloadData.EMPLOYER_NAMES));
        selfEmpl.supervisorName().setValue("Mr. " + DataGenerator.randomLastName());
        selfEmpl.supervisorPhone().setValue(RandomUtil.randomPhone());
        selfEmpl.incomeAmount().setValue(new BigDecimal(1000 + RandomUtil.randomInt(4000)));
        selfEmpl.amountPeriod().setValue(AmountPeriod.Monthly);
        selfEmpl.position().setValue(RandomUtil.random(PreloadData.OCCUPATIONS));

        int startYear = 1990 + RandomUtil.randomInt(20);
        int endYear = startYear + 1 + RandomUtil.randomInt(8);

        selfEmpl.starts().setValue(new LogicalDate(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));
        selfEmpl.ends().setValue(new LogicalDate(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));

        return selfEmpl;
    }

    private Collection<CustomerScreeningAsset> createAssets(int minAssets) {
        List<CustomerScreeningAsset> assets = new ArrayList<CustomerScreeningAsset>();
        for (int i = 0; i < 1 + minAssets + RandomUtil.randomInt(3); i++) {
            CustomerScreeningAsset asset = EntityFactory.create(CustomerScreeningAsset.class);

            asset.assetType().setValue(RandomUtil.random(AssetType.values()));
            asset.ownership().setValue(new BigDecimal(RandomUtil.randomDouble(1.d, 3)));
            asset.assetValue().setValue(BigDecimal.valueOf(500 + RandomUtil.randomDouble(500)));
            asset.files().add(createProofOfAssetDocument());

            assets.add(asset);
        }
        return assets;
    }

    private Collection<CustomerCreditCheck> createPersonCreditCheck() {
        List<CustomerCreditCheck> list = new ArrayList<CustomerCreditCheck>();
        for (int i = 0; i < 1 + RandomUtil.randomInt(3); i++) {
            CustomerCreditCheck pcc = EntityFactory.create(CustomerCreditCheck.class);

            pcc.creditCheckDate().setValue(RandomUtil.randomDateDaysShifted(-40));

            pcc.backgroundCheckPolicy().bankruptcy().setValue(RandomUtil.randomEnum(BjccEntry.class));
            pcc.backgroundCheckPolicy().judgment().setValue(RandomUtil.randomEnum(BjccEntry.class));
            pcc.backgroundCheckPolicy().collection().setValue(RandomUtil.randomEnum(BjccEntry.class));
            pcc.backgroundCheckPolicy().chargeOff().setValue(RandomUtil.randomEnum(BjccEntry.class));

            pcc.amountChecked().setValue(BigDecimal.valueOf(500 + RandomUtil.randomDouble(500)));

            List<CreditCheckResult> options = new ArrayList<CreditCheckResult>(EnumSet.allOf(CreditCheckResult.class));
            options.remove(CreditCheckResult.Error);
            // Make Accept more frequent
            options.add(CreditCheckResult.Accept);

            pcc.creditCheckResult().setValue(RandomUtil.random(options));

            switch (pcc.creditCheckResult().getValue()) {
            case Accept:
                pcc.amountApproved().setValue(BigDecimal.valueOf(pcc.amountChecked().getValue().doubleValue() - RandomUtil.randomDouble(500)));
                break;
            default:
                pcc.reason().setValue(CommonsGenerator.lipsumShort());
                break;
            }

            list.add(pcc);
        }
        return list;
    }

    private IdentificationDocument createIdentificationDocument(IdentificationDocumentType.Type type) {
        IdentificationDocument document = EntityFactory.create(IdentificationDocument.class);
        if (identificationDocumentTypes == null) {
            identificationDocumentTypes = Persistence.service().query(EntityQueryCriteria.create(IdentificationDocumentType.class));
        }
        if (type == null) {
            document.idType().set(RandomUtil.random(identificationDocumentTypes));
        } else {
            for (IdentificationDocumentType idType : identificationDocumentTypes) {
                if (idType.type().getValue() == type) {
                    document.idType().set(idType);
                    break;
                }
            }
        }

        if (document.idType().type().getValue() == IdentificationDocumentType.Type.canadianSIN) {
            document.idNumber().setValue(CreditCardNumberGenerator.generateCanadianSin());
        } else {
            document.idNumber().setValue(RandomUtil.randomLetters(10));
        }
        document.files().add(createDocumentPage(IdentificationDocumentFile.class, "doc-security" + RandomUtil.randomInt(3) + ".jpg"));
        return document;
    }

    private ProofOfIncomeDocumentFile createProofOfIncomeDocument() {
        ProofOfIncomeDocumentFile document = EntityFactory.create(ProofOfIncomeDocumentFile.class);
        document.description().setValue("Proof of income document " + RandomUtil.randomLetters(10));
        document.file().fileName().setValue("doc-income" + RandomUtil.randomInt(3) + ".jpg");
        return document;
    }

    private ProofOfAssetDocumentFile createProofOfAssetDocument() {
        ProofOfAssetDocumentFile document = EntityFactory.create(ProofOfAssetDocumentFile.class);
        document.description().setValue("Proof of asset document " + RandomUtil.randomLetters(10));
        document.file().fileName().setValue("doc-asset" + RandomUtil.randomInt(3) + ".jpg");
        return document;
    }

    public <T extends IHasFile<?>> T createDocumentPage(Class<T> fileClass, String fileName) {
        T applicationDocument = EntityFactory.create(fileClass);
        applicationDocument.file().fileName().setValue(fileName);
        return applicationDocument;
    }

    public static void attachDocumentData(CustomerScreening screening) {
        for (IdentificationDocument document : screening.version().documents()) {
            attachDocumentData(document);
        }
        for (CustomerScreeningIncome income : screening.version().incomes()) {
            attachDocumentData(income);
        }
        for (CustomerScreeningAsset asset : screening.version().assets()) {
            attachDocumentData(asset);
        }
    }

    private static void attachDocumentData(IdentificationDocument document) {
        for (IdentificationDocumentFile applicationDocument : document.files()) {
            String fileName = applicationDocument.file().fileName().getValue();
            IdentificationDocumentBlob applicationDocumentData;
            try {
                byte[] data = IOUtils.getBinaryResource("pt-docs/" + fileName, ScreeningGenerator.class);
                if (data == null) {
                    throw new Error("Could not find DocumentData [" + fileName + "] in classpath");
                }
                String contentType = MimeMap.getContentType(FilenameUtils.getExtension(fileName));
                applicationDocumentData = EntityFactory.create(IdentificationDocumentBlob.class);
                applicationDocumentData.data().setValue(data);
                applicationDocumentData.contentType().setValue(contentType);

            } catch (IOException e) {
                throw new Error("Failed to read the file [" + fileName + "]", e);
            }

            Persistence.service().persist(applicationDocumentData);
            applicationDocument.file().fileSize().setValue(applicationDocumentData.data().getValue().length);
            applicationDocument.file().blobKey().set(applicationDocumentData.id());
            FileUploadRegistry.register(applicationDocument.file());
        }
    }

    private static void attachDocumentData(CustomerScreeningIncome income) {
        for (ProofOfIncomeDocumentFile applicationDocument : income.files()) {
            String fileName = applicationDocument.file().fileName().getValue();
            ProofOfIncomeDocumentBlob applicationDocumentData;
            try {
                byte[] data = IOUtils.getBinaryResource("pt-docs/" + fileName, ScreeningGenerator.class);
                if (data == null) {
                    throw new Error("Could not find DocumentData [" + fileName + "] in classpath");
                }
                String contentType = MimeMap.getContentType(FilenameUtils.getExtension(fileName));
                applicationDocumentData = EntityFactory.create(ProofOfIncomeDocumentBlob.class);
                applicationDocumentData.data().setValue(data);
                applicationDocumentData.contentType().setValue(contentType);

            } catch (IOException e) {
                throw new Error("Failed to read the file [" + fileName + "]", e);
            }

            Persistence.service().persist(applicationDocumentData);
            applicationDocument.file().fileSize().setValue(applicationDocumentData.data().getValue().length);
            applicationDocument.file().blobKey().set(applicationDocumentData.id());
            FileUploadRegistry.register(applicationDocument.file());
        }
    }

    private static void attachDocumentData(CustomerScreeningAsset document) {
        for (ProofOfAssetDocumentFile applicationDocument : document.files()) {
            String fileName = applicationDocument.file().fileName().getValue();
            ProofOfAssetDocumentBlob applicationDocumentData;
            try {
                byte[] data = IOUtils.getBinaryResource("pt-docs/" + fileName, ScreeningGenerator.class);
                if (data == null) {
                    throw new Error("Could not find DocumentData [" + fileName + "] in classpath");
                }
                String contentType = MimeMap.getContentType(FilenameUtils.getExtension(fileName));
                applicationDocumentData = EntityFactory.create(ProofOfAssetDocumentBlob.class);
                applicationDocumentData.data().setValue(data);
                applicationDocumentData.contentType().setValue(contentType);

            } catch (IOException e) {
                throw new Error("Failed to read the file [" + fileName + "]", e);
            }

            Persistence.service().persist(applicationDocumentData);
            applicationDocument.file().fileSize().setValue(applicationDocumentData.data().getValue().length);
            applicationDocument.file().blobKey().set(applicationDocumentData.id());
            FileUploadRegistry.register(applicationDocument.file());
        }
    }
}
