package ua.pravex.timesheet.client;

import ua.pravex.timesheet.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
/*import com.vaadin.polymer.paper.widget.PaperButton;*/

public class webapp implements EntryPoint {

	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	private final Messages messages = GWT.create(Messages.class);

	@Override
	public void onModuleLoad() {
		final Label userNameLabel = new Label("Ім'я користувача");
		final Label userAccountLabel = new Label("Обліковий запис користувача");
		final Button logOutButton = new Button("Вихід");
		final HorizontalPanel userHeaderPanel = new HorizontalPanel();
		userHeaderPanel.add(userNameLabel);
		userHeaderPanel.add(userAccountLabel);
		userHeaderPanel.add(logOutButton);
		RootPanel.get("userHeaderPanel").add(userHeaderPanel);

		final Button saveUserReportButton = new Button("Зберегти звіт на сервері");
		final HorizontalPanel saveUserReportPanel = new HorizontalPanel();
		saveUserReportPanel.add(saveUserReportButton);
		RootPanel.get("saveUserReportPanel").add(saveUserReportPanel);

		FlexTable userReportTableHeaderRow = new FlexTable();
		RootPanel.get("userReportTableHeaderRow").add(userReportTableHeaderRow);

		FlexTable userReportTableBody = new FlexTable();
		RootPanel.get("userReportTableBody").add(userReportTableBody);

		userReportTableHeaderRow.setText(0, 0, "First row, first column in userReportTableHeaderRow");
		
		ReportLineWidget.Builder builder = ReportLineWidget.getBuilder();
		builder.setHoursNums(new int[]{1,2,3,4});
		userReportTableBody.setWidget(0, 0, new ReportLineWidget(builder));
		
		
		/*IntegerBox numberInput2 = new IntegerBox();
		numberInput2.getElement().setAttribute("type", "number");
		userReportTableBody.setWidget(1, 1, numberInput2);*/
		
		/*PaperButton addUserReportLineButton = new PaperButton("addUserReportLine");
		addUserReportLineButton.setRaised(true);
		RootPanel.get("addUserReportLinePanel").add(addUserReportLineButton);*/
	}

	public void onModuleLoadOld() {
		final Button sendButton = new Button(messages.sendButton());

		final TextBox nameField = new TextBox();
		nameField.setText(messages.nameField());
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer, new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Remote Procedure Call");
						serverResponseLabel.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}

}
