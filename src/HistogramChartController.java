/*
Name: Eduard Zakarian
Student Number: 965217
This is my own work.
*/

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HistogramChartController implements Initializable {
    private static Image image;
    private XYChart.Series<Number, Number> series;
    private XYChart.Series<Number, Number> series1;
    private XYChart.Series<Number, Number> series2;
    private XYChart.Series<Number, Number> series3;
    private int[][] histogram;
    @FXML
    private LineChart<Number, Number> histogramChartRed;
    @FXML
    private LineChart<Number, Number> histogramChartGreen;
    @FXML
    private LineChart<Number, Number> histogramChartBlue;
    @FXML
    private LineChart<Number, Number> histogramChartBrightness;
    @FXML
    private AnchorPane ap;

    //This method produces a non-colored equalised image
    public static Image histogramEq(Image img, int[] map) {
        System.out.println("Histogram Equalisation");
        int height = (int) img.getHeight();
        int width = (int) img.getWidth();
        WritableImage equalisedImage = new WritableImage(width, height);
        PixelWriter equalisedImage_writer = equalisedImage.getPixelWriter();
        PixelReader image_reader = image.getPixelReader();

        //Iterating through each pixel of an image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = image_reader.getColor(x, y);
                int red = (int) (color.getRed() * 255);
                int green = (int) (color.getGreen() * 255);
                int blue = (int) (color.getBlue() * 255);
                int grey = (red + green + blue) / 3;
                //Using a map to look-up the new value of a grey channel and putting this new value into the image
                int new_grey = map[grey];
                color = Color.color(((float) new_grey / 255.0), ((float) new_grey / 255.0), ((float) new_grey / 255.0));
                equalisedImage_writer.setColor(x, y, color);
            }
        }

        return equalisedImage;
    }

    @FXML
    void okayButtonPressed(ActionEvent event) {
        //Close the window after all operations are done
        Stage stage = (Stage) ap.getScene().getWindow();
        stage.close();
    }

    //This method is responsible for applying histogram equalisation effect on an image
    @FXML
    public void histEqButtonPressed(ActionEvent event) {
        //An array to store cumulative distribution values
        int[] t = new int[256];
        //The first element of t is equal to a grey channel's histogram value
        t[0] = histogram[0][3];
        for (int i = 1; i < 256; i++) {
            t[i] = t[i - 1] + histogram[i][3];
        }

        int[] mapping = new int[256];
        int imgSize = ((int) image.getHeight() * (int) image.getWidth());
        for (int i = 0; i < 256; i++) {
            mapping[i] = (int) (255.0 * ((float) t[i] / (float) imgSize));
        }

        //Applying the mapping on an image
        Image i = this.histogramEq(image, mapping);
        Photoshop.imageView.setImage(i);
        //Close the window after all operations are done
        Stage stage = (Stage) ap.getScene().getWindow();
        stage.close();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Hide the charts legend as it doesn't make sense here
        histogramChartRed.legendVisibleProperty().setValue(false);
        histogramChartGreen.legendVisibleProperty().setValue(false);
        histogramChartBlue.legendVisibleProperty().setValue(false);
        histogramChartBrightness.legendVisibleProperty().setValue(false);
        //Hide node bubbles to represent a smooth graph
        histogramChartRed.setCreateSymbols(false);
        histogramChartGreen.setCreateSymbols(false);
        histogramChartBlue.setCreateSymbols(false);
        histogramChartBrightness.setCreateSymbols(false);

        histogramChartRed.setHorizontalGridLinesVisible(true);
        histogramChartGreen.setHorizontalGridLinesVisible(true);
        histogramChartBlue.setHorizontalGridLinesVisible(true);
        histogramChartBrightness.setHorizontalGridLinesVisible(true);

        //Initialising 4 series for 4 charts
        series = new XYChart.Series<>();
        series1 = new XYChart.Series<>();
        series2 = new XYChart.Series<>();
        series3 = new XYChart.Series<>();

        histogram = new int[256][4];
        //Get the source image
        image = Photoshop.image;
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader p = image.getPixelReader();
        //This loop computes the histogram values and stores them into an array
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = p.getColor(x, y);
                int red = (int) (color.getRed() * 255);
                int green = (int) (color.getGreen() * 255);
                int blue = (int) (color.getBlue() * 255);
                int grey = (red + green + blue) / 3;
                //0 - RED, 1 - GREEN, 2 - BLUE, 3 - BRIGHTNESS
                histogram[red][0]++;
                histogram[green][1]++;
                histogram[blue][2]++;
                histogram[grey][3]++;
            }
        }
        //Add the histogram information to a respective line chart
        for (int i = 0; i < 256; i++) {
            series.getData().add(new XYChart.Data(i, histogram[i][0]));
            series1.getData().add(new XYChart.Data<>(i, histogram[i][1]));
            series2.getData().add(new XYChart.Data<>(i, histogram[i][2]));
            series3.getData().add(new XYChart.Data<>(i, histogram[i][3]));
        }

        histogramChartRed.getData().addAll(series);
        histogramChartGreen.getData().addAll(series1);
        histogramChartBlue.getData().addAll(series2);
        histogramChartBrightness.getData().addAll(series3);

    }
}
