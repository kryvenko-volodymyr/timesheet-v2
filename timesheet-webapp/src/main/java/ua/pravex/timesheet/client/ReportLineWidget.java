package ua.pravex.timesheet.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import ua.pravex.timesheet.client.ReportLineWidget.Builder;

class ReportLineWidget extends Composite {
	private HorizontalPanel mainPanel;
	private VerticalPanel workInstanceInfo;
	private IntegerBox[] reportLineData;

	public ReportLineWidget (Builder builder) {
		workInstanceInfo = new VerticalPanel();
		mainPanel = new HorizontalPanel();
		mainPanel.add(workInstanceInfo);
		reportLineData = new IntegerBox[builder.getHoursNums().length];
		
		for (int j = 0; j < builder.getHoursNums().length; j++) {
			IntegerBox reportLineDataCell = new IntegerBox();
			reportLineDataCell.getElement().setAttribute("type", "number");
			reportLineDataCell.setValue(builder.getHoursNums()[j]);
			reportLineData[j] = reportLineDataCell;
			mainPanel.add(reportLineData[j]);
		}

		initWidget(mainPanel);
	}
	
	static class Builder {
		private int[] hoursNums;

		public int[] getHoursNums() {
			return hoursNums;
		}

		public void setHoursNums(int[] hoursNums) {
			this.hoursNums = hoursNums;
		}
	}

	public static Builder getBuilder() {
		return new Builder();
	}
}
