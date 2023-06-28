module com.ggc.theaterkarten {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ggc.theaterkarten to javafx.fxml;
    exports com.ggc.theaterkarten;
}