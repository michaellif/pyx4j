/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import java.io.IOException;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.policy.policies.LeaseAgreementLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm.TargetRole;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class LeaseAgreementPolicyPreloader extends AbstractPolicyPreloader<LeaseAgreementLegalPolicy> {

    public LeaseAgreementPolicyPreloader() {
        super(LeaseAgreementLegalPolicy.class);
    }

    @Override
    protected LeaseAgreementLegalPolicy createPolicy(StringBuilder log) {
        LeaseAgreementLegalPolicy policy = EntityFactory.create(LeaseAgreementLegalPolicy.class);

        // add legal terms
        policy.legal().add(createTerm("1. Agreement/Definitions", "leaseAgreementTerm1.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("2. Possession", "leaseAgreementTerm2.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("3. Special Promotions", "leaseAgreementTerm3.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(
                createTerm("4. Security Deposit/Pet Deposit (Where Applicable)", "leaseAgreementTerm4.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("5. Delay in Delivery", "leaseAgreementTerm5.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("6. Parking", "leaseAgreementTerm6.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(
                createTerm("7. Abandonment of Premises by Tenant/Liquidated Damages", "leaseAgreementTerm7.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("8. Condition of Premises/Inspections", "leaseAgreementTerm8.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("9. Payment of Rent", "leaseAgreementTerm9.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("10. Arrears/Insufficient Funds/Bankruptcy", "leaseAgreementTerm10.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("11. Utilities", "leaseAgreementTerm11.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("12. Rent Increases", "leaseAgreementTerm12.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("13. Additional Occupants", "leaseAgreementTerm13.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("14. Use of Rental Unit", "leaseAgreementTerm14.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("15. Moving", "leaseAgreementTerm15.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("16. Assign or Sublet", "leaseAgreementTerm16.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("17. Where Spouse Obtains \"Tenant\" Status", "leaseAgreementTerm17.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("18. Conduct", "leaseAgreementTerm18.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("19. Pets", "leaseAgreementTerm19.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("20. Occupants and Invited Guests", "leaseAgreementTerm20.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("21. Storage/Bicycles", "leaseAgreementTerm21.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("22. Waterbeds or Liquid Filled Items", "leaseAgreementTerm22.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("23. Waste Management", "leaseAgreementTerm23.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("24. Floors", "leaseAgreementTerm24.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("25. Common Areas/Laundry Rooms", "leaseAgreementTerm25.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("26. Outside", "leaseAgreementTerm26.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("27. Repairs", "leaseAgreementTerm27.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("28. Hazards", "leaseAgreementTerm28.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("29. Liability and Insurance", "leaseAgreementTerm29.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("30. Locks", "leaseAgreementTerm30.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("31. Entry of Rental Unit by Landlord", "leaseAgreementTerm31.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("32. Ending the Tenancy", "leaseAgreementTerm32.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("33. Overholding", "leaseAgreementTerm33.html", TargetRole.Applicant, SignatureFormat.None));
        policy.legal().add(createTerm("34. Rental Application", "leaseAgreementTerm34.html", TargetRole.Applicant, SignatureFormat.FullName));

        // add confirmation terms
        policy.confirmation().add(createTerm("Privacy Policy", "leaseAgreementConfirm1.html", SignatureFormat.AgreeBox));
        policy.confirmation().add(createTerm("Digital Signature", "leaseAgreementConfirm2.html", SignatureFormat.FullName));

        return policy;
    }

    private LeaseAgreementLegalTerm createTerm(String caption, String termsSourceFile, TargetRole role, SignatureFormat format) {
        String termsContent = getTermsContent(termsSourceFile);

        LeaseAgreementLegalTerm term = EntityFactory.create(LeaseAgreementLegalTerm.class);
        term.title().setValue(caption);
        term.body().setValue(termsContent);
        term.signatureFormat().setValue(format);
        return term;
    }

    private LeaseAgreementConfirmationTerm createTerm(String caption, String termsSourceFile, SignatureFormat format) {
        String termsContent = getTermsContent(termsSourceFile);

        LeaseAgreementConfirmationTerm term = EntityFactory.create(LeaseAgreementConfirmationTerm.class);
        term.title().setValue(caption);
        term.body().setValue(termsContent);
        term.signatureFormat().setValue(format);
        return term;
    }

    private String getTermsContent(String termsSourceFile) {
        String termsContent;
        try {
            termsContent = IOUtils.getUTF8TextResource(termsSourceFile, LeaseAgreementPolicyPreloader.class);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (termsContent == null) {
            throw new Error("Resource " + termsSourceFile + " not found.");
        }
        return termsContent;
    }
}
