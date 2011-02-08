package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CHyperlink;

public class TopRightActionsViewImpl extends SimplePanel implements TopRightActionsView {

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private Presenter presenter;

    public TopRightActionsViewImpl() {

        HorizontalPanel linksPanel = new HorizontalPanel();

        CHyperlink privacy = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                //empty
            }
        });
        privacy.setDebugId(new StringDebugId("privacy"));
        privacy.setValue(i18n.tr("Privacy Policy"));
        linksPanel.add(privacy);

        linksPanel.add(new HTML("&nbsp;-&nbsp;"));

        CHyperlink terms = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                //empty
            }
        });
        terms.setDebugId(new StringDebugId("terms"));
        terms.setValue(i18n.tr("Terms and Conditions"));
        linksPanel.add(terms);

        linksPanel.add(new HTML("&nbsp;-&nbsp;"));

        CHyperlink logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
            }
        });
        logout.setDebugId(new StringDebugId("logout"));
        logout.setValue(i18n.tr("LogOut"));
        linksPanel.add(logout);
        linksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        linksPanel.getElement().getStyle().setFontSize(0.9, Unit.EM);

        setWidget(linksPanel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
