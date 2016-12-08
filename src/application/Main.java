package application;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.*;
import ui.ModelTree;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;

public class Main extends Application {

    private Company company;

    @Override
    public void start(Stage primaryStage) {

        company = createCompany();

        ModelTree<EmploymentUnit<?>> tree = new ModelTree<EmploymentUnit<?>>(company,
                EmploymentUnit::getSubUnits,
                EmploymentUnit::nameProperty,
                unit -> PseudoClass.getPseudoClass(unit.getClass().getSimpleName().toLowerCase()));

        TreeView<EmploymentUnit<?>> treeView = tree.getTreeView();

        TextField add = new TextField();

        Button addButton = new Button("Add");
        addButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        treeView.getSelectionModel().getSelectedItem() == null ||
                                treeView.getSelectionModel().getSelectedItem().getValue() instanceof Role,
                treeView.getSelectionModel().selectedItemProperty()));

        EventHandler<ActionEvent> addHandler = e -> {
            if (treeView.getSelectionModel().getSelectedItem() == null
                    || treeView.getSelectionModel().getSelectedItem().getValue() instanceof Role) {
                return ;
            }
            treeView.getSelectionModel().getSelectedItem().getValue().createAndAddSubUnit(add.getText());
            add.clear();
        };

        add.setOnAction(addHandler);
        addButton.setOnAction(addHandler);

        Button delete = new Button("Delete");
        delete.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        treeView.getSelectionModel().getSelectedItem() == null ||
                                treeView.getSelectionModel().getSelectedItem().getValue() == company,
                treeView.getSelectionModel().selectedItemProperty()));

        delete.setOnAction(e -> {
            TreeItem<EmploymentUnit<?>> selected = treeView.getSelectionModel().getSelectedItem() ;
            selected.getParent().getValue().getSubUnits().remove(selected.getValue());
        });

        HBox controls = new HBox(5, add, addButton, delete);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(treeView);
        root.setBottom(controls);
        Scene scene = new Scene(root, 600, 600);

        scene.getStylesheets().add(getClass().getResource("/ui/style/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        savePersonDataToFile();
    }

    private Company createCompany() {
        Company comp = new Company("James D Enterprises");

        Department executive = new Department("Executive");
        Department hr = new Department("Human Resources");
        Department engineering = new Department("Software Engineering");

        Employee ceo = new Employee("James D");
        Employee bill = new Employee("Bill Gates");
        Employee tim = new Employee("Tim Cook");
        Employee larry = new Employee("Larry Ellison");
        Employee mark = new Employee("Mark Zuckerberg");

        Role ceoRole = new Role("Chief Executive Officer");
        Role coder = new Role("Code Monkey");
        Role testing = new Role("Product Testing");
        Role hiring = new Role("Hiring");
        Role firing = new Role("Firing");

        ceo.getSubUnits().add(ceoRole);

        bill.getSubUnits().addAll(coder, testing);
        tim.getSubUnits().addAll(coder, testing);

        larry.getSubUnits().addAll(hiring, firing);
        mark.getSubUnits().addAll(hiring, firing);

        executive.getSubUnits().add(ceo);
        hr.getSubUnits().addAll(larry, mark);
        engineering.getSubUnits().addAll(bill, tim);

        comp.getSubUnits().addAll(executive, engineering, hr);

        return comp ;
    }

    /**
     * Saves the current employee data to the specified file.
     *
     * @param
     */
    //public void savePersonDataToFile(File file) {     -> add @param javadoc in header above
    public void savePersonDataToFile() {

        File file = new File("C:\\Users\\oldu7\\Desktop\\test3.xml");

        try {
            JAXBContext context = JAXBContext.newInstance(DepartmentWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            DepartmentWrapper wrapper = new DepartmentWrapper();
            wrapper.setName(company.getSubUnits().get(1).getName());
            wrapper.setEmployees(company.getSubUnits().get(1).getSubUnits());

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            //setPersonFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}