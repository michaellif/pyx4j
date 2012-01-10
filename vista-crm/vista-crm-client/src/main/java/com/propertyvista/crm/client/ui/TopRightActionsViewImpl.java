package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Tooltip;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.shared.CompiledLocale;

public class TopRightActionsViewImpl extends FlowPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    public static String BACK_TO_CRM = "vista_Back2CRM";

    private static I18n i18n = I18n.get(TopRightActionsViewImpl.class);

    private Presenter presenter;

    private final HTML greetings;

    private final CHyperlink logout;

    private final CHyperlink login;

    private final CHyperlink account;

    private final CHyperlink settings;

    private final HorizontalPanel locales;

    private final Image message;

    private final Image alert;

    private final SearchBox search;

    private final Theme otherTheme = Theme.BrownWarm;

    public TopRightActionsViewImpl() {
        setStyleName(CrmSitePanelTheme.StyleName.SiteViewAction.name());
        setSize("100%", "100%");

        HorizontalPanel container = new HorizontalPanel();
        container.getElement().getStyle().setFloat(Style.Float.RIGHT);
        container.setSize("30%", "100%");

        greetings = new HTML("");
        greetings.getElement().getStyle().setDisplay(Display.INLINE);
        greetings.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        greetings.getElement().getStyle().setMarginRight(1, Unit.EM);

        logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        });
        logout.setDebugId(new StringDebugId("logout"));
        logout.setValue(i18n.tr("LogOut"));
        logout.setVisible(false);
        logout.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        login = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.login();
            }
        });
        login.setDebugId(new StringDebugId("login"));
        login.setValue(i18n.tr("Log In"));
        login.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        account = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.showAccount();
            }
        });
        account.setDebugId(new StringDebugId("account"));
        account.setValue(i18n.tr("Account"));
        account.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        settings = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.SwitchCrmAndSettings();
            }
        });
        settings.setDebugId(new StringDebugId("administration"));
        settings.setValue(i18n.tr("Administration"));
        settings.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        locales = new HorizontalPanel();
        locales.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        alert = new Image(CrmImages.INSTANCE.alert());
        alert.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showAlerts();
            }
        });
        //  alert.getElement().getStyle().setMarginRight(1, Unit.EM);
        alert.getElement().getStyle().setCursor(Cursor.POINTER);

        message = new Image(CrmImages.INSTANCE.message());
        message.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showMessages();
            }
        });
        //    message.getElement().getStyle().setMarginRight(1, Unit.EM);
        //   message.getElement().getStyle().setMarginLeft(1, Unit.EM);
        message.getElement().getStyle().setCursor(Cursor.POINTER);

        search = new SearchBox();

        /**
         * the following set of wrappers keep login/logout group relatively steady when
         * the elements right of it disappear
         */
        SimplePanel searchwr = new SimplePanel();
        searchwr.getElement().setAttribute("style", "min-width:12em");
        searchwr.add(search);

        SimplePanel messagewr = new SimplePanel();
        messagewr.getElement().setAttribute("style", "min-width:3em");
        messagewr.add(message);

        SimplePanel alertwr = new SimplePanel();
        alertwr.getElement().setAttribute("style", "min-width:3em");
        alertwr.add(alert);

        container.add(greetings);
        container.add(account);
        container.add(settings);
        container.add(login);
        container.add(logout);
        container.add(searchwr);
        container.add(messagewr);
        container.add(alertwr);
        container.add(locales);

        add(container);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;

        // style settings link:
        if (presenter.getWhere().getClass().getName().contains(CrmSiteMap.Settings.class.getName())) {
            settings.setValue(i18n.tr("Home"));
            settings.asWidget().addStyleName(BACK_TO_CRM);
        } else {
            settings.setValue(i18n.tr("Administration"));
            settings.asWidget().removeStyleName(BACK_TO_CRM);
        }
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        alert.setVisible(false);
        message.setVisible(false);
        search.setVisible(false);
        account.setVisible(false);
        settings.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        alert.setVisible(true);
        message.setVisible(true);
        account.setVisible(true);
        settings.setVisible(true);
        search.setVisible(true);
        greetings.setHTML(i18n.tr("Welcome &nbsp;{0}", userName));
    }

    @Override
    public void setAvailableLocales(List<CompiledLocale> localeList) {
        locales.clear();
        for (final CompiledLocale compiledLocale : localeList) {
            CHyperlink link = new CHyperlink(null, new Command() {
                @Override
                public void execute() {
                    presenter.setLocale(compiledLocale);
                }
            });
            link.setValue(compiledLocale.getLanguage());
            locales.add(link);
            Tooltip.tooltip(link.getWidget(), LocaleInfo.getLocaleNativeDisplayName(compiledLocale.name()));
            locales.add(new Label("/"));
        }
        if (locales.getWidgetCount() > 0) {
            locales.remove(locales.getWidgetCount() - 1);
        }
    }
}
