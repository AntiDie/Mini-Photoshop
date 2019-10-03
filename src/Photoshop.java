/*
Name: Eduard Zakarian
Student Number: 965217
This is my own work.
*/

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Photoshop extends Application {
    public static Image image;
    public static ImageView imageView;
    private TextField gammaField = new TextField("");
    //Hard-coded values are stored in this array
    private int[][] laplacianMatrix = new int[5][5];

    public static void main(String[] args) {
        launch();
    }

    public static Image contrastStretching(Image image, double r1, double r2, double s1, double s2) {
        // Find the width and height of the image to be process
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        // Create a new image of that width and height
        WritableImage contrast_stretched_image = new WritableImage(width, height);
        // Get an interface to write to that image memory
        PixelWriter contrast_stretched_image_writer = contrast_stretched_image.getPixelWriter();
        // Get an interface to read from the original image passed as the
        // parameter to the function
        PixelReader image_reader = image.getPixelReader();


        // Iterate over all pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = image_reader.getColor(x, y);
                double red = color.getRed();
                double green = color.getGreen();
                double blue = color.getBlue();
                double outR = getContrastStretchedValue(red, r1, r2, s1, s2);
                double outG = getContrastStretchedValue(green, r1, r2, s1, s2);
                double outB = getContrastStretchedValue(blue, r1, r2, s1, s2);
                color = Color.color(outR, outG, outB);
                contrast_stretched_image_writer.setColor(x, y, color);
            }
        }
        return contrast_stretched_image;
    }

    //Applies the formula to compute contrast stretching with provided color channel and threshold
    private static double getContrastStretchedValue(double channel, double r1, double r2, double s1, double s2) {
        double out;
        if (channel < r1) {
            out = (s1 / r1) * channel;
        } else if (r1 <= channel && channel <= r2) {
            out = (((s2 - s1) / (r2 - r1)) * (channel - r1)) + s1;
        } else {
            out = (((1. - s2) / (1. - r2)) * (channel - r2)) + s2;
        }
        return out;
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        stage.setTitle("Photoshop");
        gammaField.setPromptText("Gamma");
        // Read the image
        image = new Image(new FileInputStream("raytrace.jpg"));

        // Create the graphical view of the image
        imageView = new ImageView(image);

        // Create the simple GUI
        Button invert_button = new Button("Invert");
        Button gamma_button = new Button("Gamma Correct");
        Button contrast_button = new Button("Contrast Stretching");
        Button histogram_button = new Button("Histograms");
        Button cc_button = new Button("Cross Correlation");

        // Add all the event handlers
        invert_button.setOnAction(event -> {
            System.out.println("Invert");
            Image inverted_image = ImageInverter(imageView.getImage());
            // Update the GUI so the new image is displayed
            imageView.setImage(inverted_image);
        });

        gamma_button.setOnAction(event -> {
            System.out.println("Gamma Correction");
            if (!gammaField.getText().isEmpty()) {
                Image gamma_corrected_image = gammaCorrection(imageView.getImage());
                imageView.setImage(gamma_corrected_image);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gamma value is not set!");
                alert.showAndWait();
            }
        });

        contrast_button.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Contrast Stretching");
                //Load up a new FXML window to change r1,s1,r2,s2 values using a line chart
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ContrastChart.fxml"));
                    Parent root1 = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UTILITY);
                    stage.setTitle("Contrast Stretching Chart");
                    stage.setScene(new Scene(root1));
                    stage.show();
                    //All the manipulations to an image are done in the controller
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        histogram_button.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Histogram");
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HistogramChart.fxml"));
                    Parent root1 = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UTILITY);
                    stage.setTitle("Histogram Chart");
                    stage.setScene(new Scene(root1));
                    stage.show();
                    //All the manipulations to an image are done in the controller
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cc_button.setOnAction(event -> {
            System.out.println("Cross Correlation");
            //Initializing the Laplacian Matrix
            laplacianInit();
            Image crossCorrelationImage = crossCorrelation();
            imageView.setImage(crossCorrelationImage);
        });
        // Using a flow pane
        FlowPane root = new FlowPane();
        // Gaps between buttons
        root.setVgap(10);
        root.setHgap(5);

        // Add all the buttons and the image for the GUI
        root.getChildren().addAll(invert_button, gamma_button, gammaField, contrast_button,
                histogram_button, cc_button, imageView);

        // Display to user
        Scene scene = new Scene(root, 941, 664);
        stage.setScene(scene);
        stage.show();
    }

    // Example function of invert
    private Image ImageInverter(Image image) {
        // Find the width and height of the image to be process
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        // Create a new image of that width and height
        WritableImage inverted_image = new WritableImage(width, height);
        // Get an interface to write to that image memory
        PixelWriter inverted_image_writer = inverted_image.getPixelWriter();
        // Get an interface to read from the original image passed as the
        // parameter to the function
        PixelReader image_reader = image.getPixelReader();

        // Iterate over all pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = image_reader.getColor(x, y);
                color = Color.color(1.0 - color.getRed(), 1.0 - color.getGreen(), 1.0 - color.getBlue());
                inverted_image_writer.setColor(x, y, color);
            }
        }
        return inverted_image;
    }

    private Image gammaCorrection(Image image) {
        double gammaValue = Double.parseDouble(gammaField.getText());
        // Look-up Table
        double[] gammaTable = new double[256];
        for (int i = 0; i < 256; i++) {
            gammaTable[i] = Math.pow((double) i / 255, 1.0 / gammaValue);
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage gamma_corrected_image = new WritableImage(width, height);
        PixelWriter gamma_corrected_image_writer = gamma_corrected_image.getPixelWriter();
        PixelReader image_reader = image.getPixelReader();

        // Iterate over all pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // For each pixel, get the colour between 0 and 1 -> I variable
                Color color = image_reader.getColor(x, y);
                // Applying the formula from lecture for R, G and B
                color = Color.color(gammaTable[(int) (color.getRed() * 255)],
                        gammaTable[(int) (color.getGreen() * 255)], gammaTable[(int) (color.getBlue() * 255)]);
                gamma_corrected_image_writer.setColor(x, y, color);
            }
        }
        return gamma_corrected_image;
    }

    private Image crossCorrelation() {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage crossCorrelationImage = new WritableImage(width, height);
        PixelWriter image_writer = crossCorrelationImage.getPixelWriter();

        //The list where all sums of the products will be stored
        int[][][] map = new int[width][height][3];
        // Iterate over all pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Protecting ourselves from going out of bounds of the array
                if (((x + 4) < width) && ((y + 4) < height)) {
                    //Calculating the sum. 0 - Red, 1 - Green, 2 - Blue
                    map[x][y][0] = calcCenter("r", x, y);
                    map[x][y][1] = calcCenter("g", x, y);
                    map[x][y][2] = calcCenter("b", x, y);
                }
            }
        }

        int min = 0;
        int max = 0;
        //Iterating through each value of 3D array of sums and finding the overall min and max
        for (int[][] u : map) {
            for (int[] uu : u) {
                for (int current : uu) {
                    if (current < min) {
                        min = current;
                    }
                    if (current > max) {
                        max = current;
                    }
                }
            }
        }

        //Applying the normalisation and building the cross-correlated image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int newRed = ((map[x][y][0] - min) * 255) / (max - min);
                int newGreen = ((map[x][y][1] - min) * 255) / (max - min);
                int newBlue = ((map[x][y][2] - min) * 255) / (max - min);
                Color color = Color.color
                        ((float) newRed / 255.0, (float) newGreen / 255.0, (float) newBlue / 255.0);
                image_writer.setColor(x, y, color);
            }
        }
        return crossCorrelationImage;
    }

    //Hard-Coded 5x5 Matrix
    private void laplacianInit() {
        laplacianMatrix[0][0] = -4;
        laplacianMatrix[0][1] = -1;
        laplacianMatrix[0][2] = 0;
        laplacianMatrix[0][3] = -1;
        laplacianMatrix[0][4] = -4;

        laplacianMatrix[1][0] = -1;
        laplacianMatrix[1][1] = 2;
        laplacianMatrix[1][2] = 3;
        laplacianMatrix[1][3] = 2;
        laplacianMatrix[1][4] = -1;

        laplacianMatrix[2][0] = 0;
        laplacianMatrix[2][1] = 3;
        laplacianMatrix[2][2] = 4;
        laplacianMatrix[2][3] = 3;
        laplacianMatrix[2][4] = 0;

        laplacianMatrix[3][0] = -1;
        laplacianMatrix[3][1] = 2;
        laplacianMatrix[3][2] = 3;
        laplacianMatrix[3][3] = 2;
        laplacianMatrix[3][4] = -1;

        laplacianMatrix[4][0] = -4;
        laplacianMatrix[4][1] = -1;
        laplacianMatrix[4][2] = 0;
        laplacianMatrix[4][3] = -1;
        laplacianMatrix[4][4] = -4;
    }

    //Gets the value of a specified channel of a pixel with coordinates x and y
    private int colorGetter(int x, int y, String channel) {
        PixelReader image_reader = image.getPixelReader();
        Color color = image_reader.getColor(x, y);
        int val;
        switch (channel) {
            case "r":
                val = (int) (color.getRed() * 255);
                break;
            case "g":
                val = (int) (color.getGreen() * 255);
                break;
            case "b":
                val = (int) (color.getBlue() * 255);
                break;
            default:
                val = -1; //Error Value
        }
        return val;
    }

    private int calcCenter(String channel, int x, int y) {
        return (laplacianMatrix[0][0] * colorGetter(x, y, channel) +
                laplacianMatrix[0][1] * colorGetter(x, y + 1, channel) +
                laplacianMatrix[0][2] * colorGetter(x, y + 2, channel) +
                laplacianMatrix[0][3] * colorGetter(x, y + 3, channel) +
                laplacianMatrix[0][4] * colorGetter(x, y + 4, channel) +

                laplacianMatrix[1][0] * colorGetter(x + 1, y, channel) +
                laplacianMatrix[1][1] * colorGetter(x + 1, y + 1, channel) +
                laplacianMatrix[1][2] * colorGetter(x + 1, y + 2, channel) +
                laplacianMatrix[1][3] * colorGetter(x + 1, y + 3, channel) +
                laplacianMatrix[1][4] * colorGetter(x + 1, y + 4, channel) +

                laplacianMatrix[2][0] * colorGetter(x + 2, y, channel) +
                laplacianMatrix[2][1] * colorGetter(x + 2, y + 1, channel) +
                laplacianMatrix[2][2] * colorGetter(x + 2, y + 2, channel) +
                laplacianMatrix[2][3] * colorGetter(x + 2, y + 3, channel) +
                laplacianMatrix[2][4] * colorGetter(x + 2, y + 4, channel) +

                laplacianMatrix[3][0] * colorGetter(x + 3, y, channel) +
                laplacianMatrix[3][1] * colorGetter(x + 3, y + 1, channel) +
                laplacianMatrix[3][2] * colorGetter(x + 3, y + 2, channel) +
                laplacianMatrix[3][3] * colorGetter(x + 3, y + 3, channel) +
                laplacianMatrix[3][4] * colorGetter(x + 3, y + 4, channel) +

                laplacianMatrix[4][0] * colorGetter(x + 4, y, channel) +
                laplacianMatrix[4][1] * colorGetter(x + 4, y + 1, channel) +
                laplacianMatrix[4][2] * colorGetter(x + 4, y + 2, channel) +
                laplacianMatrix[4][3] * colorGetter(x + 4, y + 3, channel) +
                laplacianMatrix[4][4] * colorGetter(x + 4, y + 4, channel));
    }
}