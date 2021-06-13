package gui;

import DSMData.DataHandler;
import IOHandler.IOHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.util.Vector;

public class ToolbarHandler {
    private VBox layout;

    private Button addMatrixItem;
    private Button deleteMatrixItem;
    private Button renameMatrixItem;
    private Button modifyConnections;
    private Button sort;

    private TabView editor;
    private IOHandler ioHandler;

    public ToolbarHandler(IOHandler ioHandler, TabView editor) {
        layout = new VBox();
        this.editor = editor;
        this.ioHandler = ioHandler;

        addMatrixItem = new Button("Add Row/Column");
        addMatrixItem.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            Stage window = new Stage();

            // Create Root window
            window.initModality(Modality.APPLICATION_MODAL); //Block events to other windows
            window.setTitle("Add Row/Column");

            // Create changes view and button for it
            Label label = new Label("Changes to be made");
            ListView< Pair<String, String> > changesToMakeView = new ListView<>();
            changesToMakeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            changesToMakeView.setCellFactory(param -> new ListCell< Pair<String, String> >() {
                @Override
                protected void updateItem(Pair<String, String> item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null || item.getKey() == null) {
                        setText(null);
                    } else {
                        if(item.getKey().equals("symmetric")) {
                            setText(item.getValue() + " (Row/Column)");
                        } else if(item.getKey().equals("row")) {
                            setText(item.getValue() + " (Row)");
                        } else {
                            setText(item.getValue() + "(Col)");
                        }
                    }
                }
            });

            Button deleteSelected = new Button("Delete Selected Item(s)");
            deleteSelected.setOnAction(ee -> {
                changesToMakeView.getItems().removeAll(changesToMakeView.getSelectionModel().getSelectedItems());
            });

            // Create user input area
            HBox entryArea = new HBox();

            TextField textField = new TextField();
            textField.setMaxWidth(Double.MAX_VALUE);
            textField.setPromptText("Row/Column Name");
            HBox.setHgrow(textField, Priority.ALWAYS);

            if(ioHandler.getMatrix(editor.getFocusedMatrixUid()).isSymmetrical()) {
                Button addItem = new Button("Add Item");
                addItem.setOnAction(ee -> {
                    String itemName = textField.getText();
                    changesToMakeView.getItems().add(new Pair<String, String>("symmetric", itemName));
                });
                entryArea.getChildren().addAll(textField, addItem);
                entryArea.setPadding(new Insets(10, 10, 10, 10));
                entryArea.setSpacing(20);
            } else {
                Button addRow = new Button("Add as Row");
                addRow.setOnAction(ee -> {
                    String itemName = textField.getText();
                    changesToMakeView.getItems().add(new Pair<String, String>("row", itemName));
                });

                Button addColumn = new Button("Add as Column");
                addColumn.setOnAction(ee -> {
                    String itemName = textField.getText();
                    changesToMakeView.getItems().add(new Pair<String, String>("col", itemName));
                });

                entryArea.getChildren().addAll(textField, addRow, addColumn);
                entryArea.setPadding(new Insets(10, 10, 10, 10));
                entryArea.setSpacing(20);
            }

            // create HBox for user to close with our without changes
            HBox closeArea = new HBox();
            Button applyButton = new Button("Apply Changes");
            applyButton.setOnAction(ee -> {
                for(Pair<String, String> item : changesToMakeView.getItems()) {
                    if(item.getKey().equals("row")) {
                        ioHandler.getMatrix(editor.getFocusedMatrixUid()).addItem(item.getValue(), true);
                    } else if(item.getKey().equals("col")) {
                        ioHandler.getMatrix(editor.getFocusedMatrixUid()).addItem(item.getValue(), false);
                    } else {
                        ioHandler.getMatrix(editor.getFocusedMatrixUid()).addSymmetricItem(item.getValue());
                    }
                }
                window.close();
                editor.refreshTab();
            });

            Pane spacer = new Pane();  // used as a spacer between buttons
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);

            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(ee -> {
                window.close();
            });
            closeArea.getChildren().addAll(cancelButton, spacer, applyButton);

            VBox layout = new VBox(10);
            layout.getChildren().addAll(label, changesToMakeView, deleteSelected, entryArea, closeArea);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(10, 10, 10, 10));
            layout.setSpacing(10);

            //Display window and wait for it to be closed before returning
            Scene scene = new Scene(layout, 500, 300);
            window.setScene(scene);
            window.showAndWait();
        });
        addMatrixItem.setMaxWidth(Double.MAX_VALUE);




        deleteMatrixItem = new Button("Delete Row/Column");
        deleteMatrixItem.setOnAction(e -> {
            System.out.println("Deleting row or column");
        });
        deleteMatrixItem.setMaxWidth(Double.MAX_VALUE);




        renameMatrixItem = new Button("Rename Row/Column");
        renameMatrixItem.setOnAction(e -> {
            System.out.println("Renaming row or column");
        });
        renameMatrixItem.setMaxWidth(Double.MAX_VALUE);




        modifyConnections = new Button("Modify Connections");
        modifyConnections.setOnAction(e -> {
            System.out.println("Modifying connections");
        });
        modifyConnections.setMaxWidth(Double.MAX_VALUE);




        sort = new Button("Sort");
        sort.setOnAction(e -> {
            System.out.println("Sorting");
            editor.refreshTab();
        });
        sort.setMaxWidth(Double.MAX_VALUE);




        layout.getChildren().addAll(addMatrixItem, deleteMatrixItem, renameMatrixItem, modifyConnections, sort);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);
    }

    public VBox getLayout() {
        return layout;
    }
}
