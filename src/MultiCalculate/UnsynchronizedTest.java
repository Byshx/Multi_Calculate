package MultiCalculate;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import Matrix_Multithread.*;
import Sort_Multithread.*;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class UnsynchronizedTest extends Application {

	public void init(Stage primaryStage) {
		Region veil = new Region();
		veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

		HBox mHbox = new HBox(10);
		ProgressIndicator mBar = new ProgressIndicator(0);
		mBar.setMaxSize(150, 150);
		Label mLabel = new Label("Loading...0%");
		mLabel.setFont(new Font(10));
		mHbox.getChildren().add(mBar);
		mHbox.getChildren().add(mLabel);

		Task<Void> progressTask = new Task<Void>() {

			@Override
			protected void succeeded() {
				super.succeeded();
				updateMessage("Succeeded");
			}

			@Override
			protected void cancelled() {
				super.cancelled();
				updateMessage("Cancelled");
			}

			@Override
			protected void failed() {
				super.failed();
				updateMessage("Failed");
			}

			@Override
			protected Void call() throws Exception {
				for (int i = 0; i < 100; i++) {
					Thread.sleep(25);
					updateProgress(i + 1, 100);
					updateMessage("Loading..." + (i + 1) + "%");
				}
				updateMessage("Finish");
				return null;
			}
		};

		StackPane root = new StackPane();
		root.getChildren().addAll(veil, mBar, mLabel);
		Scene scene = new Scene(root, 300, 250);
		veil.visibleProperty().bind(progressTask.runningProperty());
		mBar.progressProperty().bind(progressTask.progressProperty());
		mLabel.textProperty().bind(progressTask.messageProperty());

		primaryStage.setTitle("The lesson of Task");
		primaryStage.setScene(scene);

		new Thread(progressTask).start();
	}

	@Override
	public void start(Stage primaryStage) {
		init(primaryStage);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}