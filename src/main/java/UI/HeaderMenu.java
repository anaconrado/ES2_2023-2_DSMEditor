package UI;

import Constants.Constants;
import Matrices.AsymmetricDSM;
import Matrices.Data.AbstractDSMData;
import Matrices.Data.AsymmetricDSMData;
import Matrices.Data.Entities.DSMConnection;
import Matrices.Data.Entities.DSMInterfaceType;
import Matrices.Data.Entities.DSMItem;
import Matrices.Data.Entities.Grouping;
import Matrices.Data.Flags.IPropagationAnalysis;
import Matrices.Data.MultiDomainDSMData;
import Matrices.Data.SymmetricDSMData;
import Matrices.IOHandlers.AbstractIOHandler;
import Matrices.IOHandlers.AsymmetricIOHandler;
import Matrices.IOHandlers.Flags.IThebeauExport;
import Matrices.IOHandlers.MultiDomainIOHandler;
import Matrices.IOHandlers.SymmetricIOHandler;
import Matrices.MultiDomainDSM;
import Matrices.SymmetricDSM;
import Matrices.Views.AbstractMatrixView;
import Matrices.Views.Flags.ISymmetricHighlight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jdom2.Element;

import java.io.File;
import java.util.*;


/**
 * Class to create the header of the gui. Includes menus like file, edit, and view
 *
 * @author Aiden Carney
 */
public class HeaderMenu {
    private static int defaultName = 0;

    private final Menu fileMenu = new Menu("_File");
    private final Menu editMenu = new Menu("_Edit");
    private final Menu viewMenu = new Menu("_View");
    private final Menu toolsMenu = new Menu("_Tools");
    private final Menu helpMenu = new Menu("_Help");

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private RadioMenuItem namesView = new RadioMenuItem("Names");
    private RadioMenuItem weightsView = new RadioMenuItem("Weights");
    private RadioMenuItem interfacesView = new RadioMenuItem("Interfaces");
    private RadioMenuItem fastRenderView = new RadioMenuItem("Fast Render");

    private final MenuBar menuBar = new MenuBar();

    private final EditorPane editor;
    //private IDSM matrix;
    private AbstractDSMData matrixData;
    private AbstractIOHandler ioHandler;
    private AbstractMatrixView matrixView;

    private boolean disabled = false;

    /**
     * Creates a new instance of the header menu and instantiate widgets on it
     */
    public HeaderMenu(EditorPane editor) {
        this.editor = editor;
        if(editor.getFocusedMatrix() != null) {
            this.matrixData = editor.getFocusedMatrix().getMatrixData();
            this.ioHandler = editor.getFocusedMatrix().getMatrixIOHandler();
            this.matrixView = editor.getFocusedMatrix().getMatrixView();
        }

        setupFileMenu();
        setupEditMenu();
        setUpToolsMenu();
        setupViewMenu();
        setupHelpMenu();

        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, toolsMenu, helpMenu);
    }


//region File Menu Items
    /**
     * sets up the Menu object for the file menu
     */
    private void setupFileMenu() {
        Menu newFileMenu = new Menu("New");
        MenuItem openMenu = new MenuItem("Open...");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem saveFileAs = new MenuItem("Save As...");
        Menu importMenu = new Menu("Import");
        Menu exportMenu = new Menu("Export");
        MenuItem exitMenu = new MenuItem("Exit");

        setupNewFileMenuButton(newFileMenu);
        setupOpenMenuButton(openMenu);
        setupSaveFileMenuButton(saveFile);
        setupSaveAsFileMenuButton(saveFileAs);
        setupImportMenuButton(importMenu);
        setupExportMenuButton(exportMenu);
        exitMenu.setOnAction(e -> menuBar.getScene().getWindow().fireEvent(
                new WindowEvent(
                        menuBar.getScene().getWindow(),
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        ));


        fileMenu.getItems().add(newFileMenu);
        fileMenu.getItems().add(openMenu);
        if(matrixData != null) {
            fileMenu.getItems().add(saveFile);
            fileMenu.getItems().add(saveFileAs);
        }
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(importMenu);
        if(matrixData != null) {
            fileMenu.getItems().add(exportMenu);
        }
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(exitMenu);
    }


    /**
     * Sets up the menu items for creating new DSMs
     *
     * @param parent  the parent menu
     */
    private void setupNewFileMenuButton(Menu parent) {
        MenuItem newSymmetric = new MenuItem("Symmetric Matrix");
        newSymmetric.setOnAction(e -> {
            File file = new File("./untitled" + defaultName);
            while(file.exists()) {  // make sure file does not exist
                defaultName += 1;
                file = new File("./untitled" + defaultName);
            }
            SymmetricDSMData newMatrix = new SymmetricDSMData();
            SymmetricIOHandler ioHandler = new SymmetricIOHandler(file, newMatrix);

            this.editor.addTab(new SymmetricDSM(newMatrix, ioHandler));

            defaultName += 1;
        });
        MenuItem newNonSymmetric = new MenuItem("Non-Symmetric Matrix");
        newNonSymmetric.setOnAction(e -> {
            File file = new File("./untitled" + defaultName);
            while(file.exists()) {  // make sure file does not exist
                defaultName += 1;
                file = new File("./untitled" + defaultName);
            }
            AsymmetricDSMData newMatrix = new AsymmetricDSMData();
            AsymmetricIOHandler ioHandler = new AsymmetricIOHandler(file, newMatrix);

            this.editor.addTab(new AsymmetricDSM(newMatrix, ioHandler));

            defaultName += 1;
        });

        MenuItem newMultiDomain = new MenuItem("Multi-Domain Matrix");
        newMultiDomain.setOnAction(e -> {
            MultiDomainDSMData matrix = new MultiDomainDSMData();
            File file = new File("./untitled" + defaultName);
            while(file.exists()) {  // make sure file does not exist
                defaultName += 1;
                file = new File("./untitled" + defaultName);
            }
            MultiDomainDSMData newMatrix = new MultiDomainDSMData();
            MultiDomainIOHandler ioHandler = new MultiDomainIOHandler(file, newMatrix);

            this.editor.addTab(new MultiDomainDSM(newMatrix, ioHandler, this));

            defaultName += 1;
        });

        parent.getItems().addAll(newSymmetric, newNonSymmetric, newMultiDomain);
    }


    /**
     * Sets up the menu for opening dsms
     *
     * @param menu  the menu item to set the callback for
     */
    private <T extends MenuItem> void setupOpenMenuButton(T menu) {
        menu.setOnAction( e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DSM File", "*.dsm"));  // dsm is the only file type usable
            File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
            if(file == null) {
                return;
            } else if (this.editor.getMatricesCollection().getMatrixFileAbsoluteSavePaths().contains(file.getAbsolutePath())) {
                editor.focusTab(file);  // focus on that tab because it is already open
                return;
            }
            // make sure user did not just close out of the file chooser window
            switch (AbstractIOHandler.getFileDSMType(file)) {
                case "symmetric" -> this.editor.addTab(new SymmetricDSM(file));
                case "asymmetric" -> this.editor.addTab(new AsymmetricDSM(file));
                case "multi-domain" -> this.editor.addTab(new MultiDomainDSM(file, this));

                default -> System.out.println("the type of dsm could not be determined from the file " + file.getAbsolutePath());
            }
        });
    }


    /**
     * Sets up the menu button for saving a file
     *
     * @param menu  the Menu object
     */
    public void setupSaveFileMenuButton(MenuItem menu) {
        menu.setOnAction(e -> {
            if(matrixData == null) return;

            if(!ioHandler.getSavePath().exists() || ioHandler.getSavePath().getAbsolutePath().contains("untitled")) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DSM File", "*.dsm"));  // dsm is the only file type usable
                File fileName = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
                if(fileName != null) {
                    fileName = AbstractIOHandler.forceExtension(fileName, ".dsm");
                    ioHandler.setSavePath(fileName);
                } else {  // user did not select a file, so do not save it
                    return;
                }
            }
            int code = ioHandler.saveMatrixToFile(ioHandler.getSavePath());  // TODO: add checking with the return code
        });
    }


    /**
     * Sets up the menu button for performing the "save as" operation on a file
     *
     * @param menu  the Menu object
     */
    public <T extends MenuItem> void setupSaveAsFileMenuButton(T menu) {
        menu.setOnAction(e -> {
            if(matrixData == null) return;

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DSM File", "*.dsm"));  // dsm is the only file type usable
            File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
            if(file != null) {
                int code = ioHandler.saveMatrixToFile(file);  // TODO: add checking with the return code
                ioHandler.setSavePath(file);
            }
        });
    }


    /**
     * Sets up the menu for importing dsms from alternative sources (ex. thebeau matlab files)
     *
     * @param parent  the parent menu
     */
    private void setupImportMenuButton(Menu parent) {
        MenuItem importThebeau = new MenuItem("Thebeau Matlab File...");
        importThebeau.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Matlab File", "*.m"));  // matlab is the only file type usable
            File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
            if(file != null) {  // make sure user did not just close out of the file chooser window
                SymmetricIOHandler ioHandler = new SymmetricIOHandler(file);
                SymmetricDSMData matrix = ioHandler.importThebeauMatlabFile(file);
                if(matrixData == null) {
                    // TODO: open window saying there was an error parsing the document
                    System.out.println("there was an error reading the file " + file);
                } else if(!this.editor.getMatricesCollection().getMatrixFileAbsoluteSavePaths().contains(file.getAbsolutePath())) {
                    File importedFile = new File(file.getParent(), file.getName().substring(0, file.getName().lastIndexOf('.')) + ".dsm");  // convert .m extension to .dsm
                    this.editor.addTab(new SymmetricDSM(matrix, ioHandler));
                } else {
                    editor.focusTab(file);  // focus on that tab because it is already open
                }
            }
        });

        parent.getItems().add(importThebeau);
    }


    /**
     * Sets up the menu items for exporting a dsm
     *
     * @param parent  the parent menu
     */
    private void setupExportMenuButton(Menu parent) {
        if (matrixData == null) return;

        MenuItem exportCSV = new MenuItem("CSV File (.csv)...");
        MenuItem exportXLSX = new MenuItem("Micro$oft Excel File (.xlsx)...");
        MenuItem exportImage = new MenuItem("PNG Image File (.png)...");

        // matrices by default are instances of IStandardExports so set them up
        exportCSV.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            ioHandler.promptExportToCSV(menuBar.getScene().getWindow());
        });

        exportXLSX.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            ioHandler.promptExportToExcel(menuBar.getScene().getWindow());
        });

        exportImage.setOnAction(e -> {
            if(editor.getFocusedMatrixUid() == null) {
                return;
            }
            ioHandler.exportToImage(matrixData.createCopy(), matrixView.createCopy());
        });


        parent.getItems().addAll(exportCSV, exportXLSX, exportImage);


        if(ioHandler instanceof IThebeauExport) {
            MenuItem exportThebeau = new MenuItem("Thebeau Matlab File (.m)...");
            exportThebeau.setOnAction(e -> {
                if(editor.getFocusedMatrixUid() == null) {
                    return;
                }

                ((IThebeauExport)ioHandler).promptExportToThebeau(menuBar.getScene().getWindow());
            });

            parent.getItems().add(exportThebeau);
        }

    }
//endregion


    /**
     * sets up the Menu object for the edit menu
     */
    private void setupEditMenu() {
        editMenu.getItems().clear();  // ensure parent not set twice

        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e -> {
            if(matrixData == null) {
                return;
            }
            matrixData.undoToCheckpoint();
            matrixView.refreshView();
        });

        MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e -> {
            if(matrixData == null) {
                return;
            }
            matrixData.redoToCheckpoint();
            matrixView.refreshView();
        });


        MenuItem invert = new MenuItem("Transpose Matrix");
        invert.setOnAction(e -> {
            if(matrixData == null) {
                return;
            }
            matrixData.transposeMatrix();
            matrixData.setCurrentStateAsCheckpoint();
            matrixView.refreshView();
        });


        MenuItem convertToMDM = new MenuItem("Convert to Multi-Domain");
        convertToMDM.setOnAction(e -> {
            if(matrixData instanceof SymmetricDSMData symmetricMatrix) {
                // create the default domain and the groupings
                ArrayList<Grouping> domainGroupings = new ArrayList<>();
                for(Grouping grouping : symmetricMatrix.getGroupings()) {
                    domainGroupings.add(new Grouping(grouping));
                }
                HashMap<Grouping, Collection<Grouping>> domains = new HashMap<>();
                Grouping domain = new Grouping("default", Color.color(1, 1, 1));
                domains.put(domain, domainGroupings);

                MultiDomainDSMData multiDomainMatrix = new MultiDomainDSMData(domains);
                multiDomainMatrix.setTitle(symmetricMatrix.getTitle());
                multiDomainMatrix.setProjectName(symmetricMatrix.getProjectName());
                multiDomainMatrix.setCustomer(symmetricMatrix.getCustomer());
                multiDomainMatrix.setVersionNumber(symmetricMatrix.getVersionNumber());

                // create the items
                for(DSMItem row : symmetricMatrix.getRows()) {
                    DSMItem newRow = new DSMItem(row);
                    newRow.setGroup2(domain);
                    multiDomainMatrix.addItem(newRow, true);
                }
                for(DSMItem col : symmetricMatrix.getCols()) {
                    DSMItem newCol = new DSMItem(col);
                    newCol.setGroup2(domain);
                    multiDomainMatrix.addItem(newCol, false);
                }

                // create the connections
                for(DSMConnection conn : symmetricMatrix.getConnections()) {
                    ArrayList<DSMInterfaceType> connectionInterfaces = new ArrayList<>();  // parse interfaces
                    for(DSMInterfaceType interfaceType : conn.getInterfaces()) {
                        connectionInterfaces.add(new DSMInterfaceType(interfaceType));
                    }
                    multiDomainMatrix.modifyConnection(conn.getRowUid(), conn.getColUid(), conn.getConnectionName(), conn.getWeight(), connectionInterfaces);
                }

                // create the interfaces
                HashMap<String, Vector<DSMInterfaceType>> interfaceTypes = new HashMap<>();
                for(Map.Entry<String, Vector<DSMInterfaceType>> interfaceGroup : symmetricMatrix.getInterfaceTypes().entrySet()) {
                    Vector<DSMInterfaceType> interfaces = new Vector<>();
                    for(DSMInterfaceType i : interfaceGroup.getValue()) {
                        interfaces.add(new DSMInterfaceType(i));
                    }
                    interfaceTypes.put(interfaceGroup.getKey(), interfaces);
                }
                for(String interfaceGrouping : interfaceTypes.keySet()) {  // add the groupings
                    multiDomainMatrix.addInterfaceTypeGrouping(interfaceGrouping);
                }
                for(Map.Entry<String, Vector<DSMInterfaceType>> interfaces : interfaceTypes.entrySet()) {  // add the interfaces
                    for(DSMInterfaceType i : interfaces.getValue()) {
                        multiDomainMatrix.addInterface(interfaces.getKey(), i);
                    }
                }

                multiDomainMatrix.setCurrentStateAsCheckpoint();
                multiDomainMatrix.clearStacks();


                File file = new File("./untitled" + defaultName);
                while(file.exists()) {  // make sure file does not exist
                    defaultName += 1;
                    file = new File("./untitled" + defaultName);
                }
                MultiDomainIOHandler ioHandler = new MultiDomainIOHandler(file, multiDomainMatrix);

                this.editor.addTab(new MultiDomainDSM(multiDomainMatrix, ioHandler, this));

                defaultName += 1;
            }
        });


        editMenu.setOnShown(e -> {
            if(matrixData == null) {
                undo.setDisable(true);
                redo.setDisable(true);
                convertToMDM.setDisable(true);
            } else {
                undo.setDisable(!matrixData.canUndo());
                redo.setDisable(!matrixData.canRedo());
                convertToMDM.setDisable(false);
            }
        });


        editMenu.getItems().add(undo);
        editMenu.getItems().add(redo);
        editMenu.getItems().add(new SeparatorMenuItem());
        editMenu.getItems().add(invert);

        if(matrixData instanceof SymmetricDSMData) {
            editMenu.getItems().add(convertToMDM);
        }
    }


    /**
     * sets up the Menu object for the tools menu
     */
    private void setUpToolsMenu() {
        if(matrixData == null) return;

        MenuItem search = new MenuItem("Find Connections");
        search.setOnAction(e -> editor.getSearchWidget().open());
        toolsMenu.getItems().add(search);


        if(matrixView instanceof ISymmetricHighlight symmetricMatrixView) {
            RadioMenuItem validateSymmetry = new RadioMenuItem("Validate Symmetry");
            validateSymmetry.setSelected(symmetricMatrixView.getSymmetryValidation());
            validateSymmetry.setOnAction(e -> {
                if (validateSymmetry.isSelected()) {
                    symmetricMatrixView.setValidateSymmetry();
                } else {
                    symmetricMatrixView.clearValidateSymmetry();
                }
            });
            toolsMenu.getItems().add(validateSymmetry);
        }


        if(matrixData instanceof IPropagationAnalysis) {
            MenuItem propagationAnalysis = new MenuItem("Propagation Analysis...");
            propagationAnalysis.setOnAction(e -> {
                if (editor.getFocusedMatrixUid() == null) {
                    return;
                }

                PropagationAnalysisWindow p = new PropagationAnalysisWindow(this.editor.getFocusedMatrixData());
                p.start();
            });

            toolsMenu.getItems().add(propagationAnalysis);
        }

        if(matrixData instanceof SymmetricDSMData) {
            MenuItem coordinationScore = new MenuItem("Thebeau Cluster Analysis...");
            coordinationScore.setOnAction(e -> {
                if (editor.getFocusedMatrixUid() == null) {
                    return;
                }

                ClusterAnalysisWindow c = new ClusterAnalysisWindow((SymmetricDSMData) this.editor.getFocusedMatrixData());
                c.start();
            });

            MenuItem thebeau = new MenuItem("Thebeau Algorithm...");
            thebeau.setOnAction(e -> {
                if (editor.getFocusedMatrixUid() == null) {
                    return;
                }

                ClusterAlgorithmWindow c = new ClusterAlgorithmWindow((SymmetricDSMData) this.editor.getFocusedMatrixData());
                c.start();
            });

            toolsMenu.getItems().addAll(coordinationScore, thebeau);
        }
    }


    /**
     * sets up the Menu object for the view menu
     */
    protected void setupViewMenu() {
        MenuItem zoomIn = new MenuItem("Zoom In");  // TODO: ensure this works in editor when new matrices are opened
        zoomIn.setOnAction(e -> editor.increaseFontScaling());
        MenuItem zoomOut = new MenuItem("Zoom Out");
        zoomOut.setOnAction(e -> editor.decreaseFontScaling());
        MenuItem zoomReset = new MenuItem("Reset Zoom");
        zoomReset.setOnAction(e -> editor.resetFontScaling());

        Menu viewMode = new Menu("View Mode");

        namesView = new RadioMenuItem("Names");
        weightsView = new RadioMenuItem("Weights");
        interfacesView = new RadioMenuItem("Interfaces");
        fastRenderView = new RadioMenuItem("Fast Render");

        namesView.setToggleGroup(toggleGroup);
        weightsView.setToggleGroup(toggleGroup);
        interfacesView.setToggleGroup(toggleGroup);
        fastRenderView.setToggleGroup(toggleGroup);


        toggleGroup.selectedToggleProperty().addListener((o, oldValue, newValue) -> {
            if(matrixData == null || newValue == null) return;

            if(newValue.equals(namesView)) {
                if(this.disabled) {
                    matrixView.setCurrentMode(AbstractMatrixView.MatrixViewMode.STATIC_NAMES);
                } else {
                    matrixView.setCurrentMode(AbstractMatrixView.MatrixViewMode.EDIT_NAMES);
                }

            } else if(newValue.equals(weightsView)) {
                if(this.disabled) {
                    matrixView.setCurrentMode(AbstractMatrixView.MatrixViewMode.STATIC_WEIGHTS);
                } else {
                    matrixView.setCurrentMode(AbstractMatrixView.MatrixViewMode.EDIT_WEIGHTS);
                }

            } else if(newValue.equals(interfacesView)) {
                if(this.disabled) {
                    matrixView.setCurrentMode(AbstractMatrixView.MatrixViewMode.STATIC_INTERFACES);
                } else {
                    matrixView.setCurrentMode(AbstractMatrixView.MatrixViewMode.EDIT_INTERFACES);
                }

            } else if(newValue.equals(fastRenderView)) {
                matrixView.setCurrentMode(AbstractMatrixView.MatrixViewMode.FAST_RENDER);
            }

            matrixView.refreshView();
        });

        viewMode.getItems().addAll(namesView, weightsView, interfacesView, fastRenderView);


        // set default values of check boxes
        if(matrixData != null) {
            AbstractMatrixView.MatrixViewMode mode = matrixView.getCurrentMode();
            if(mode.equals(AbstractMatrixView.MatrixViewMode.EDIT_NAMES) || mode.equals(AbstractMatrixView.MatrixViewMode.STATIC_NAMES)) {
                namesView.setSelected(true);
            } else if(mode.equals(AbstractMatrixView.MatrixViewMode.EDIT_WEIGHTS) || mode.equals(AbstractMatrixView.MatrixViewMode.STATIC_WEIGHTS)) {
                weightsView.setSelected(true);
            } else if(mode.equals(AbstractMatrixView.MatrixViewMode.EDIT_INTERFACES) || mode.equals(AbstractMatrixView.MatrixViewMode.STATIC_INTERFACES)) {
                interfacesView.setSelected(true);
            } else if(mode.equals(AbstractMatrixView.MatrixViewMode.FAST_RENDER)) {
                fastRenderView.setSelected(true);
            }
        } else {  // default to true if no matrix is open
            namesView.setSelected(true);
        }


        viewMenu.setOnShown(e -> {
            if(matrixData == null) {
                zoomIn.setDisable(true);
                zoomOut.setDisable(true);
                zoomReset.setDisable(true);
                viewMode.setDisable(true);
            } else {
                zoomIn.setDisable(false);
                zoomOut.setDisable(false);
                zoomReset.setDisable(false);
                viewMode.setDisable(false);
            }
        });


        viewMenu.getItems().addAll(zoomIn, zoomOut, zoomReset);
        viewMenu.getItems().add(new SeparatorMenuItem());
        viewMenu.getItems().add(viewMode);

    }


    /**
     * sets up the Menu object for the help menu
     */
    protected void setupHelpMenu() {
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            // bring up window asking to delete rows or columns
            Stage window = new Stage();
            window.setTitle("About");
            window.initModality(Modality.APPLICATION_MODAL);  // Block events to other windows

            VBox rootLayout = new VBox();
            rootLayout.setPadding(new Insets(10, 10, 10, 10));
            rootLayout.setSpacing(10);

            Label versionLabel = new Label("Version: " + Constants.version);

            rootLayout.getChildren().addAll(versionLabel);

            Scene scene = new Scene(rootLayout);
            window.setScene(scene);
            window.showAndWait();
        });

        helpMenu.getItems().addAll(about);
    }


    /**
     * @return  the menu bar object that has been set up
     */
    public MenuBar getMenuBar() {
        return this.menuBar;
    }


    /**
     * Refreshes the header menu for a new matrix
     *
     * @param matrixData  the matrix data object
     * @param ioHandler   the matrix ioHandler object
     * @param matrixView  the matrix view object
     */
    public void refresh(AbstractDSMData matrixData, AbstractIOHandler ioHandler, AbstractMatrixView matrixView) {
        this.matrixData = matrixData;
        this.ioHandler = ioHandler;
        this.matrixView = matrixView;

        menuBar.getMenus().clear();
        fileMenu.getItems().clear();
        editMenu.getItems().clear();
        viewMenu.getItems().clear();
        toolsMenu.getItems().clear();
        helpMenu.getItems().clear();

        setupFileMenu();
        setupEditMenu();
        setUpToolsMenu();
        setupViewMenu();
        setupHelpMenu();

        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, toolsMenu, helpMenu);
    }


    /**
     * Enables or disables the edit menu
     *
     * @param disabled  if the edit menu should be disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        editMenu.setDisable(disabled);

        RadioMenuItem currentMode = (RadioMenuItem) toggleGroup.getSelectedToggle();
        toggleGroup.selectToggle(null);
        toggleGroup.selectToggle(currentMode);  // force update to the view mode
    }
}
