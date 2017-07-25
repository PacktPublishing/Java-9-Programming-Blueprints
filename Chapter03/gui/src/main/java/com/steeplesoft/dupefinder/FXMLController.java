package com.steeplesoft.dupefinder;

import com.steeplesoft.dupefinder.lib.FileFinder;
import com.steeplesoft.dupefinder.lib.model.FileInfo;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

public class FXMLController implements Initializable {

    @FXML
    private ListView<String> searchPatternsListView;
    @FXML
    private ListView<String> sourceDirsListView;
    @FXML
    private ListView<String> dupeFileGroupListView;
    @FXML
    private ListView<FileInfo> matchingFilesListView;
    @FXML
    private Button addPattern;
    @FXML
    private Button removePattern;
    @FXML
    private Button addPath;
    @FXML
    private Button removePath;
    @FXML
    private Button findFiles;
    @FXML
    private HBox findBox;

    final private ObservableList<String> paths = FXCollections.observableArrayList();
    final private ObservableList<String> patterns = FXCollections.observableArrayList();
    private String lastDir = System.getProperty("user.home");
    private Map<String, List<FileInfo>> dupes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchPatternsListView.setItems(patterns);
        sourceDirsListView.setItems(paths);

        dupeFileGroupListView.setCellFactory((ListView<String> p) -> new ListCell<String>() {
            @Override
            public void updateItem(String string, boolean empty) {
                super.updateItem(string, empty);
                final int index = p.getItems().indexOf(string);
                if (index > -1) {
                    setText("Group #" + (index + 1));
                } else {
                    setText(null);
                }
            }
        });

        matchingFilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        matchingFilesListView.setCellFactory((ListView<FileInfo> p) -> new ListCell<FileInfo>() {
            @Override
            protected void updateItem(FileInfo fileInfo, boolean bln) {
                super.updateItem(fileInfo, bln);
                if (fileInfo != null) {
                    setText(fileInfo.getPath());
                } else {
                    setText(null);
                }
            }

        });

        findFiles.prefWidthProperty().bind(findBox.widthProperty());
    }

    @FXML
    public void closeApplication(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void showAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About...");
        alert.setHeaderText("Packt Publishing Duplicate Finder");
        alert.setContentText("(c) Copyright 2016");
        alert.showAndWait();
    }

    @FXML
    protected void handleButtonAction(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button button = (Button) event.getSource();
            if (button.equals(addPattern)) {
                addPattern();
            } else if (button.equals(removePattern)) {
                removePattern();
            } else if (button.equals(addPath)) {
                addPath();
            } else if (button.equals(removePath)) {
                removePath();
            } else if (button.equals(findFiles)) {
                findFiles();
            }
        }
    }

    @FXML
    public void dupeGroupClicked(MouseEvent event) {
        int index = dupeFileGroupListView.getSelectionModel().getSelectedIndex();
        if (index > -1) {
            String hash = dupeFileGroupListView.getSelectionModel().getSelectedItem();
            matchingFilesListView.getItems().clear();
            matchingFilesListView.getItems().addAll(dupes.get(hash));
        }
    }

    @FXML
    public void openFiles(ActionEvent event) {
        matchingFilesListView.getSelectionModel().getSelectedItems()
                .forEach(f -> {
                    try {
                        Desktop.getDesktop().open(new File(f.getPath()));
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
    }

    @FXML
    public void deleteSelectedFiles(ActionEvent event) {
        final ObservableList<FileInfo> selectedFiles = matchingFilesListView.getSelectionModel().getSelectedItems();
        if (selectedFiles.size() > 0) {
            showConfirmationDialog("Are you sure you want to delete the selected files",
                    () -> selectedFiles.forEach(f -> {
                        if (Desktop.getDesktop().moveToTrash(new File(f.getPath()))) {
                            matchingFilesListView.getItems().remove(f);
                            dupes.get(dupeFileGroupListView.getSelectionModel().getSelectedItem())
                                    .remove(f);
                        }
                    }));
        }
    }

    @FXML
    public void keyPressed(KeyEvent event) {
        dupeGroupClicked(null);
    }

    private void addPattern() {
        TextInputDialog dialog = new TextInputDialog("*.*");
        dialog.setTitle("Add a pattern");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the pattern you wish to add:");

        dialog.showAndWait()
                .filter(n -> n != null && !n.trim().isEmpty())
                .ifPresent(name -> patterns.add(name));
    }

    private void removePattern() {
        if (searchPatternsListView.getSelectionModel().getSelectedIndex() > -1) {
            showConfirmationDialog(
                    "Are you sure you want to remove this pattern?",
                    (() -> patterns.remove(searchPatternsListView
                            .getSelectionModel().getSelectedItem())));
        }
    }

    protected void showConfirmationDialog(String message,
            Runnable action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait()
                .filter(b -> b == ButtonType.OK)
                .ifPresent(b -> action.run());
    }

    private void addPath() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Add Search Path");
        dc.setInitialDirectory(new File(lastDir));
        File dir = dc.showDialog(null);
        if (dir != null) {
            try {
                lastDir = dir.getParent();
                paths.add(dir.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void removePath() {
        showConfirmationDialog("Are you sure you want to remove this path?",
                (() -> paths.remove(sourceDirsListView.getSelectionModel().getSelectedItem())));
    }

    private void findFiles() {
        FileFinder ff = new FileFinder();
        patterns.forEach(p -> ff.addPattern(p));
        paths.forEach(p -> ff.addPath(p));

        ff.find();
        dupes = ff.getDuplicates();
        ObservableList<String> groups = FXCollections.observableArrayList(dupes.keySet());

        dupeFileGroupListView.setItems(groups);
    }
}
