package MultiCalculate;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Tooltip;
import javafx.application.Platform;

@SuppressWarnings("restriction")
public class GetXYData {

	@SuppressWarnings("rawtypes")
	protected XYChart.Series series = new XYChart.Series();
	@SuppressWarnings("rawtypes")
	private LineChart linechart = null;

	@SuppressWarnings({ "unchecked" })

	public GetXYData(Main main) {
		// TODO Auto-generated constructor stub
		main.lineChart.getData().add(series);
		linechart = main.lineChart;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void GenerateSeries(int thread, long time) {
		XYChart.Data item = new XYChart.Data(thread, time);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				series.getData().add(item);
				Tooltip.install(item.getNode(), new Tooltip("线程数:" + thread + " 耗时:" + time + " ms"));
			}
		});

	}

	public void getData(int thread, long time) {
		GenerateSeries(thread, time);
	}

	public void Clear() {

		// ？？？
		linechart.setAnimated(false);
		series.getData().clear();
		linechart.setAnimated(true);
	}

}
