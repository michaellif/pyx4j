package com.propertyvista.operations.client.ui;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.layout.RiaLayoutPanelTheme;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class HeaderViewImpl extends FlowPanel implements HeaderView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    private static final I18n i18n = I18n.get(HeaderViewImpl.class);

    private Presenter presenter;

    private HTML greetings;

    private Anchor logout;

    private Anchor login;

    private Anchor account;

    private Anchor settings;

    public HeaderViewImpl() {
        setStyleName(RiaLayoutPanelTheme.StyleName.SiteViewHeader.name());
        setSize("100%", "100%");

        add(createLogoContainer());

        add(createActionsContainer());

    }

    private Widget createLogoContainer() {
        SimplePanel logoContainer = new SimplePanel();
        HTML logo = new HTML("<h1>Property Vista Software - Operations</h1>");
        logo.getElement().getStyle().setCursor(Cursor.POINTER);
        logo.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.navigToLanding();
            }
        });
        logoContainer.setWidget(logo);
        return logoContainer;
    }

    private Widget createActionsContainer() {

        Toolbar toolbar = new Toolbar();
        toolbar.addStyleName(RiaLayoutPanelTheme.StyleName.SiteViewAction.name());

        greetings = new HTML("");
        greetings.getElement().getStyle().setDisplay(Display.INLINE);
        greetings.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        greetings.getElement().getStyle().setMarginRight(1, Unit.EM);

        logout = new Anchor(null);
        logout.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.logout();
            }
        });
        logout.ensureDebugId("logout");
        logout.setHTML(i18n.tr("LogOut"));
        logout.setVisible(false);
        logout.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        login = new Anchor(null);
        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.login();
            }
        });

        login.ensureDebugId("login");
        login.setHTML(i18n.tr("Log In"));
        login.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        account = new Anchor(null);
        account.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showAccount();
            }
        });

        account.ensureDebugId("account");
        account.setHTML(i18n.tr("Account"));
        account.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        settings = new Anchor(null);
        settings.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showSettings();
            }
        });

        toolbar.add(greetings);
        toolbar.add(account);
        toolbar.add(settings);
        toolbar.add(login);
        toolbar.add(logout);

        return toolbar;
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        account.setVisible(false);
        settings.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        account.setVisible(true);
        settings.setVisible(true);
        greetings.setHTML(i18n.tr("Welcome &nbsp;{0}", userName));
    }
}
