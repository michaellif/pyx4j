/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 6, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.maintenance;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CListBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestForm extends CEntityDecoratableForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestForm.class);

    private static String defaultChoice = i18n.tr("Select");

    private CEntityComboBox<IssueElement> mainCombo;

    public MaintenanceRequestForm() {
        super(MaintenanceRequestDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;

        content.setBR(++row, 0, 1);

        // Add components
        final OptionsDrivenCombo<IssueElement> combo1 = new OptionsDrivenCombo<IssueElement>(IssueElement.class) {
            @Override
            protected boolean isLeaf(IssueElement opt) {
                return !opt.isEmpty() && opt.name().isNull();
            }
        };
        final OptionsDrivenCombo<IssueRepairSubject> combo2 = new OptionsDrivenCombo<IssueRepairSubject>(IssueRepairSubject.class) {
            @Override
            protected boolean isLeaf(IssueRepairSubject opt) {
                return !opt.isEmpty() && opt.name().isNull();
            }
        };
        final OptionsDrivenCombo<IssueSubjectDetails> combo3 = new OptionsDrivenCombo<IssueSubjectDetails>(IssueSubjectDetails.class) {
            @Override
            protected boolean isLeaf(IssueSubjectDetails opt) {
                return !opt.isEmpty() && opt.name().isNull();
            }
        };
        final OptionsDrivenCombo<IssueClassification> combo4 = new OptionsDrivenCombo<IssueClassification>(IssueClassification.class) {
            @Override
            protected boolean isLeaf(IssueClassification opt) {
                return !opt.isEmpty() && opt.issue().isNull();
            }
        };

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails().subject().issueElement(), combo1), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails().subject(), combo2), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification().subjectDetails(), combo3), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().issueClassification(), combo4), 15).build());

        // clear values for dependable selectors
        final String defaultRepairSubject = defaultChoice + " " + combo1.getTitle();
        final String defaultSubjectDetails = defaultChoice + " " + combo2.getTitle();
        final String defaultClassification = defaultChoice + " " + combo3.getTitle();

        combo1.setNoSelectionText(defaultChoice);
        combo2.optionsClear(defaultRepairSubject);
        combo3.optionsClear(defaultSubjectDetails);
        combo4.optionsClear(defaultClassification);

        // add onChange handlers
        combo1.addValueChangeHandler(new ValueChangeHandler<IssueElement>() {
            @Override
            public void onValueChange(ValueChangeEvent<IssueElement> event) {
                if (event.getValue() != null) {
                    combo2.optionsReset(PropertyCriterion.eq(combo2.proto().issueElement(), event.getValue()), defaultChoice);
                } else {
                    combo2.optionsClear(defaultRepairSubject);
                }
                // clear remaining selectors
                combo3.optionsClear(defaultSubjectDetails);
                combo4.optionsClear(defaultClassification);
            }
        });

        combo2.addValueChangeHandler(new ValueChangeHandler<IssueRepairSubject>() {
            @Override
            public void onValueChange(ValueChangeEvent<IssueRepairSubject> event) {
                if (event.getValue() != null) {
                    combo3.optionsReset(PropertyCriterion.eq(combo3.proto().subject(), event.getValue()), defaultChoice);
                } else {
                    combo3.optionsClear(defaultSubjectDetails);
                }
                // clear remaining selectors
                combo4.optionsClear(defaultClassification);
            }
        });

        combo3.addValueChangeHandler(new ValueChangeHandler<IssueSubjectDetails>() {
            @Override
            public void onValueChange(ValueChangeEvent<IssueSubjectDetails> event) {
                if (event.getValue() != null) {
                    combo4.optionsReset(PropertyCriterion.eq(combo4.proto().subjectDetails(), event.getValue()), defaultChoice);
                } else {
                    combo4.optionsClear(defaultClassification);
                }
            }
        });

        // Description
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());

        this.mainCombo = combo1;
        return content;
    }

    @Override
    public void reset() {
        mainCombo.setValue(null);
        super.reset();
    }

    abstract class OptionsDrivenCombo<E extends IEntity> extends CEntityComboBox<E> {
        public OptionsDrivenCombo(Class<E> entityClass) {
            super(entityClass);
        }

        protected abstract boolean isLeaf(E opt);

        public void optionsReset(PropertyCriterion crit, final String title) {
//            System.out.println("===> " + getTitle() + ": optionsReset()");
            optionsSet(crit, title);
        }

        private void optionsSet(PropertyCriterion crit, final String title) {
            this.resetCriteria();
            this.addCriterion(crit);
            this.retriveOptions(new AsyncOptionsReadyCallback<E>() {
                @Override
                public void onOptionsReady(List<E> opt) {
//                    System.out.println("===> " + getTitle() + ": got " + opt.size() + " opts");
                    boolean autoComplete = (opt != null && opt.size() == 1 && isLeaf(opt.get(0)));
                    if (autoComplete) {
                        setVisible(false);
                        // auto-load options for next selector
                        setValue(opt.get(0));
//                        System.out.println("===> " + getTitle() + ": autoselect " + opt.get(0).getPrimaryKey());
                    } else {
                        boolean isActive = (opt != null && opt.size() > 0 && !isLeaf(opt.get(0)));
                        setVisible(isActive);
                        setMandatory(isActive);
                        if (isActive) {
                            setNoSelectionText(title);
                            // clear options for next selector
                            setValue(null);
                        }
                    }
                }
            });
        }

        public void optionsClear(String title) {
//            System.out.println("===> " + getTitle() + ": optionsClear()");
            optionsSet(PropertyCriterion.eq(this.proto().id(), (Serializable) null), title);
        }
    }
}
