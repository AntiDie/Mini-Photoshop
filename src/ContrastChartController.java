/*
Name: Eduard Zakarian
Student Number: 965217
This is my own work.
*/

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ContrastChartController implements Initializable {
    //Two points (r1,s1) and (r2,s2)
    private XYChart.Series<Number, Number> series;

    //The pane used to retrieve a current scene
    @FXML
    private AnchorPane ap;

    @FXML
    private LineChart<Number, Number> contrastChart;

    @FXML
    private NumberAxis x;

    @FXML
    private NumberAxis y;

    //Gets the coordinates from series and applies them to the contrast stretching algorithm
    @FXML
    void okayButtonPressed() {
        double r1 = series.getData().get(1).getXValue().doubleValue() / 255.;
        double r2 = series.getData().get(2).getXValue().doubleValue() / 255.;
        double s1 = series.getData().get(1).getYValue().doubleValue() / 255.;
        double s2 = series.getData().get(2).getYValue().doubleValue() / 255.;

        //Applying the formula for the current image
        Image contrast_stretched_image = Photoshop.contrastStretching(Photoshop.imageView.getImage(), r1, r2, s1, s2);
        Photoshop.imageView.setImage(contrast_stretched_image);

        //Close the window after all operations are done
        Stage stage = (Stage) ap.getScene().getWindow();
        stage.close();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Restrict axis scaling as values are being changed
        x.setAutoRanging(false);
        y.setAutoRanging(false);
        //Hide the charts legend as it doesn't make sense here
        contrastChart.legendVisibleProperty().setValue(false);
        series = new XYChart.Series<>();
        //Default Values for node1 and node2
        series.getData().add(new XYChart.Data(0, 0));
        series.getData().add(new XYChart.Data(0, 0));
        series.getData().add(new XYChart.Data(255, 255));
        series.getData().add(new XYChart.Data(255, 255));
        contrastChart.getData().addAll(series);
        series.getData().get(0).getNode().setVisible(false);
        series.getData().get(3).getNode().setVisible(false);

        //The code to make nodes draggable
        //This for loop executes till there are no unvisited nodes left (2 times in our case)
        for (XYChart.Data<Number, Number> data : series.getData()) {
            Node node = data.getNode();
            node.setCursor(Cursor.HAND);
            node.setOnMouseDragged(e -> {
                Point2D mousePoint = new Point2D(e.getSceneX(), e.getSceneY());
                double mouseX = x.sceneToLocal(mousePoint).getX();
                double mouseY = y.sceneToLocal(mousePoint).getY();
                //Set the new X and Y value to our series
                data.setXValue(x.getValueForDisplay(mouseX));
                data.setYValue(y.getValueForDisplay(mouseY));
            });

        }
    }
}
