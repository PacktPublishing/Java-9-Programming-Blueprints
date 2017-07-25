package com.steeplesoft.mailfilter.gui;

import com.steeplesoft.mailfilter.model.Rule;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author jason
 */
public class RuleDescriptionFactory implements Callback<TableColumn.CellDataFeatures<Rule, String>, ObservableValue<String>> {
    
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Rule, String> param) {
        String desc = "";
        final Rule rule = param.getValue();
        final String matchingText = rule.getMatchingText();
        switch (rule.getType()) {
            case MOVE:
                desc = (matchingText != null) ? String.format("Move emails matching '%s' to '%s'", matchingText, rule.getDestFolder()) : String.format("Move emails older than %s to '%s'", rule.getOlderThan(), rule.getDestFolder());
                break;
            case DELETE:
                desc = (matchingText != null) ? String.format("Delete emails matching '%s'", matchingText) : String.format("Delete emails older than %s", rule.getOlderThan());
                break;
        }
        return new SimpleStringProperty(desc);
    }
    
}
