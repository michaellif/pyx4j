/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import java.util.Vector;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ListBox;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.portal.client.themes.TenantDashboardTheme;
import com.propertyvista.portal.rpc.portal.dto.MaintenanceRequestDTO;

public class NewMaintenanceRequestViewImpl extends CEntityDecoratableEditor<MaintenanceRequestDTO> implements NewMaintenanceRequestView {

    private static I18n i18n = I18n.get(NewMaintenanceRequestViewImpl.class);

    private Presenter presenter;

    private Selector<IssueElement> issueElementSelector;

    private Selector<IssueRepairSubject> issueRepairSubjectSelector;

    private Selector<IssueSubjectDetails> issueSubjectDetailsSelector;

    private Selector<IssueClassification> issueClassificationSelector;

    public NewMaintenanceRequestViewImpl() {
        super(MaintenanceRequestDTO.class);
        initContent();
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel content = new FormFlexPanel();
        content.getColumnFormatter().setWidth(0, "75px");
        content.getColumnFormatter().setWidth(1, "75px");
        content.getColumnFormatter().setWidth(2, "75px");
        content.getColumnFormatter().setWidth(3, "75px");

        int row = -1;

        // Issue Type Selector Header
        content.setHTML(++row, 0, i18n.tr("Rooms"));
        content.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

        content.setHTML(row, 1, i18n.tr("Repair Subject"));
        content.setHTML(row, 2, i18n.tr("Subject Details"));
        content.setHTML(row, 3, i18n.tr("Issue"));

        content.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

        // Issue Type Selector
        issueElementSelector = new Selector<IssueElement>();
        issueElementSelector.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                presenter.onIssueElementSelection(issueElementSelector.getSelectedItem());
            }
        });
        content.setWidget(++row, 0, issueElementSelector);

        issueRepairSubjectSelector = new Selector<IssueRepairSubject>();
        issueRepairSubjectSelector.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                presenter.onIssueRepairSubjectSelection(issueRepairSubjectSelector.getSelectedItem());
            }
        });
        content.setWidget(row, 1, issueRepairSubjectSelector);

        issueSubjectDetailsSelector = new Selector<IssueSubjectDetails>();
        issueSubjectDetailsSelector.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                presenter.onSubjectDetailsSelection(issueSubjectDetailsSelector.getSelectedItem());
            }
        });

        content.setWidget(row, 2, issueSubjectDetailsSelector);

        issueClassificationSelector = new Selector<IssueClassification>();
        content.setWidget(row, 3, issueClassificationSelector);

        // Description
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());
        content.getFlexCellFormatter().setColSpan(row, 0, 4);

        Button submitButton = new Button(i18n.tr("Submit"));
        submitButton.getElement().getStyle().setMargin(20, Unit.PX);
        submitButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        submitButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.submit();
            }
        });
        content.setWidget(++row, 0, submitButton);
        content.getFlexCellFormatter().setColSpan(row, 0, 4);

        return content;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateIssueElementSelector(Vector<IssueElement> IssueElements) {
        issueElementSelector.clear();
        issueRepairSubjectSelector.clear();
        issueSubjectDetailsSelector.clear();
        issueClassificationSelector.clear();
        for (IssueElement issueElement : IssueElements) {
            issueElementSelector.addItem(issueElement, issueElement.name().getStringView());
        }
    }

    @Override
    public void updateIssueRepairSubjectSelector(IList<IssueRepairSubject> subjects) {
        issueRepairSubjectSelector.clear();
        issueSubjectDetailsSelector.clear();
        issueClassificationSelector.clear();
        for (IssueRepairSubject subject : subjects) {
            issueRepairSubjectSelector.addItem(subject, subject.name().getStringView());
        }
    }

    @Override
    public void updateIssueSubjectDetailsSelector(IList<IssueSubjectDetails> details) {
        issueSubjectDetailsSelector.clear();
        issueClassificationSelector.clear();
        for (IssueSubjectDetails detail : details) {
            issueSubjectDetailsSelector.addItem(detail, detail.name().getStringView());
        }
    }

    @Override
    public void updateIssueClassificationSelector(IList<IssueClassification> classifications) {
        issueClassificationSelector.clear();
        for (IssueClassification classification : classifications) {
            issueClassificationSelector.addItem(classification, classification.issue().getStringView());
        }
    }

    class Selector<E extends IEntity> extends ListBox {

        private final Vector<E> values = new Vector<E>();

        Selector() {
            super(true);
            setWidth("100%");
            setHeight("250px");
            getElement().getStyle().setProperty("overflow", "auto");
            getElement().getStyle().setProperty("background", "white");
        }

        @Override
        public void clear() {
            super.clear();
            values.clear();
        }

        void addItem(E entity, String label) {
            values.add(entity);
            super.addItem(label);
        }

        E getSelectedItem() {
            return values.get(getSelectedIndex());
        }
    }

}
