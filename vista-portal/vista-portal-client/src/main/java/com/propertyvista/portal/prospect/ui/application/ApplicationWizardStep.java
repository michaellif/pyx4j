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
package com.propertyvista.portal.prospect.ui.application;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public abstract class ApplicationWizardStep extends WizardStep {

    private ApplicationWizard wizard;

    private final OnlineApplicationWizardStepMeta meta;

    public ApplicationWizardStep(OnlineApplicationWizardStepMeta name) {
        this.meta = name;
    }

    public void init(ApplicationWizard wizard) {
        this.wizard = wizard;
        Widget content = createStepContent();
        setStepContent(content);
        setStepTitle(meta.toString());
    }

    public ApplicationWizard getWizard() {
        return wizard;
    }

    abstract public Widget createStepContent();

    public void addValidations() {
    }

    public void onValueSet(boolean populate) {
    }

    // helpers:

    public ApplicationWizardView getView() {
        return (ApplicationWizardView) getWizard().getView();
    }

    public OnlineApplicationDTO getValue() {
        return getWizard().getValue();
    }

    public OnlineApplicationDTO proto() {
        return getWizard().proto();
    }

    public CComponent<?, ?> inject(IObject<?> member) {
        return getWizard().inject(member);
    }

    public CField<?, ?> inject(IObject<?> member, IDecorator<CField<?, ?>> decorator) {
        return getWizard().inject(member, decorator);
    }

    public CComponent<?, ?> inject(IObject<?> member, CComponent<?, ?> comp) {
        return getWizard().inject(member, comp);
    }

    public CField<?, ?> inject(IObject<?> member, CField<?, ?> comp, IDecorator<CField<?, ?>> decorator) {
        return getWizard().inject(member, comp, decorator);
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member) {
        return inject(member, new FieldDecoratorBuilder().build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, String labelWidth, String componentWidth, String contentWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, int labelWidth, int componentWidth, int contentWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, int labelWidth, int componentWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth, componentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, int labelWidth) {
        return inject(member, new FieldDecoratorBuilder(labelWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp) {
        return inject(member, comp, new FieldDecoratorBuilder().build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp, String labelWidth, String componentWidth, String contentWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp, int labelWidth, int componentWidth, int contentWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth, componentWidth, contentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp, int labelWidth, int componentWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth, componentWidth).build());
    }

    public final CField<?, ?> injectAndDecorate(IObject<?> member, CField<?, ?> comp, int labelWidth) {
        return inject(member, comp, new FieldDecoratorBuilder(labelWidth).build());
    }

    public <T extends IEntity> CComponent<?, T> get(T member) {
        return getWizard().get(member);
    }

    public <T extends IEntity> CComponent<?, List<T>> get(IList<T> member) {
        return getWizard().get(member);
    }

    public <T> CComponent<?, T> get(IObject<T> member) {
        return getWizard().get(member);
    }

    public OnlineApplicationWizardStepMeta getOnlineApplicationWizardStepMeta() {
        return meta;
    }
}