package View.HeaderMenu;

import Data.SymmetricDSM;
import IOHandler.SymmetricIOHandler;
import View.ConnectionSearchWidget;
import View.EditorPane;
import View.MatrixHandlers.SymmetricMatrixHandler;
import View.SideBarTools.SymmetricSideBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import java.io.File;

public class SymmetricHeaderMenu extends TemplateHeaderMenu {


    /**
     * Creates a new instance of the header menu and instantiate widgets on it
     *
     * @param editor        the EditorPane instance
     */
    public SymmetricHeaderMenu(EditorPane editor, ConnectionSearchWidget searchWidget) {
        super(editor, searchWidget);

        // methods to set up buttons are already called
    }


    /**
     * sets up the Menu object for the file menu
     */
    @Override
    protected void setupFileMenu() {
        Menu newFileMenu = new Menu("New...");
        MenuItem newSymmetric = new MenuItem("Symmetric Matrix");
        newSymmetric.setOnAction(e -> {
            SymmetricDSM matrix = new SymmetricDSM();
            File file = new File("./untitled" + Integer.toString(defaultName));
            while(file.exists()) {  // make sure file does not exist
                defaultName += 1;
                file = new File("./untitled" + Integer.toString(defaultName));
            }

            this.editor.addTab(
                matrix,
                new SymmetricIOHandler(file),
                new SymmetricMatrixHandler(matrix, 12.0),
                this,
                new SymmetricSideBar(matrix, editor
            ));

            defaultName += 1;
        });
//        MenuItem newNonSymmetric = new MenuItem("Non-Symmetric Matrix");
//        newNonSymmetric.setOnAction(e -> {
//
//        });

        newFileMenu.getItems().addAll(newSymmetric);


        MenuItem openFile = new MenuItem("Open...");
        openFile.setOnAction( e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DSM File", "*.dsm"));  // dsm is the only file type usable
            File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
            if(file != null) {  // make sure user did not just close out of the file chooser window
                SymmetricIOHandler ioHandler = new SymmetricIOHandler(file);
                SymmetricDSM matrix = ioHandler.readFile();
                if(matrix == null) {
                    // TODO: open window saying there was an error parsing the document
                    System.out.println("there was an error reading the file " + file.toString());
                } else if(!this.editor.getMatrixFileAbsoluteSavePaths().contains(file.getAbsolutePath())) {
                    this.editor.addTab(
                        matrix,
                        new SymmetricIOHandler(file),
                        new SymmetricMatrixHandler(matrix, 12.0),
                        this,
                        new SymmetricSideBar(matrix, editor
                    ));
                } else {
                    editor.focusTab(file);  // focus on that tab because it is already open
                }
            }
        });

        Menu importMenu = new Menu("Import...");
        MenuItem importThebeau = new MenuItem("Thebeau Matlab File");
        importThebeau.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Matlab File", "*.m"));  // matlab is the only file type usable
            File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
            if(file != null) {  // make sure user did not just close out of the file chooser window
                SymmetricIOHandler ioHandler = new SymmetricIOHandler(file);
                SymmetricDSM matrix = ioHandler.readFile();
                if(matrix == null) {
                    // TODO: open window saying there was an error parsing the document
                    System.out.println("there was an error reading the file " + file.toString());
                } else if(!this.editor.getMatrixFileAbsoluteSavePaths().contains(file.getAbsolutePath())) {
                    File importedFile = new File(file.getParent(), file.getName().substring(0, file.getName().lastIndexOf('.')) + ".dsm");  // convert .m extension to .dsm
                    this.editor.addTab(
                        matrix,
                        new SymmetricIOHandler(importedFile),
                        new SymmetricMatrixHandler(matrix, 12.0),
                        this,
                        new SymmetricSideBar(matrix, editor
                    ));
                } else {
                    editor.focusTab(file);  // focus on that tab because it is already open
                }
            }
        });

        importMenu.getItems().add(importThebeau);

        MenuItem saveFile = new MenuItem("Save...");
        saveFile.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            if(this.editor.getMatrixIOHandler(editor.getFocusedMatrixUid()).getSavePath().getAbsolutePath().contains("untitled")) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DSM File", "*.dsm"));  // dsm is the only file type usable
                File fileName = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
                if(fileName != null) {
                    this.editor.getMatrixIOHandler(editor.getFocusedMatrixUid()).setSavePath(fileName);
                } else {  // user did not select a file, so do not save it
                    return;
                }
            }
            int matrixUid = editor.getFocusedMatrixUid();
            SymmetricDSM matrix = (SymmetricDSM) this.editor.getFocusedMatrix();
            int code = this.editor.getMatrixIOHandler(matrixUid).saveMatrixToFile(matrix, this.editor.getMatrixIOHandler(editor.getFocusedMatrixUid()).getSavePath());  // TODO: add checking with the return code
        });

        MenuItem saveFileAs = new MenuItem("Save As...");
        saveFileAs.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DSM File", "*.dsm"));  // dsm is the only file type usable
            File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
            if(file != null) {
                int matrixUid = editor.getFocusedMatrixUid();
                SymmetricDSM matrix = (SymmetricDSM) this.editor.getFocusedMatrix();
                int code = this.editor.getMatrixIOHandler(matrixUid).saveMatrixToFile(matrix, file);  // TODO: add checking with the return code

                this.editor.getMatrixIOHandler(matrixUid).setSavePath(file);
            }
        });

        Menu exportMenu = new Menu("Export");
        MenuItem exportCSV = new MenuItem("CSV File (.csv)");
        exportCSV.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            int matrixUid = editor.getFocusedMatrixUid();
            SymmetricDSM matrix = (SymmetricDSM) this.editor.getFocusedMatrix();
            this.editor.getMatrixIOHandler(matrixUid).promptExportToCSV(matrix, menuBar.getScene().getWindow());
        });
        MenuItem exportXLSX = new MenuItem("Micro$oft Excel File (.xlsx)");
        exportXLSX.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            int matrixUid = editor.getFocusedMatrixUid();
            SymmetricDSM matrix = (SymmetricDSM) this.editor.getFocusedMatrix();
            this.editor.getMatrixIOHandler(matrixUid).promptExportToExcel(matrix, menuBar.getScene().getWindow());
        });
        MenuItem exportThebeau = new MenuItem("Thebeau Matlab File (.m)");
        exportThebeau.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            int matrixUid = editor.getFocusedMatrixUid();
            SymmetricDSM matrix = (SymmetricDSM) this.editor.getFocusedMatrix();
            SymmetricIOHandler ioHandler = (SymmetricIOHandler) this.editor.getMatrixIOHandler(matrixUid);
            ioHandler.promptExportToThebeau(matrix, menuBar.getScene().getWindow());
        });
        MenuItem exportImage = new MenuItem("PNG Image File (.png)");
        exportImage.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            int matrixUid = editor.getFocusedMatrixUid();
            SymmetricDSM matrix = (SymmetricDSM) this.editor.getFocusedMatrix();
            this.editor.getMatrixIOHandler(matrixUid).exportToImage(matrix, this.editor.getMatrixHandler(matrixUid));
        });

        exportMenu.getItems().addAll(exportCSV, exportXLSX, exportThebeau, exportImage);


        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            menuBar.getScene().getWindow().fireEvent(
                    new WindowEvent(
                            menuBar.getScene().getWindow(),
                            WindowEvent.WINDOW_CLOSE_REQUEST
                    )
            );
        });

        fileMenu.getItems().add(newFileMenu);
        fileMenu.getItems().add(openFile);
        fileMenu.getItems().add(saveFile);
        fileMenu.getItems().add(saveFileAs);
//        fileMenu.getItems().add(new SeparatorMenuItem());
//        fileMenu.getItems().add(new MenuItem("Settings..."));
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(importMenu);
        fileMenu.getItems().add(exportMenu);
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(exit);
    }


    /**
     * sets up the Menu object for the edit menu
     */
    @Override
    protected void setupEditMenu() {
        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            this.editor.getFocusedMatrix().undoToCheckpoint();
            editor.refreshTab();
        });

        MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            this.editor.getFocusedMatrix().redoToCheckpoint();
            editor.refreshTab();
        });


        MenuItem invert = new MenuItem("Invert Matrix");
        invert.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            this.editor.getFocusedMatrix().invertMatrix();
            this.editor.getFocusedMatrix().setCurrentStateAsCheckpoint();
            editor.refreshTab();
        });


        editMenu.setOnShown(e -> {  // disable validate symmetry for non-symmetrical matrices
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            undo.setDisable(!this.editor.getFocusedMatrix().canUndo());
            redo.setDisable(!this.editor.getFocusedMatrix().canRedo());
        });


        editMenu.getItems().add(undo);
        editMenu.getItems().add(redo);
        editMenu.getItems().add(new SeparatorMenuItem());
        editMenu.getItems().add(invert);
    }


    /**
     * sets up the Menu object for the tools menu
     */
    @Override
    protected void setUpToolsMenu() {

    }
}
