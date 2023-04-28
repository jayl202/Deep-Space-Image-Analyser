package com.example.deepspaceimageeditor;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;
import javafx.scene.control.Button;

import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Point2D;

import static javafx.collections.FXCollections.observableArrayList;

public class EditorController implements Initializable {
    public WritableImage newBlackAndWhiteImage;
    public int width;
    public int height;
    public int[][] imageArray;
    public UnionFind unionFind;
    public static final int WHITE = 1;
    public static final int BLACK = 0;
    private int luminanceValue = 40;
    FileChooser fileChooser = new FileChooser();
    HashMap<Integer, ArrayList<Integer>> starMap;
    private List<Circle> starCircles = new ArrayList<>();
    private Tooltip starTooltip;
    private Map<Circle, ArrayList<Integer>> circleStarMap = new HashMap<>();
    @FXML
    private AnchorPane anchorPane;
    @FXML
    public ImageView origImage;
    @FXML
    public ImageView newImage;
    @FXML
    public Button chooseFileButton;
    @FXML
    public TextField numberOfStarsTextField;
    @FXML
    public Button circleStarsButton;
    @FXML
    public Button labelStarsButton;
    @FXML
    public TextField luminanceTextField;
    @FXML
    private TextField sizeThresholdTextField;
    @FXML
    private Button filterSizeButton;
    EditorController eccon;

    public void getFile(ActionEvent event) {
        fileChooser.setTitle("Select an image to show");
        File selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (selectedFile != null) {
            // create image from selected file
            Image image = new Image(selectedFile.toURI().toString());
            // display the image in the imageView
            origImage.setImage(image);
            newImage.setImage(image);
            blackAndWhite();
        }
    }

    public WritableImage blackAndWhite() {
        newImage.setImage(origImage.getImage());
        Image image = newImage.getImage();
        PixelReader pixelReader = image.getPixelReader();

        height = (int) image.getHeight();
        width = (int) image.getWidth();

        imageArray = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = pixelReader.getColor(col, row);
                int red = (int) (color.getRed() * 255);
                int green = (int) (color.getGreen() * 255);
                int blue = (int) (color.getBlue() * 255);
                int luminance = (red + green + blue) / 3;

                if (luminance > luminanceValue) {
                    imageArray[row][col] = WHITE;
                } else {
                    imageArray[row][col] = BLACK;
                }
            }
        }

        // Create a UnionFind object with the number of pixels in the image as size
        unionFind = new UnionFind(height * width);

        // Iterate over the imageArray and union adjacent white pixels
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int currentPixel = imageArray[row][col];
                int currentIndex = row * width + col;

                if (currentPixel == WHITE) {
                    // Check if the pixel to the right is white
                    if (col + 1 < width && imageArray[row][col + 1] == WHITE) {
                        int rightIndex = row * width + col + 1;
                        unionFind.unify(currentIndex, rightIndex);
                    }

                    // Check if the pixel below is white
                    if (row + 1 < height && imageArray[row + 1][col] == WHITE) {
                        int belowIndex = (row + 1) * width + col;
                        unionFind.unify(currentIndex, belowIndex);
                    }
                }
            }
        }

        // Create a new image from the processed image array
        WritableImage processedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = processedImage.getPixelWriter();
        newImage.setImage(processedImage);

        // Create a hashmap to track each star
        starMap = new HashMap<>();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int color = imageArray[row][col];

                if (color == WHITE) {
                    int index = row * width + col;
                    int root = unionFind.find(index);

                    // Add the pixel to the appropriate ArrayList in the hashmap
                    if (!starMap.containsKey(root)) {
                        starMap.put(root, new ArrayList<>());
                    }
                    starMap.get(root).add(index); //new int[]{col, row});
                }
                // Set the pixel color in the processed image
                if (color == WHITE) {
                    pixelWriter.setColor(col, row, Color.WHITE);
                } else {
                    pixelWriter.setColor(col, row, Color.BLACK);
                }
            }
        }

        //for test
        int largestRoot= Collections.max(starMap.keySet(),(a, b)->starMap.get(a).size()-starMap.get(b).size());
        List lis=starMap.get(largestRoot);


        newBlackAndWhiteImage = processedImage;
        displayStarsWithRandomColor();

        return processedImage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser.setInitialDirectory(new File("C:\\Users\\jayla\\Desktop\\Year 2\\Semester 2\\Data Structures & Algorithms 2\\DeepSpaceImageFolder"));
        eccon=this;
        starTooltip = new Tooltip("Hello");
        newImage.setOnMouseMoved(event -> handleMouseMove(event));
    }

    public void reset(ActionEvent event) {
        newImage.setImage(origImage.getImage());
    }



    public void type(ActionEvent event) {
    }

    public void circleStars(ActionEvent event) {
        Image originalImage = origImage.getImage();

        Canvas canvas = new Canvas(originalImage.getWidth(), originalImage.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(originalImage, 0, 0);

        for (ArrayList<Integer> set : starMap.values()) {
            graphicsContext.setStroke(Color.BLUE);
            graphicsContext.setLineWidth(2);
            graphicsContext.setFill(Color.TRANSPARENT);

            int radius = (int) Math.sqrt(set.size());

            int centreX = 0, centreY = 0;
            for (int index : set) {
                int x = index % width;
                int y = index / width;
                centreX += x;
                centreY += y;
            }
            centreX /= set.size();
            centreY /= set.size();

            graphicsContext.strokeOval(centreX - radius, centreY - radius, radius * 2, radius * 2);

            Circle circle = new Circle(centreX, centreY, radius);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.TRANSPARENT);
            circleStarMap.put(circle, set);

            Tooltip tooltip = new Tooltip("Pixels: " + set.size());
            Tooltip.install(circle, tooltip);

            // Add a click event listener to each star (circle) to change its color
            circle.setOnMouseClicked(eventClick -> {
                Color newColor = getRandomColour();
                circle.setStroke(newColor);
                refreshDisplay();
            });
        }

        WritableImage circleImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, circleImage);

        newImage.setImage(circleImage);
        ArrayList stuff = new ArrayList(circleStarMap.keySet());
        stuff.removeIf(p -> anchorPane.getChildren().contains(p));

        anchorPane.getChildren().addAll(stuff); //circleStarMap.keySet());
        starCircles = new ArrayList<>(circleStarMap.keySet());

        numberOfStarsTextField.setText(Integer.toString(countStars()));
    }


    public void labelStars(ActionEvent event) {
        WritableImage labeledImage = new WritableImage((int) newImage.getImage().getWidth(), (int) newImage.getImage().getHeight());
        PixelWriter pixelWriter = labeledImage.getPixelWriter();
        PixelReader pixelReader = newImage.getImage().getPixelReader();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pixelWriter.setColor(col, row, pixelReader.getColor(col, row));
            }
        }

        Canvas canvas = new Canvas(labeledImage.getWidth(), labeledImage.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(labeledImage, 0, 0);

        // Sort celestial objects by size in descending order
        List<ArrayList<Integer>> sortedCelestialObjects = new ArrayList<>(starMap.values());
        sortedCelestialObjects.sort((a, b) -> b.size() - a.size());

        int label = 1;
        for (ArrayList<Integer> star : sortedCelestialObjects) {
            if (star.size() > 1) { // Use a threshold to filter out small noise
                int centerX = 0, centerY = 0;
                for (int index : star) {
                    int x = index % width;
                    int y = index / width;
                    centerX += x;
                    centerY += y;
                }
                centerX /= star.size();
                centerY /= star.size();

                gc.setFill(Color.RED);
                gc.setFont(new Font("Arial", 10));
                gc.fillText(Integer.toString(label), centerX, centerY);
                label++;
            }
        }

        WritableImage finalLabeledImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(new SnapshotParameters(), finalLabeledImage);

        newImage.setImage(finalLabeledImage);
    }

    public Color getRandomColour() {
        Random rand = new Random();
        return Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public int countStars() {
        int starCount = 0;
        for (ArrayList<Integer> star : starMap.values()) {
            if (star.size() > 1) { // Use a threshold to filter out small noise
                starCount++;
            }
        }
        return starCount;
    }

    public void setLuminance(ActionEvent event) {
        String luminanceString = luminanceTextField.getText();
        try {
            int newLuminanceValue = Integer.parseInt(luminanceString);
            if (newLuminanceValue < 10 || newLuminanceValue > 255) {
                // Display an error message if the value is out of range
                System.out.println("Luminance value must be between 10 and 255");
            } else {
                // Update the luminance value and apply it to the black and white image
                luminanceValue = newLuminanceValue;
                blackAndWhite();
            }
        } catch (NumberFormatException e) {
            // Display an error message if the input is not a valid number
            System.out.println("Invalid luminance value: " + luminanceString);
        }
    }

    public void giveRandomColour(ActionEvent event) {
        WritableImage blackAndWhite = newBlackAndWhiteImage;
        Canvas canvas = new Canvas(blackAndWhite.getWidth(), blackAndWhite.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(blackAndWhite, 0, 0);

        PixelReader pixelReader = blackAndWhite.getPixelReader();
        WritableImage coloredImage = new WritableImage((int) blackAndWhite.getWidth(), (int) blackAndWhite.getHeight());
        PixelWriter pixelWriter = coloredImage.getPixelWriter();

        // Copy black pixels from the original image
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (pixelReader.getColor(col, row).equals(Color.BLACK)) {
                    pixelWriter.setColor(col, row, Color.BLACK);
                }
            }
        }

        // Color disjoint sets
        for (List<Integer> set : starMap.values()) {
            Color randomColor = getRandomColour();

            for (int index : set) {
                int x = index % width;
                int y = index / width;
                pixelWriter.setColor(x, y, randomColor);
            }
        }

        newImage.setImage(coloredImage);
    }

    public void filterStarsBySize(int sizeThreshold) {
        WritableImage filteredImage = new WritableImage(width, height);
        PixelWriter pixelWriter = filteredImage.getPixelWriter();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = row * width + col;
                int root = unionFind.find(index);

                if (imageArray[row][col] == WHITE && starMap.get(root).size() >= sizeThreshold) {
                    pixelWriter.setColor(col, row, Color.WHITE);
                } else {
                    pixelWriter.setColor(col, row, Color.BLACK);
                }
            }
        }

        newImage.setImage(filteredImage);
        newBlackAndWhiteImage = filteredImage;
    }

    public void filterSize(ActionEvent event) {
        String sizeThresholdString = sizeThresholdTextField.getText();
        try {
            int sizeThreshold = Integer.parseInt(sizeThresholdString);
            if (sizeThreshold < 1 || sizeThreshold > 10000) {
                // Display an error message if the value is out of range
                System.out.println("Size threshold must be between 1 and 1000");
            } else {
                // Filter the stars based on size and update the image
                filterStarsBySize(sizeThreshold);
            }
        } catch (NumberFormatException e) {
            // Display an error message if the input is not a valid number
            System.out.println("Invalid size threshold: " + sizeThresholdString);
        }
    }
    private void handleMouseMove(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        boolean isMouseInsideCircle = false;

        for (Map.Entry<Circle, ArrayList<Integer>> entry : circleStarMap.entrySet()) {
            Circle circle = entry.getKey();
            ArrayList<Integer> star = entry.getValue();

            double centerX = circle.getCenterX();
            double centerY = circle.getCenterY();
            double radius = circle.getRadius();

            double distance = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));

            if (distance <= radius) {
                isMouseInsideCircle = true;

                // Calculate average RGB values
                int redTotal = 0, greenTotal = 0, blueTotal = 0;
                PixelReader pixelReader = origImage.getImage().getPixelReader();

                for (int index : star) {
                    int x = index % width;
                    int y = index / width;
                    Color color = pixelReader.getColor(x, y);
                    redTotal += (int) (color.getRed() * 255);
                    greenTotal += (int) (color.getGreen() * 255);
                    blueTotal += (int) (color.getBlue() * 255);
                }

                int starSize = star.size();
                double redAvg = (double) redTotal / (starSize * 255);
                double greenAvg = (double) greenTotal / (starSize * 255);
                double blueAvg = (double) blueTotal / (starSize * 255);

                // Update the tooltip text
                starTooltip.setText(String.format("Pixels: %d\nHydrogen: %.2f\nOxygen: %.2f\nSulphur: %.2f",
                        starSize, greenAvg, blueAvg, redAvg));
                starTooltip.show(newImage, event.getScreenX() + 10, event.getScreenY() + 10);
                break;
            }
        }

        if (!isMouseInsideCircle) {
            starTooltip.hide();
        }
    }

    private void refreshDisplay() {
        // Clear the existing circles from the anchorPane.
        anchorPane.getChildren().removeAll(starCircles);

        // Add the updated circles to the anchorPane.
        anchorPane.getChildren().addAll(starCircles);
    }

    public void displayStarsWithRandomColor() {
        Image blackAndWhiteImage = newBlackAndWhiteImage;
        Canvas canvas = new Canvas(blackAndWhiteImage.getWidth(), blackAndWhiteImage.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(blackAndWhiteImage, 0, 0);

        Canvas overlayCanvas = new Canvas(blackAndWhiteImage.getWidth(), blackAndWhiteImage.getHeight());
        GraphicsContext overlayGraphicsContext = overlayCanvas.getGraphicsContext2D();
        overlayCanvas.setOpacity(0);

        for (ArrayList<Integer> set : starMap.values()) {
            int radius = (int) Math.sqrt(set.size());

            int centreX = 0, centreY = 0;
            for (int index : set) {
                int x = index % width;
                int y = index / width;
                centreX += x;
                centreY += y;
            }
            centreX /= set.size();
            centreY /= set.size();

            Circle circle = new Circle(centreX, centreY, radius);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.TRANSPARENT);
            circleStarMap.put(circle, set);

            overlayGraphicsContext.beginPath();
            overlayGraphicsContext.arc(centreX, centreY, radius, radius, 0, 360);
            overlayGraphicsContext.closePath();
            overlayGraphicsContext.setFill(Color.TRANSPARENT);
            overlayGraphicsContext.fill();
            overlayGraphicsContext.setLineWidth(0);
            overlayGraphicsContext.setStroke(Color.TRANSPARENT);
            overlayGraphicsContext.stroke();
        }

        overlayCanvas.setOnMouseClicked(e -> {
            double mouseX = e.getX();
            double mouseY = e.getY();
            Circle clickedCircle = null;

            for (Circle circle : circleStarMap.keySet()) {
                if (circle.contains(mouseX, mouseY)) {
                    clickedCircle = circle;
                    break;
                }
            }

            if (clickedCircle != null) {
                ArrayList<Integer> set = circleStarMap.get(clickedCircle);
                Color randomColor = getRandomColour();

                for (int index : set) {
                    int x = index % width;
                    int y = index / width;
                    graphicsContext.setFill(randomColor);
                    graphicsContext.fillRect(x, y, 1, 1);
                }
                WritableImage updatedImage = graphicsContext.getCanvas().snapshot(null, null);
                newImage.setImage(updatedImage);
                newBlackAndWhiteImage = updatedImage;
            }
        });

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(canvas, overlayCanvas);
        newImage.setImage(blackAndWhiteImage);
        newImage.setClip(stackPane);
    }

    public void handleStarClick(MouseEvent event) {
        Image image = newImage.getImage();
        WritableImage currentImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
        PixelReader pixelReader = currentImage.getPixelReader();
        WritableImage coloredImage = new WritableImage((int) currentImage.getWidth(), (int) currentImage.getHeight());
        PixelWriter pixelWriter = coloredImage.getPixelWriter();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pixelWriter.setColor(col, row, pixelReader.getColor(col, row));
            }
        }

        double mouseX = event.getX();
        double mouseY = event.getY();

        List<Integer> clickedStar = null;
        double minDistance = Double.MAX_VALUE;

        for (List<Integer> set : starMap.values()) {
            for (int index : set) {
                int x = index % width;
                int y = index / width;
                double distance = Math.sqrt(Math.pow(x - mouseX, 2) + Math.pow(y - mouseY, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    clickedStar = set;
                }
            }
        }

        if (clickedStar != null && minDistance <= 5) {
            Color randomColor = getRandomColour();
            for (int index : clickedStar) {
                int x = index % width;
                int y = index / width;
                pixelWriter.setColor(x, y, randomColor);
            }
        }

        newImage.setImage(coloredImage);
    }



}





