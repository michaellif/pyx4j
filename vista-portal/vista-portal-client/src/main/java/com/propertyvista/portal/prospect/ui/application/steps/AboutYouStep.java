/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.shared.services.CustomerPicturePortalUploadService;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.portal.shared.ui.util.editors.IdUploaderFolder;
import com.propertyvista.portal.shared.ui.util.editors.NameEditor;

public class AboutYouStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(AboutYouStep.class);

    private final IdUploaderFolder fileUpload = new IdUploaderFolder();

    public AboutYouStep() {
        super(OnlineApplicationWizardStepMeta.AboutYou);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        CImage imageHolder = new CImage(GWT.<CustomerPicturePortalUploadService> create(CustomerPicturePortalUploadService.class), new VistaFileURLBuilder(
                CustomerPicture.class));
        imageHolder.setImageSize(150, 200);
        imageHolder.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.profilePicture()));
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().picture().file(), imageHolder)).customLabel("").build());

        panel.setH3(++row, 0, 1, i18n.tr("Personal Information"));
        panel.setWidget(++row, 0, inject(proto().applicant().person().name(), new NameEditor(i18n.tr("Full Name"))));
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().sex()), 100).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().birthDate()), 150).build());

        panel.setH3(++row, 0, 1, i18n.tr("Contact Information"));
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().homePhone()), 180).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().mobilePhone()), 180).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().workPhone()), 180).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().applicant().person().email()), 230).build());

        panel.setH3(++row, 0, 1, i18n.tr("Identification Documents"));
        panel.setWidget(++row, 0, 2, inject(proto().applicant().documents(), fileUpload));

        return panel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);
        if (getWizard().isEditable()) {
            fileUpload.setDocumentsPolicy(getValue().applicant().documentsPolicy());
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().applicant().person().birthDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && getValue() != null) {
                    if (enforceAgeOfMajority()) {
                        if (!TimeUtils.isOlderThan(value, ageOfMajority())) {
                            return new ValidationError(component, i18n.tr(
                                    "You are too young to be an Applicant or co-Applicant: the minimum age required is {0}.", ageOfMajority()));
                        }
                    }
                }
                return null;
            }
        });
    }

    public Integer ageOfMajority() {
        return getWizard().getValue().ageOfMajority().getValue();
    }

    public boolean enforceAgeOfMajority() {
        return getWizard().getValue().enforceAgeOfMajority().isBooleanTrue();
    }
}
