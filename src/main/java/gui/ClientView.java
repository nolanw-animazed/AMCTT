package gui;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import controller.ClientController;
import data.UserData;
import uk.org.okapibarcode.backend.Code128;
import uk.org.okapibarcode.backend.HumanReadableLocation;
import uk.org.okapibarcode.graphics.Color;
import uk.org.okapibarcode.output.Java2DRenderer;

/**
 * The main GUI of the MCTT, handling the entirety of the front end for the system.
 * 
 * @author Nolan Wright
 */
public class ClientView {
	
	/** The main controller for the MCTT, used to collect data from the back list */
	private ClientController cc;
	/** Main Frame for the project, stored for regeneration purposes */
	private JFrame mainFrame;
	/** Top left Panel for the project, stored for regeneration purposes*/
	private JPanel topLeftPanel;
	/** Top right panel for the project, stored for regeneration purposes*/
	private JPanel topRightPanel;
	/** Bottom Left panel for the project, stored for regeneration purposes*/
	private JPanel bottomLeftPanel;
	/** Bottom right Scroll panel for the project, stored for regeneration purposes*/
	private JScrollPane bottomRightPanel;
	/** The Inside of the scroll panel, stored for regeneration purposes*/
	private JPanel innerBottomRightPanel;
	/** Current Selected Printer (to print barcodes)*/
	private PrintService selectedPrinter;
	
	/**
	 * Main constructor for the Client Controller, however it does not initialize the window (it's waiting for the dataFile to be read)
	 * @param cc The Main Controller for the project
	 */
	public ClientView(ClientController cc) {
		this.cc = cc;
		mainFrame = null;
		topLeftPanel = null;
		topRightPanel = null;
		bottomLeftPanel = null;
		bottomRightPanel = null;
		innerBottomRightPanel = null;
	}

	/**
	 * Initializes the Window for the GUI, preparing the Main Menu for the MCTT
	 */
	public void intializeWindow() {
		mainFrame = new JFrame("Manga Cafe Time System GUI");
		//Build the Header Bar
		buildJMenuBarHeader();
		//If the Program closes, we will attempt to save the data (will not save if the computer shuts off)
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to close the program?", "Confrimation" , JOptionPane.YES_NO_OPTION);
				if(answer == 0) {
					cc.saveData(1);
					System.exit(0);
				}
			}
		});
		
		mainFrame.setSize(600, 600);
		
		//Creating the Navigation buttons for the bottom left panel.
		JButton customerCreationButton = new JButton("Create a new Customer");
		customerCreationButton.addActionListener(e -> {
			this.createCustomerCreationPanel();
		});
		
		JButton customerSearchButton = new JButton("Search for a Customer");
		customerSearchButton.addActionListener(e -> {
			this.createCustomerSearchPanel();
		});
		
		JButton customerLoginButton = new JButton("Login a Customer");
		customerLoginButton.addActionListener(e -> {
			this.createCustomerLoginPanel();
		});
		
		//Create Panel Objects for the four corners.
		topLeftPanel = new JPanel();
		topRightPanel = new JPanel();
		bottomLeftPanel = new JPanel();
		innerBottomRightPanel = new JPanel();
		bottomRightPanel = new JScrollPane(innerBottomRightPanel);
		
		//Bottom Left Panel is filled with the 3 main buttons.
		bottomLeftPanel.setLayout(new GridLayout(3, 0));
		bottomLeftPanel.add(customerCreationButton);
		bottomLeftPanel.add(customerSearchButton);
		bottomLeftPanel.add(customerLoginButton);
		
		//Maybe unneeded, but this is to keep the vertical bar active at all times for clarity
		bottomRightPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Using a 4 corners approach to the program, with each panel getting it's own corner
		mainFrame.setLayout(new GridLayout(2,2));
		mainFrame.add(topLeftPanel);
		mainFrame.add(topRightPanel);
		mainFrame.add(bottomLeftPanel);
		mainFrame.add(bottomRightPanel);
		
		//Used to redisplay
		regenerateWindow();
	}

	/**
	 * Used to build the upper menu bar for the project
	 */
	private void buildJMenuBarHeader() {
		
		JMenuBar header = new JMenuBar();
		
		JMenu themes = new JMenu("Themes");
		
		JMenuItem metal = new JMenuItem("Metal");
		
		JMenuItem nimbus = new JMenuItem("Nimbus");
		
		JMenuItem motif = new JMenuItem("Motif");
		
		JMenuItem windows = new JMenuItem("Windows");
		
		metal.addActionListener(e -> {
			changeUIDesign(UIManager.getCrossPlatformLookAndFeelClassName());
		});
		
		nimbus.addActionListener(e -> {
			changeUIDesign("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		});
		
		motif.addActionListener(e -> {
			changeUIDesign("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		});
		
		windows.addActionListener(e -> {
			changeUIDesign("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		});
		
		themes.add(metal);
		themes.add(nimbus);
		themes.add(motif);
		themes.add(windows);
		
		header.add(themes);
		
		JMenu printers = new JMenu("Printers");
		
		PrintService[] allPrinters = PrintServiceLookup.lookupPrintServices(null, null);
		
		for (PrintService printer : allPrinters) {
			String printerName = printer.getName();
			JMenuItem printerMenuItems = new JMenuItem(printerName);
			
			printerMenuItems.addActionListener(e -> {
				selectedPrinter = printer;
			});
			printers.add(printerMenuItems);
		}
		
		header.add(printers);
		
		mainFrame.setJMenuBar(header);
	}

	private void changeUIDesign(String uiDesign) {
		try {
			UIManager.setLookAndFeel(uiDesign);
			SwingUtilities.updateComponentTreeUI(mainFrame);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	private void regenerateWindow() {
		mainFrame.revalidate();
		mainFrame.repaint();
		mainFrame.setVisible(true);
	}

	private void createCustomerLoginPanel() {
		JFrame frame = new JFrame("MCTT Customer Creation");
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setSize(240, 240);
		
		JLabel customerIdLabel = new JLabel("<html>Please Enter the Customer's ID (Scan their Barcode)</html>");
		
		customerIdLabel.setBorder(new EmptyBorder(10,10,10,10));
		
		JTextField customerIdField = new JTextField();
		
		JButton customerCreationButton = new JButton("Add Customer to the System");

		customerCreationButton.addActionListener(e -> {
				UserData ud = cc.getUserById(customerIdField.getText());
				if(ud != null) {
					this.displayUserData(ud);
					frame.dispose();
				} else {
					JOptionPane.showConfirmDialog(null, "Please enter a valid id for the user.", "Error" , JOptionPane.DEFAULT_OPTION);
				}
		});
		
		frame.setLayout(new GridLayout(3, 1));
		
		frame.add(customerIdLabel);
		frame.add(customerIdField);
		frame.add(customerCreationButton);
		
		frame.setVisible(true);
		
	}

	private void displayUserData(UserData ud) {
		JLabel userNameLabel = new JLabel(ud.getName());
		userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userNameLabel.setVerticalAlignment(SwingConstants.CENTER);
		JLabel userTimeRemainingLabel = new JLabel("<html>Current Time <br>" +ud.currentTimeRemaining() + "</html>");
		
		userTimeRemainingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userTimeRemainingLabel.setVerticalAlignment(SwingConstants.CENTER);
		
		topRightPanel.removeAll();
		
		topRightPanel.setLayout(new GridLayout(3, 2));
		
		JButton displayCustomerInfoButton = new JButton("Customer Info");

		displayCustomerInfoButton.addActionListener(e -> {
			this.displayCustomerInfoWindow(ud);
		});
		
		JButton addCustomerHourButton = new JButton("Add 1 Hour");

		addCustomerHourButton.addActionListener(e -> {
			ud.addHours(1);
			userTimeRemainingLabel.setText("<html>Current Time <br>" + ud.currentTimeRemaining() + "</html>");
		});
		
		JButton addCustomerTimeButton = new JButton("Add/Subtract Time");

		addCustomerTimeButton.addActionListener(e -> {
			this.displayCustomerAddTimeWindow(ud);
		});
		
		JButton customerTimeButton = new JButton("Start Time");
		
		if(ud.isLoggedIn()) {
			customerTimeButton.setText("End Time");
		}

		customerTimeButton.addActionListener(e -> {
			try{
				cc.toggleUser(ud);
				topRightPanel.removeAll();
				this.regenerateWindow();
			} catch(IllegalArgumentException exception) {
				JOptionPane.showConfirmDialog(null, "Please add time to this user.", "Error" , JOptionPane.DEFAULT_OPTION);
			}
		});
		
		topRightPanel.add(userNameLabel);
		topRightPanel.add(displayCustomerInfoButton);
		topRightPanel.add(addCustomerHourButton);
		topRightPanel.add(addCustomerTimeButton);
		topRightPanel.add(userTimeRemainingLabel);
		topRightPanel.add(customerTimeButton);
		
		this.regenerateWindow();
	}

	private void displayCustomerAddTimeWindow(UserData ud) {
		JFrame frame = new JFrame("MCTT Time Addition");
		
		frame.setSize(240, 240);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JLabel hoursInfoPanel = new JLabel("<html>Hours:</html>");
		
		JTextField hoursField = new JTextField("0");
		
		JLabel minutesInfoPanel = new JLabel("<html>Minutes</html>");
		
		JTextField minutesField = new JTextField("0");
		
		JButton addTime = new JButton("Add Time");
		
		addTime.addActionListener(e -> {
			try {
				long hours = Long.parseLong(hoursField.getText());
				long minutes = Long.parseLong(minutesField.getText());
				ud.addHours(hours);
				ud.addMinutes(minutes);
				this.displayUserData(ud);
				this.regenerateWindow();
				frame.dispose();
			} catch(IllegalArgumentException exception) {
				JOptionPane.showConfirmDialog(null, "Please write only numbers into the fields.", "Error" , JOptionPane.DEFAULT_OPTION);
			}
		});
		
		JButton minusTime = new JButton("Minus Time");
		
		minusTime.addActionListener(e -> {
			try {
				int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to subtract time?", "Confrimation" , JOptionPane.YES_NO_CANCEL_OPTION);
				if(answer == 0) {
					long hours = Long.parseLong(hoursField.getText());
					long minutes = Long.parseLong(minutesField.getText());
					ud.subtractHours(hours);
					ud.subtractMinutes(minutes);
					this.displayUserData(ud);
					this.regenerateWindow();
					frame.dispose();
				} else {
					this.displayUserData(ud);
					this.regenerateWindow();
					frame.dispose();
				}
			} catch(IllegalArgumentException exception) {
				JOptionPane.showConfirmDialog(null, "Please write only numbers into the fields.", "Error" , JOptionPane.DEFAULT_OPTION);
			}
		});
		
		frame.setLayout(new GridLayout(3, 2));
		
		frame.add(hoursInfoPanel);
		frame.add(hoursField);
		frame.add(minutesInfoPanel);
		frame.add(minutesField);
		frame.add(addTime);
		frame.add(minusTime);
		
		frame.setVisible(true);
	}

	private void displayCustomerInfoWindow(UserData ud) {
		JFrame frame = new JFrame("Customer Info");
		
		frame.setSize(360, 360);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setLayout(new GridLayout(3, 2));
		
		JLabel helpLabel = new JLabel("<html>Click on any button to switch the data.</html>");
		
		helpLabel.setBorder(new EmptyBorder(10,10,10,10));
		
		JButton name = new JButton("<html>Name - " + ud.getName() + "</html>");
		
		JButton email = new JButton("<html>Email - " + ud.getEmail() + "</html>");
		
		JButton phone = new JButton("<html>Phone - " + ud.getPhoneNumber() + "</html>");
		
		JLabel idLabel = new JLabel("<html>The User's Non-Changeable Id - " + ud.getId() + "</html>");
		
		JButton reprintButton = new JButton("<html>Reprint the Barcode</html>");
		
		name.addActionListener(e -> {
			showCustomerInfoUpdatePanel(ud, 0);
			frame.dispose();
		});
		
		email.addActionListener(e -> {
			showCustomerInfoUpdatePanel(ud, 1);
			frame.dispose();
		});
		
		phone.addActionListener(e -> {
			showCustomerInfoUpdatePanel(ud, 2);
			frame.dispose();
		});
		
		reprintButton.addActionListener(e -> {
			printBarcodeForUser(ud);
		});
		
		idLabel.setBorder(new EmptyBorder(10,10,10,10));
		
		frame.add(helpLabel);
		frame.add(name);
		frame.add(email);
		frame.add(phone);
		frame.add(idLabel);
		frame.add(reprintButton);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setVisible(true);
	}
	
	private void showCustomerInfoUpdatePanel(UserData ud, int setting) {
		JFrame frame = new JFrame("MCTT Time Addition");
		
		frame.setSize(240, 240);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JLabel customerIdLabel = new JLabel();
		
		if(setting == 0) {
			customerIdLabel.setText("Please give the user a new name");
		} else if(setting == 1) {
			customerIdLabel.setText("Please give the user a new email");
		} else {
			customerIdLabel.setText("Please give the user a new phone number");
		}
		
		customerIdLabel.setBorder(new EmptyBorder(10,10,10,10));
		
		JTextField updateTextField = new JTextField();
		
		JButton dataUpdateButton = new JButton("Update the Data");

		dataUpdateButton.addActionListener(e -> {
				try {
					if(setting == 0) {
						ud.setName(updateTextField.getText());
					} else if(setting == 1) {
						ud.setEmail(updateTextField.getText());
					} else {
						ud.setPhoneNumber(updateTextField.getText());
					}
					this.displayUserData(ud);
					this.regenerateWindow();
					frame.dispose();
				} catch(IllegalArgumentException exception) {
					JOptionPane.showConfirmDialog(null, exception.getMessage(), "Error" , JOptionPane.DEFAULT_OPTION);
				}
		});
		
		frame.setLayout(new GridLayout(3, 1));
		
		frame.add(customerIdLabel);
		frame.add(updateTextField);
		frame.add(dataUpdateButton);
		
		frame.setVisible(true);
	}

	private void printBarcodeForUser(UserData ud) {
		Code128 barcode = new Code128();
		barcode.setFontName("Monospaced");
		barcode.setFontSize(16);
		barcode.setModuleWidth(1);
		barcode.setBarHeight(40);
		barcode.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
		barcode.setContent(Long.toString(ud.getId()));

		int width = barcode.getWidth();
		int height = barcode.getHeight();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = image.createGraphics();
		Java2DRenderer renderer = new Java2DRenderer(g2d, 1, Color.WHITE, Color.BLACK);
		renderer.render(barcode);

		try {
			ImageIO.write(image, "png", new File("code128.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File file = new File("code128.png");
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (selectedPrinter != null) {
			DocPrintJob job = selectedPrinter.createPrintJob();
			SimpleDoc doc = new SimpleDoc(inputStream, DocFlavor.INPUT_STREAM.PNG, null);
			
			PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
			
			attributes.add(OrientationRequested.PORTRAIT);
			attributes.add(new PrinterResolution(203, 203, PrinterResolution.DPI));
			attributes.add(new MediaPrintableArea(23, 0, 51, 25, MediaPrintableArea.MM));
			try {
				job.print(doc, attributes);
			} catch (PrintException e) {
				JOptionPane.showConfirmDialog(null, "Selected Printer does not work.", "Error" , JOptionPane.DEFAULT_OPTION);
			}
		} else {
			JOptionPane.showConfirmDialog(null, "Please select a printer from the toolbar.", "Error" , JOptionPane.DEFAULT_OPTION);
		}
		
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createCustomerSearchPanel() {
		JFrame frame = new JFrame("Customer Search Panel");
		
		frame.setSize(360, 360);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setLayout(new GridLayout(4, 2));
		
		JLabel explanation = new JLabel("<html>Please enter as much customer info as possible. At least an email or phone number is required.</html>");
		
		explanation.setBorder(new EmptyBorder(10,10,10,10));
		
		JLabel customerName = new JLabel("<html>Customer Name<html>");
		
		JTextField customerNameField = new JTextField();
		
		JLabel customerEmail = new JLabel("<html>Customer Email<html>");
		
		JTextField customerEmailField = new JTextField();
		
		JLabel customerPhoneNumber = new JLabel("<html>Customer Phone Number<html>");
		
		JTextField customerPhoneNumberField = new JTextField();
		
		customerName.setBorder(new EmptyBorder(10,10,10,10));
		customerEmail.setBorder(new EmptyBorder(10,10,10,10));
		customerPhoneNumber.setBorder(new EmptyBorder(10,10,10,10));
		
		JButton searchButton = new JButton("Search");
		
		searchButton.addActionListener(e -> {
			String customerNameString = customerNameField.getText();
			String customerEmailString = customerEmailField.getText();
			String customerPhoneNumberString = customerPhoneNumberField.getText();
			if((customerEmailString.equals("") || customerEmailString == null) &&
					(customerPhoneNumberString.equals("") || customerPhoneNumberString == null)) {
				JOptionPane.showConfirmDialog(null, "Please add more user info (at least an email or phone number).", "Error" , JOptionPane.DEFAULT_OPTION);
			} else {
				displayFoundUsers(cc.searchForUsers(customerNameString, customerEmailString, customerPhoneNumberString));
				frame.dispose();
			}
		});
		
		frame.add(explanation);
		frame.add(searchButton);
		frame.add(customerName);
		frame.add(customerNameField);
		frame.add(customerEmail);
		frame.add(customerEmailField);
		frame.add(customerPhoneNumber);
		frame.add(customerPhoneNumberField);
		
		frame.setVisible(true);
	}
	
	
	private void displayFoundUsers(UserData[] searchForUsers) {
		JFrame frame = new JFrame("Customer Search Results Panel");
		
		frame.setSize(360, 360);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setLayout(new GridLayout(2 + searchForUsers.length, 1));
		
		JLabel infoLabel = new JLabel("<html>Please click the user that fits with the search</html>");
		infoLabel.setBorder(new EmptyBorder(10,10,10,10));
		
		frame.add(infoLabel);
		
		for(int i = 0; i < searchForUsers.length; i++ ) {
			JButton userButton = new JButton(searchForUsers[i].stringForSearching());
			UserData ud = searchForUsers[i];
			userButton.addActionListener(e -> {
				displayUserData(ud);
				frame.dispose();
				regenerateWindow();
			});
			frame.add(userButton);
		}
		
		frame.setVisible(true);
	}

	/**
	 * Used to continually update the loggedIn Users Panel from the ClientController
	 * @param loggedInUsers Given list from UserList of all the Logged In Users
	 */
	public void displayLoggedInUsers(ArrayList<UserData> loggedInUsers) {
		//Reset the inside of the Bottom Right Panel
		innerBottomRightPanel.removeAll();
		innerBottomRightPanel.setLayout(new BoxLayout(innerBottomRightPanel, BoxLayout.Y_AXIS));
		
		//Use all the UserData to create the LoggedIn User Buttons (and attach actionListeners to each of them)
		for(int i = 0; i < loggedInUsers.size(); i++) {
			UserData ud = loggedInUsers.get(i);
			JButton j = new JButton("<html>" +ud.nameAndTimeRemaining() + "</html>");
			j.addActionListener(e -> {
				displayUserData(ud);
			});
			innerBottomRightPanel.add(j);
		}
		
		//Repaint and Revalidate the Bottom Right Panel, then Regenerate the main Window
		innerBottomRightPanel.revalidate();
		innerBottomRightPanel.repaint();
		
		bottomRightPanel.revalidate();
		bottomRightPanel.repaint();
		
		this.regenerateWindow();
	}

	/**
	 * Used to create the customer creation panel for the GUI. Takes in name, email, and phone numbers to create and add userData to the system.
	 */
	private void createCustomerCreationPanel() {
		JFrame frame = new JFrame("MCTT Customer Creation");
		
		frame.setSize(480, 480);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		//Set up all the JLabels for use within the window.
		JLabel customerInfoPanelInfo1 = new JLabel("<html>Please Enter the Customer Data into this screen. Email or Phone Number is fine. Hover over the text fields if you need anymore info.</html>");
		JLabel customerNameLabel = new JLabel("Customer's First and Last Name");
		JLabel customerEmailLabel = new JLabel("Customer's Email");
		JLabel customerPhoneNumberLabel = new JLabel("Customer's Phone Number");
		
		//Create error helping tooltips for the text fields
		String customerNameFieldToolTip = "First and Last Name Preferred, as it helps with searching. First is required.";
		String customerEmailFieldToolTip = "Needs to have an @ sign in the email.";
		String customerPhoneNumberFieldToolTip = "Just put the 10 numbers of the phone number, no need to add dashs or parentheses.";
		
		//Set up all the Text Fields and apply the Tootltips to them
		JTextField customerNameField = new JTextField();
		customerNameField.setToolTipText(customerNameFieldToolTip);
		JTextField customerEmailField = new JTextField();
		customerEmailField.setToolTipText(customerEmailFieldToolTip);
		JTextField customerPhoneNumberField = new JTextField();
		customerPhoneNumberField.setToolTipText(customerPhoneNumberFieldToolTip);
		
		//Create the Customer Creation Button and apply a listener to it
		JButton customerCreationButton = new JButton("Add Customer to the System");
		customerCreationButton.addActionListener(e -> {
			try {
				//Attempt to make a User Data with the info that was given. If it fails, an error will be spit out.
				UserData ud = new UserData(customerNameField.getText(), customerEmailField.getText(), customerPhoneNumberField.getText());
				cc.addUser(ud);
				this.displayUserData(ud);
				this.regenerateWindow();
				frame.dispose();
			} catch(IllegalArgumentException exception) {
				JOptionPane.showConfirmDialog(null, "<html>" + exception.getMessage() + "</html>", "Error" , JOptionPane.DEFAULT_OPTION);
			}
		});
		
		//Grid is used to format all the fields to be aligned.
		frame.setLayout(new GridLayout(4, 2));
		
		frame.add(customerInfoPanelInfo1);
		frame.add(customerCreationButton);
		
		frame.add(customerNameLabel);
		frame.add(customerNameField);
		frame.add(customerEmailLabel);
		frame.add(customerEmailField);
		frame.add(customerPhoneNumberLabel);
		frame.add(customerPhoneNumberField);
		
		frame.setVisible(true);
		
	}

	/**
	 * Used at startup to get the UserFile for Data via a JFileChooser
	 * @return Returns the Absolute Path String for a File, returns null if a File isn't Chosen
	 */
	public String getUserFile() {
		//Sets the directory of the fileChooser to the current directory
		JFileChooser fileChooser = new JFileChooser(".");
		
		int response = fileChooser.showOpenDialog(null);
		
		//If a file is given back, it takes that exact path and sends it back, if not null is returned.
		if(response == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}
}
