package com.steeplesoft.mailfilter.gui;

import com.steeplesoft.mailfilter.AccountService;
import com.steeplesoft.mailfilter.model.Account;
import com.steeplesoft.mailfilter.model.Rule;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import jfxtras.labs.scene.control.BeanPathAdapter;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

public class Controller implements Initializable {

    private final ObservableList<Account> accounts = FXCollections.observableArrayList();
    private final ObservableList<Rule> rules = FXCollections.observableArrayList();

    @FXML
    private TextField serverName;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    @FXML
    private CheckBox useSsl;
    @FXML
    private TableView rulesTableView;
    @FXML
    private ChoiceBox<String> type;
    @FXML
    private TextField sourceFolder;
    @FXML
    private TextField destFolder;
    @FXML
    private TextField matchingText;
    @FXML
    private TextField age;
    @FXML
    private CheckListView fields;
    @FXML
    private ListView<Account> accountsListView;

    private AccountService accountService;
    private AccountProperty accountProperty;
    private RuleProperty ruleProperty;
    private boolean isSelectingNewRule = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureAccountsListView();
        configureRulesListTable();
        configureRuleFields();
    }

    @FXML
    public void saveAccounts(ActionEvent e) {
        accountService.saveAccounts(accounts);
    }

    @FXML
    public void addAccount(ActionEvent e) {
        final Account account = new Account();
        account.setUserName("<new address>");
        account.setServerName("<new server>");
        accounts.add(account);
        accountProperty.set(account);
    }

    @FXML
    public void removeAccount(ActionEvent e) {
        int index = accountsListView.getSelectionModel().getSelectedIndex();
        Account account = accountsListView.getSelectionModel().getSelectedItem();
        accounts.remove(account);
        accountsListView.getSelectionModel().select(index > 1 ? index - 1 : 0);
    }

    @FXML
    public void addRule(ActionEvent e) {

    }

    @FXML
    public void removeRule(ActionEvent e) {

    }

    private void configureAccountsListView() {
        accountService = new AccountService();
        accounts.addAll(accountService.getAccounts());
        accountsListView.setItems(accounts);

        accountsListView.setCellFactory((ListView<Account> p) -> new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean bln) {
                super.updateItem(account, bln);
                if (account != null) {
                    setText(String.format("%s on %s", account.getUserName(), account.getServerName()));
                } else {
                    setText(null);
                }
            }
        });

        accountProperty = new AccountProperty();
        accountsListView.setOnMouseClicked(e -> {
            final Account account = accountsListView.getSelectionModel().getSelectedItem();
            if (account != null) {
                accountProperty.set(account);
            }
        });

        final ChangeListener<String> accountChangeListener = (observable, oldValue, newValue) -> accountsListView.refresh();
        serverName.textProperty().addListener(accountChangeListener);
        userName.textProperty().addListener(accountChangeListener);
    }

    private void configureRulesListTable() {
        ObservableList<TableColumn> columns = rulesTableView.getColumns();
        final TableColumn<Rule, String> typeCol = columns.get(0);
        typeCol.prefWidthProperty().bind(rulesTableView.widthProperty().multiply(0.12));
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType().toString()));

        final TableColumn<Rule, String> sourceCol = columns.get(1);
        sourceCol.prefWidthProperty().bind(rulesTableView.widthProperty().multiply(0.25));
        sourceCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSourceFolder()));

        final TableColumn<Rule, String> descCol = columns.get(2);
        descCol.prefWidthProperty().bind(rulesTableView.widthProperty().multiply(0.65));
        descCol.setCellValueFactory(new RuleDescriptionFactory());
        rulesTableView.setItems(rules);

        rulesTableView.setOnMouseClicked(e -> {
            ruleProperty.set((Rule) rulesTableView.getSelectionModel().getSelectedItem());
        });
    }

    private void configureRuleFields() {
        ruleProperty = new RuleProperty();
        fields.getCheckModel().getCheckedItems().addListener(new RuleFieldChangeListener());

        final ChangeListener<Object> ruleChangeListener = 
            (observable, oldValue, newValue) -> rulesTableView.refresh();
        sourceFolder.textProperty().addListener(ruleChangeListener);
        destFolder.textProperty().addListener(ruleChangeListener);
        matchingText.textProperty().addListener(ruleChangeListener);
        age.textProperty().addListener(ruleChangeListener);
        type.getSelectionModel().selectedIndexProperty().addListener(ruleChangeListener);
    }

    private class AccountProperty extends ObjectPropertyBase<Account> {

        private final BeanPathAdapter<Account> pathAdapter;

        public AccountProperty() {
            pathAdapter = new BeanPathAdapter<>(new Account());
            pathAdapter.bindBidirectional("serverName", serverName.textProperty());
            pathAdapter.bindBidirectional("serverPort", serverPort.textProperty());
            pathAdapter.bindBidirectional("useSsl", useSsl.selectedProperty(), Boolean.class);
            pathAdapter.bindBidirectional("userName", userName.textProperty());
            pathAdapter.bindBidirectional("password", password.textProperty());
            addListener((observable, oldValue, newValue) -> {
//            accountsListView.refresh();
                rules.setAll(newValue.getRules());
            });
        }

        @Override
        public void set(Account newValue) {
            pathAdapter.setBean(newValue);
            super.set(newValue);
        }

        @Override
        public Account getBean() {
            return getValue();
        }

        @Override
        public String getName() {
            return getBean().getServerName();
        }

    }

    private class RuleProperty extends ObjectPropertyBase<Rule> {
        private final BeanPathAdapter<Rule> pathAdapter;

        public RuleProperty() {
            pathAdapter = new BeanPathAdapter<>(new Rule());
            pathAdapter.bindBidirectional("sourceFolder", sourceFolder.textProperty());
            pathAdapter.bindBidirectional("destFolder", destFolder.textProperty());
            pathAdapter.bindBidirectional("olderThan", age.textProperty());
            pathAdapter.bindBidirectional("matchingText", matchingText.textProperty());
            pathAdapter.bindBidirectional("type", type.valueProperty(), String.class);
            addListener((observable, oldValue, newValue) -> {
                isSelectingNewRule = true;
                type.getSelectionModel().select(type.getItems().indexOf(newValue.getType().name()));

                IndexedCheckModel checkModel = fields.getCheckModel();
                checkModel.clearChecks();
                newValue.getFields().forEach((field) -> {
                    checkModel.check(checkModel.getItemIndex(field));
                });
                isSelectingNewRule = false;
            });
        }

        @Override
        public void set(Rule newValue) {
            pathAdapter.setBean(newValue);
            super.set(newValue);
        }

        @Override
        public Rule getBean() {
            return getValue();
        }

        @Override
        public String getName() {
            return "";
        }
    }

    private class RuleFieldChangeListener implements ListChangeListener {

        @Override
        public void onChanged(ListChangeListener.Change c) {
            if (!isSelectingNewRule && c.next()) {
                final Rule bean = ruleProperty.getBean();
                bean.getFields().removeAll(c.getRemoved());
                bean.getFields().addAll(c.getAddedSubList());
            }
        }
    }
}
