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

import java.util.ArrayList;

import Matrix_Multithread.*;
import Sort_Multithread.*;
import javafx.application.Platform;

@SuppressWarnings("restriction")
public class Main extends Application {

	private final NumberAxis xAxis = new NumberAxis();
	private final NumberAxis yAxis = new NumberAxis();
	protected LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

	// ������ȡ���ݽڵ����Ķ�̬����
	private ArrayList<GetXYData> lines = new ArrayList<GetXYData>();
	// �������߳�������
	private SortThread sortthread = null;
	@SuppressWarnings("unused")
	// �������߳̾��������
	private CalculateThread calcuthread = null;
	// �ж��Ƿ���ֹ�̼߳���
	private boolean Run = true;
	// �ж��Ƿ������ڽ��е��������Է�ֹ�ظ����Start��ť��ɵ����ݼ�����ң�
	private boolean hasRunningThread = false;

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

		// ���ý����� ����״̬
		ProgressBar pb = new ProgressBar(-1);
		pb.setPrefSize(200, 20);

		// ��ǩ
		Label label = new Label("    Ready    ");

		// ���ð�ť ���ͼƬ

		Image image1 = new Image(getClass().getResourceAsStream("/MultiCalculate/icon/calculate.png"));
		Button start = new Button("Start", new ImageView(image1));

		Image image2 = new Image(getClass().getResourceAsStream("/MultiCalculate/icon/Interrupt.png"));
		Button interrput = new Button("", new ImageView(image2));

		// ���õ�ѡ��
		// ����ToggleGroup ���ڵ�ѡ�ų�
		final ToggleGroup tg = new ToggleGroup();

		// ����
		RadioButton rb1 = new RadioButton();
		rb1.setText("Matrix");
		rb1.setUserData("M");

		// ����
		RadioButton rb2 = new RadioButton();
		rb2.setText("Sort");
		rb2.setUserData("S");

		// ����ѡ����ӵ�ToggleGroup
		rb1.setToggleGroup(tg);
		rb2.setToggleGroup(tg);

		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER_LEFT);
		vb.setSpacing(20);
		vb.getChildren().addAll(rb1, rb2);

		// ���
		Label size = new Label("Size");
		Label block = new Label("Block");
		Label symbol = new Label("��");
		Label increase = new Label("Increase");

		TextField tf_size = new TextField();
		tf_size.setPrefColumnCount(6);
		TextField tf_block_1 = new TextField();
		tf_block_1.setPrefColumnCount(6);
		TextField tf_block_2 = new TextField();
		tf_block_2.setPrefColumnCount(4);
		TextField tf_increase = new TextField();
		tf_increase.setPrefColumnCount(6);

		// ѡ�����򷽷�
		ChoiceBox<String> cb = new ChoiceBox<String>();
		cb.setVisible(false);
		cb.setItems(FXCollections.observableArrayList("BubbleSort", "QuickSort", "MergingSort"));

		// �Ƿ񴴽����ݽ��
		CheckBox checkBox = new CheckBox("CreatSymbol");

		// �Ƿ�����ʷ����ͼ
		CheckBox KeepGraph = new CheckBox("KeepGraph");

		GridPane gp = new GridPane();
		// gp.setGridLinesVisible(true);
		gp.setAlignment(Pos.CENTER);
		gp.setVgap(5);

		gp.add(size, 0, 0, 1, 1);
		gp.add(tf_size, 3, 0, 1, 1);

		gp.add(checkBox, 5, 0);

		gp.add(block, 0, 1, 1, 1);
		gp.add(tf_block_1, 3, 1, 1, 1);

		gp.add(symbol, 4, 1, 1, 1);

		gp.add(tf_block_2, 5, 1, 1, 1);

		gp.add(increase, 0, 2, 1, 1);
		gp.add(tf_increase, 3, 2, 1, 1);

		gp.add(cb, 5, 2, 3, 1);

		HBox innerH = new HBox();
		innerH.setAlignment(Pos.CENTER);
		innerH.setSpacing(20);
		innerH.getChildren().addAll(start, interrput);

		VBox innerV = new VBox();
		innerV.setAlignment(Pos.CENTER);
		innerV.setSpacing(10);
		innerV.getChildren().addAll(innerH, KeepGraph);

		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.setSpacing(20);
		hb.setPadding(new Insets(5, 5, scene.getHeight() * 0.05, 5));
		hb.getChildren().addAll(label, pb, vb, gp, innerV);

		borderPane.setCenter(lineChart);
		borderPane.setBottom(hb);

		// ����¼���Ӧ
		DealAction(start, interrput, tg, tf_size, tf_block_1, tf_block_2, tf_increase, cb, pb, label, checkBox,
				KeepGraph);

		stage.setScene(scene);

		// ���ͼ��
		stage.getIcons().add(new Image(Main.class.getResourceAsStream("/MultiCalculate/icon/Data.png")));
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void DealAction(Button start, Button interrupt, ToggleGroup tg, TextField tf_size, TextField tf_block_1,
			TextField tf_block_2, TextField tf_increase, ChoiceBox<String> cb, ProgressBar pb, Label label,
			CheckBox checkBox, CheckBox KeepGraph) {

		// ��������
		Main main = this;

		start.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				// ����ϴ���ֹ��û�������������ϴ����µ�Button����
				if (Run && !hasRunningThread) {
					// ��ʾ���ڴ������µ�Button�����ٴε����Ч
					hasRunningThread = true;
					// ��KeepGraph��ѡ��Ϊfalse��������������һ������ͼ������
					if (!KeepGraph.isSelected()) {
						while (!lines.isEmpty()) {
							GetXYData get = lines.remove(0);
							get.Clear();
						}
					}
					// �����µ�����
					GetXYData tmp = new GetXYData(main);
					sortthread = new SortThread(tmp);
					calcuthread = new CalculateThread(tmp);
					lines.add(tmp);

					// ���ϴ�Ϊ�жϲ������˴��뽫�жϺ�ĳ��򻷾�����
					sortthread.Recover();
					CalculateThread.Recover();

					// Button�¼���ӦӦ����ʹ��һ���̣߳���ֹUI����
					ButtonTask buttonTask = new ButtonTask();
					buttonTask.setParameters(tg, tf_size, tf_block_1, tf_block_2, tf_increase, cb, pb, label);
					buttonTask.start();
				}
			}
		});

		interrupt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				// ��������ڽ��е�Button��������ж�
				if (hasRunningThread) {
					sortthread.Interrupt();
					CalculateThread.Interrupt();
					label.setText("Terminated");
					// �����ж��Ƿ����label��������ǩ
					Run = false;
				}
			}
		});

		// ���õ�ѡ�¼�
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
		private ChoiceBox<String> cb = null;

		public void setParameters(ToggleGroup tg, TextField tf_size, TextField tf_block_1, TextField tf_block_2,
				TextField tf_increase, ChoiceBox<String> cb, ProgressBar pb, Label label) {
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
			if (tg.getSelectedToggle() == null) {
				// ���߳̽�����Button����Ӧ�е�����
				hasRunningThread = false;
				return;
			}
			int size_dim = 0, block1 = 0, block2 = 0, increase = 0;

			// ���������е������Ƿ�Ϸ�(���������������ʽ���ų����ļ�Ӣ������)
			try {
				size_dim = Integer.parseInt(tf_size.getText());
				block1 = Integer.parseInt(tf_block_1.getText());
				block2 = Integer.parseInt(tf_block_2.getText());
				increase = Integer.parseInt(tf_increase.getText());

			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Error:" + e.getMessage());
			}

			// ����Ϊ�㲻����Ӧ

			if (size_dim == 0 || block1 == 0 || block2 == 0 || increase == 0)
				return;

			// ���Ը��½������ı���
			double pro = block2 - block1;

			// ���������ߵ�����
			String seriersName = "";

			// �õ���ö��(�Ӵ���ǳ)
			if (tg.getSelectedToggle().getUserData().toString().equals("M")) {
				int i = block1;
				int I = 0;
				for (; i <= block2; i += increase, I += increase) {
					CalculateThread.setConfig(size_dim, i);
					CalculateThread.execute();

					// ����������ⲿ������Ҫʹ��final������
					final double tmpi = I;

					// ��������UI�����������룬�����Platform�н��С�
					// ����һ�ְ취��ʹ��Taskʵ��UI�̵߳��첽ִ��
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (Run)
								label.setText("Calculating..");
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
							if (Run)
								label.setText("Calculating..");
							if (pro != 0)
								pb.setProgress((double) (tmpi / pro));
						}
					});
				}
				seriersName = method;
			}
			// ����������ⲿ����ʱʹ��final
			final String tmpName = seriersName;

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// ���Ready��ǩ
					label.setText("    Ready    ");
					lines.get(lines.size() - 1).series.setName(tmpName);
					// ���-1 ʹ���������ڶ�̬״̬
					pb.setProgress(-1);
				}
			});
			// ����Button��Ӧ�߳�����������Ϊ�˷�ֹ����ֹ�������ٴε��Start��ť����û�д�Լ������ֹ�����ϵ��Start������´μ������
			Run = true;
			// ��Button������ɣ����ٴε��Start Button
			hasRunningThread = false;
		}
	}

}