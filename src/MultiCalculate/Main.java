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
import javafx.application.Platform;

@SuppressWarnings("restriction")
public class Main extends Application {

	private final NumberAxis xAxis = new NumberAxis();
	private final NumberAxis yAxis = new NumberAxis();
	protected LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
	private GetXYData getData = new GetXYData(this);
	private SortThread sortthread = new SortThread(getData);
	@SuppressWarnings("unused")
	private CalculateThread calcuthread = new CalculateThread(getData);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage stage) {

		BorderPane borderPane = new BorderPane();
		Scene scene = new Scene(borderPane, 1024, 800);

		stage.setTitle("MultiCalculate");
		// defining the axes

		xAxis.setLabel("-threads");
		yAxis.setLabel("-time/ms");
		// creating the chart

		lineChart.setTitle("Thread-Time Graph");
		// defining a series
		lineChart.setCreateSymbols(false);

		// 设置进度条 进度状态
		ProgressBar pb = new ProgressBar(-1);
		pb.setPrefSize(200, 20);

		// 标签
		Label label = new Label("   Ready  ");

		// 设置按钮 添加图片

		Image image = new Image(getClass().getResourceAsStream("calculate.png"));
		Button start = new Button("Start", new ImageView(image));

		// 设置单选框

		// 创建ToggleGroup 用于单选排斥
		final ToggleGroup tg = new ToggleGroup();

		// 矩阵
		RadioButton rb1 = new RadioButton();
		rb1.setText("Matrix");
		rb1.setUserData("M");

		// 排序
		RadioButton rb2 = new RadioButton();
		rb2.setText("Sort");
		rb2.setUserData("S");

		// 将单选框添加到ToggleGroup
		rb1.setToggleGroup(tg);
		rb2.setToggleGroup(tg);

		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER_LEFT);
		vb.setSpacing(20);
		vb.getChildren().addAll(rb1, rb2);

		// 面板
		Label size = new Label("Size");
		Label block = new Label("Block");
		Label symbol = new Label("—");
		Label increase = new Label("Increase");

		TextField tf_size = new TextField();
		tf_size.setPrefColumnCount(4);
		TextField tf_block_1 = new TextField();
		tf_block_1.setPrefColumnCount(4);
		TextField tf_block_2 = new TextField();
		tf_block_2.setPrefColumnCount(4);
		TextField tf_increase = new TextField();
		tf_increase.setPrefColumnCount(4);

		ChoiceBox cb = new ChoiceBox();
		cb.setVisible(false);
		cb.setItems(FXCollections.observableArrayList("BubbleSort", "QuickSort", "MergingSort"));

		CheckBox checkBox = new CheckBox("CreatSymbol");

		GridPane gp = new GridPane();
		// gp.setGridLinesVisible(true);
		gp.setAlignment(Pos.CENTER);
		gp.setVgap(5);

		gp.add(size, 0, 0, 1, 1);
		gp.add(tf_size, 3, 0, 1, 1);

		gp.add(checkBox, 6, 0);

		gp.add(block, 0, 1, 1, 1);
		gp.add(tf_block_1, 3, 1, 1, 1);

		gp.add(symbol, 4, 1, 1, 1);

		gp.add(tf_block_2, 6, 1, 1, 1);

		gp.add(increase, 0, 2, 1, 1);
		gp.add(tf_increase, 3, 2, 1, 1);

		gp.add(cb, 5, 2, 3, 1);

		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.setSpacing(20);
		hb.setPadding(new Insets(5, 5, scene.getHeight() * 0.05, 5));
		hb.getChildren().addAll(label, pb, vb, gp, start);

		borderPane.setCenter(lineChart);
		borderPane.setBottom(hb);

		// 添加事件响应
		DealAction(start, tg, tf_size, tf_block_1, tf_block_2, tf_increase, cb, pb, label, checkBox);

		stage.setScene(scene);
		stage.getIcons().add(new Image(Main.class.getResourceAsStream("Data.png")));
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings("rawtypes")
	private void DealAction(Button start, ToggleGroup tg, TextField tf_size, TextField tf_block_1, TextField tf_block_2,
			TextField tf_increase, ChoiceBox cb, ProgressBar pb, Label label, CheckBox checkBox) {

		start.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				getData.Clear();
				ButtonTask buttonTask = new ButtonTask();
				buttonTask.setParameters(tg, tf_size, tf_block_1, tf_block_2, tf_increase, cb, pb, label);
				buttonTask.start();
			}
		});

		// 设置单选事件
		tg.selectedToggleProperty()
				.addListener((ObservableValue<? extends Toggle> ov, Toggle old_Toggle, Toggle new_Toggle) -> {
					if (tg.getSelectedToggle() != null) {
						;
						if (tg.getSelectedToggle().getUserData().toString().equals("M")) {
							cb.setVisible(false);
						} else {
							cb.setVisible(true);
						}
					}
				});
		checkBox.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> ov, Boolean old_Value, Boolean new_Value) -> {
					if (checkBox.isSelected()) {
						lineChart.setCreateSymbols(true);
					} else {
						lineChart.setCreateSymbols(false);
					}
				});
	}

	class ButtonTask extends Thread {
		private ToggleGroup tg = null;
		private TextField tf_size = null;
		private TextField tf_block_1 = null;
		private TextField tf_block_2 = null;
		private TextField tf_increase = null;
		private ProgressBar pb = null;
		private Label label = null;
		private ChoiceBox cb = null;

		public void setParameters(ToggleGroup tg, TextField tf_size, TextField tf_block_1, TextField tf_block_2,
				TextField tf_increase, ChoiceBox cb, ProgressBar pb, Label label) {
			this.tg = tg;
			this.tf_size = tf_size;
			this.tf_block_1 = tf_block_1;
			this.tf_block_2 = tf_block_2;
			this.tf_increase = tf_increase;
			this.pb = pb;
			this.label = label;
			this.cb = cb;
		}

		public void run() {
			// TODO Auto-generated method stub
			if (tg.getSelectedToggle() == null)
				return;
			int size_dim = 0, block1 = 0, block2 = 0, increase = 0;
			try {
				size_dim = Integer.parseInt(tf_size.getText());
				block1 = Integer.parseInt(tf_block_1.getText());
				block2 = Integer.parseInt(tf_block_2.getText());
				increase = Integer.parseInt(tf_increase.getText());

			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Error:" + e.getMessage());
			}

			if (size_dim == 0 || block1 == 0 || block2 == 0 || increase == 0)
				return;
			double pro = block2 - block1;
			String seriersName = "";
			if (tg.getSelectedToggle().getUserData().toString().equals("M")) {
				int i = block1;
				int I = 0;
				for (; i <= block2; i += increase, I += increase) {
					CalculateThread.setConfig(size_dim, i);
					CalculateThread.execute();
					final double tmpi = I;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							label.setText("Calculating...");
							if (pro != 0)
								pb.setProgress((double) (tmpi / pro));
						}
					});
				}
				seriersName = "Metrix";
			} else {
				int i = block1;
				int I = 0;
				String method = cb.getSelectionModel().getSelectedItem().toString();
				for (; i <= block2; i += increase, I += increase) {
					sortthread.setConfig(size_dim, i, method);
					sortthread.execute();
					final double tmpi = I;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							label.setText("Calculating...");
							if (pro != 0)
								pb.setProgress((double) (tmpi / pro));
						}
					});
				}
				seriersName = method;
			}
			final String tmpName = seriersName;
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					label.setText("   Ready  ");
					getData.series.setName(tmpName);
					pb.setProgress(-1);
				}
			});
		}
	}

}