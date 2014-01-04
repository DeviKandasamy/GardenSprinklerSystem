package userInterface;

import businessLogic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*; 
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Design of the entire Sprinkler System user interface 
 * @author Devi
 *
 */
class SprinklerGUI extends JFrame implements ActionListener {	
	
	SprinklerConfigurationManager sprinklerconfigmgr = new SprinklerConfigurationManager();
	
	private JPanel headingPanel;
	private JSplitPane splitPaneLeft;
	private JLabel mainLbl, dateLbl, timeLbl, timeField, dateField, lblLable,  lblNote;
	private JPanel welcome, status ,configure ,enable, settings;	
	private JLabel lblDate, lblTime, lblTemp, lbleHr,lblSelDate, lblsMin, lbleMin, lbleSec, lblsHr, lblsSec, selSprinkID1, selSprinkID2, selSprinkID3;	
	private JComboBox sHr, eHr, cTemp, eMin, sMin, sSec, eSec, brknSprinklerComboBox, tempComboBox;
	private JComboBox groupField, selField, selSprinkIDField, selField1, selSprinkIDField1, selField2, selSprinkIDField2, selSprinkIDField3;
	private JLabel selGroup,  sprinklerStat, sprinklerStatField,activStatus,activStatusField,water,waterField, selSprinkID, selGroup1, selGroup2;
	private String endTimeHours = "1" ,sprinklerID = "E1" , startTimeHours = "1", temperature = "60", endTimeMin="1", startTimeMin ="1", startTimeSec = "1",endTimeSec = "1", cmbType = "North";
	private String category, choice, day = "";
	private JButton[] buttonDate = new JButton[49];
	private ImagePanel panel;
	private int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
	private int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
	
	/**
	 * Constructor class initializes the display panel
	 * @throws URISyntaxException
	 */
	public SprinklerGUI() throws URISyntaxException{
		super ("Sprinkler Bee Garden Sprinkler System");

		//Changing the look and feel
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look and feel.
		}

		//set the location of the window on the screen, get the Screen size		
		Dimension dim = getToolkit().getScreenSize();

		// set the location of where the window appears on the screen		
		setLocation(dim.width/2-100,dim.height/2-100);			

		// set the Layout
		setLayout(new BorderLayout());

		createAllPanels();

		// At this point all panels are created.
		// Add them to the content pane
		getContentPane().add(headingPanel,BorderLayout.NORTH);
		getContentPane().add(splitPaneLeft, BorderLayout.CENTER);
	}

	/**
	 * Method purpose is used for calling the ScheduleConfigurationManager
	 * initialize method
	 */
	public void initialize() {
		sprinklerconfigmgr.initialize();
	}

	/**
	 * Method to set the date and time
	 */
	private void setDateTime(){
		String DATE_FORMAT_NOW = "yyyy-MM-dd";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String today = sdf.format(cal.getTime());
		dateField = new JLabel(today);
		System.out.println("today:"+today);
		javax.swing.Timer timer = new javax.swing.Timer(1000, this);
		timer.start();
	}

	/**
	 * Creation of multiple Panels for the gui design
	 * @throws URISyntaxException
	 */
	private void createAllPanels() throws URISyntaxException{
		// create heading panel
		headingPanel = new JPanel();

		//Contents displayed in the heading
		mainLbl = new JLabel("Sprinkler Bee Garden Sprinkler System");
		dateLbl = new JLabel("       Date:");
		timeLbl = new JLabel("         Time:");

		//date and time
		timeField = new JLabel();
		dateField = new JLabel();

		//To set current date and time
		setDateTime();

		headingPanel.add (mainLbl);
		headingPanel.add (dateLbl);
		headingPanel.add(dateField);
		headingPanel.add (timeLbl);
		headingPanel.add (timeField);

		//Create a tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel pane = new JPanel();

		//Split the pane
		splitPaneLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, pane );
		splitPaneLeft.setResizeWeight(0.5);
		pane.setLayout(null);

		panel = new ImagePanel();
		pane.add(panel);
		Thread sprinklerMapWorker = new Thread() {
			public void run() {
				while (true) {
					//Need to add - Get current time, time_to_sleep = next_event_time - current_time 
					try {
						panel.setColor(sprinklerconfigmgr.sprinklerStatus);
						panel.repaint();
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		sprinklerMapWorker.start();

		//Create the left tabbed panels
		welcome = new JPanel();
		status = new JPanel();
		configure = new JPanel();
		enable = new JPanel();
		settings = new JPanel();

		//Tabbedpane creation
		tabbedPane.addTab("Welcome", null, welcome, null);
		tabbedPane.addTab("Status", null, status, null);
		tabbedPane.addTab("Configure", null, configure, null);
		tabbedPane.addTab("De/Activate", null, enable, null);
		tabbedPane.addTab("Settings", null, settings, null);

		//Individual pane components creation
		welcomePage();
		statusPage();
		configurePage();
		enablePage();
		settings();
	}

	/**
	 * Welcome page with welcome information and a url link
	 * @throws URISyntaxException
	 */
	private void welcomePage() throws URISyntaxException{
		final URI uri = new URI("http://www.busybeesprinklerrepair.com/");
		class OpenUrlAction implements ActionListener {
			@Override public void actionPerformed(ActionEvent e) {
				open(uri);
			}

			private void open(URI uri) {
				// TODO Auto-generated method stub
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(uri);
					} catch (IOException e) { /* TODO: error handling */ }
				} else { /* TODO: error handling */ }
			}
		}
		welcome.setLayout(null);

		JButton welcomeButton = new JButton();	    
		welcomeButton.setText("<HTML><br><I><center>Welcome to the</center><b><br><font size=5><center> SprinklerBee Garden Sprinkler System</center></font></I></b><br>" +
				"<br>The functionality of the system is described below:<br>" +
				"1.The system is programmed with a weekly schedule to start the sprinklers at designated times.<br>" +
				"2. You can Enable and disable the system.<br> " +
				"3. Shows the activation of the sprinklers. <br>" +
				"4. Displays the status of the sprinklers.<br>" +
				"5. Displays the total water usage.<br>" +
				"6. You can rogramm the system to activate/deactivate the sprinklers based on temperatures.<br>" +
				"7. You can adjust the temperatures to show the activation/deactivation of the sprinklers.<br>" +
				"8. You can program the system to activate/deactivate the sprinkler groups (and individual sprinklers).<br>" +
				"9. Displays a map/schematic of the garden layout and the location of sprinklers.<br>" +
				"10. Displays a graph showing the water usage by the sprinkler groups.<br><br>" +

		   		"For any queries please tap the link below:<br>" +
		   		"Tap the <FONT color=\"#000099\"><U>link</U></FONT>" +
				" to go to the sprinkler system website.</HTML>");

		welcomeButton.setBounds(50, 24, 450, 450);
		welcomeButton.setBorderPainted(false);
		welcomeButton.setOpaque(false);
		welcomeButton.setBackground(Color.LIGHT_GRAY);
		welcomeButton.addActionListener(new OpenUrlAction());
		welcome.add( welcomeButton);
	}

	/**
	 * All the components required for displaying the status of sprinkler
	 * are added to the panel 
	 */
	private void statusPage(){
		GroupLayout layout = new GroupLayout(status);
		status.setLayout(layout);

		JLabel group = new JLabel("Status of                     :");
		group.setBounds(79, 25, 186, 14);
		status.add(group);

		String[] groupStrings =  {"Group of Sprinkler", "Individual Sprinkler"};

		//Create the combo box, select item at index 4.
		//Indices start at 0, so 4 specifies the pig.
		groupField = new JComboBox(groupStrings);
		groupField.setSelectedIndex(0);
		groupField.setBounds(205, 25, 175, 22);
		status.add(groupField);

		lblNote = new JLabel();
		lblNote.setBounds(400, 22, 450, 30);
		lblNote.setText("Note: Please Select the next field");
		lblNote.setVisible(false);
		status.add(lblNote);

		groupField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jcmbType = (JComboBox) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();
				if (cmbType == "Group of Sprinkler"){
					lblNote.setText(null);
					lblNote.setText("Note: Please Select the Group");
					lblNote.setVisible(true);
					sprinklerStat.setVisible(false);
					sprinklerStatField.setVisible(false);
					selSprinkID.setVisible(false);
					selSprinkID.setVisible(false);
					selGroup.setVisible(true);
					selField.setVisible(true);
					activStatus.setVisible(true);
					activStatusField.setVisible(true);
					water.setVisible(true);
					waterField.setVisible(true);
				}
				else if (cmbType == "Individual Sprinkler"){
					lblNote.setText(null);
					lblNote.setText("Note: Please Select the Sprinkler ID");
					lblNote.setVisible(true);
					selGroup.setVisible(false);
					selField.setVisible(false);
					selSprinkID.setVisible(true);
					selSprinkIDField.setVisible(true);
					sprinklerStat.setVisible(true);
					sprinklerStatField.setVisible(true);
					activStatus.setVisible(true);
					activStatusField.setVisible(true);
					water.setVisible(true);
					waterField.setVisible(true);
				}
			}
		});

		selGroup = new JLabel("Select Group              :");
		selGroup.setBounds(79, 75, 186, 14);
		status.add(selGroup);
		selGroup.setVisible(false);

		selField = new JComboBox();
		selField.setModel(new DefaultComboBoxModel(new String[] {"North", "South", "East", "West"}));
		selField.setBounds(205, 75, 75, 20);
		status.add(selField);
		selField.setVisible(false);
		selField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jcmbType = (JComboBox) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();
				String status = null;
				String statusupdate = null;
				String waterupdate = null;
				if (cmbType == "North"){
					statusupdate = sprinklerconfigmgr.getGroupStatus(cmbType);
					activStatusField.setText(null);				
					activStatusField.setText(":  "+ statusupdate);
					waterupdate = sprinklerconfigmgr.getGroupWaterConsumption(cmbType);
					waterField.setText(null);
					waterField.setText(":  "+ waterupdate);
				}
				else if (cmbType == "South"){
					statusupdate = sprinklerconfigmgr.getGroupStatus(cmbType);
					activStatusField.setText(null);				
					activStatusField.setText(":  "+ statusupdate);
					waterupdate = sprinklerconfigmgr.getGroupWaterConsumption(cmbType);
					waterField.setText(null);
					waterField.setText(":  "+ waterupdate);
				}
				else if (cmbType == "East"){
					statusupdate = sprinklerconfigmgr.getGroupStatus(cmbType);
					activStatusField.setText(null);				
					activStatusField.setText(":  "+ statusupdate);
					waterupdate = sprinklerconfigmgr.getGroupWaterConsumption(cmbType);
					waterField.setText(null);
					waterField.setText(":  "+ waterupdate);
				}
				else if (cmbType == "West"){
					statusupdate = sprinklerconfigmgr.getGroupStatus(cmbType);
					activStatusField.setText(null);				
					activStatusField.setText(":  "+ statusupdate);
					waterupdate = sprinklerconfigmgr.getGroupWaterConsumption(cmbType);
					waterField.setText(null);
					waterField.setText(":  "+ waterupdate);
				}
			}});

		sprinklerStat = new JLabel("Sprinkler Group");
		sprinklerStat.setBounds(79, 125, 105, 14);
		status.add(sprinklerStat);
		sprinklerStat.setVisible(false);

		sprinklerStatField = new JLabel(":");
		sprinklerStatField.setBounds(190, 125, 46, 14);
		status.add(sprinklerStatField);
		sprinklerStatField.setVisible(false);

		activStatus = new JLabel("Activation status");
		activStatus.setBounds(79, 175, 105, 14);
		status.add(activStatus);
		activStatus.setVisible(false);

		activStatusField = new JLabel(":");
		activStatusField.setBounds(190, 175,300, 14);
		status.add(activStatusField);
		activStatusField.setVisible(false);

		water = new JLabel("Total Water Consumed by Group (in gallons)");
		water.setBounds(19, 225, 300, 14);
		status.add(water);
		water.setVisible(false);

		waterField = new JLabel(":");
		waterField.setBounds(310, 225, 46, 14);
		status.add(waterField);
		waterField.setVisible(false);

		selSprinkID = new JLabel("Select Sprinkler ID    :");
		selSprinkID.setBounds(79, 75, 186, 14);
		status.add(selSprinkID);
		selSprinkID.setVisible(false);

		selSprinkIDField = new JComboBox();
		selSprinkIDField.setModel(new DefaultComboBoxModel(new String[] {"N1", "N2", "N3", "S1", "S2","S3", "E1", "E2","E3", "W1", "W2", "W3"}));
		selSprinkIDField.setBounds(205, 75, 75, 20);
		status.add(selSprinkIDField);
		selSprinkIDField.setVisible(false);
		selSprinkIDField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jcmbType = (JComboBox) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();
				String statusOfSprinkler;
				String waterupdate = null;
				String sprinklerZone;
				activStatusField.setText(null);
				waterField.setText(null);

				statusOfSprinkler = sprinklerconfigmgr.getSprinklerStatus(cmbType);
				sprinklerZone = sprinklerconfigmgr.getSprinklerZone(cmbType);
				sprinklerStatField.setText(": "+sprinklerZone);
				activStatusField.setText(": " + statusOfSprinkler);
				waterupdate = sprinklerconfigmgr.getGroupWaterConsumption(sprinklerZone);
				waterField.setText(":  "+ waterupdate);
			}
		});

		JButton graphButton = new JButton("");
		graphButton.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\GraphButton.png"));
		graphButton.setBorderPainted(false); 
		graphButton.setContentAreaFilled(false); 
		graphButton.setFocusPainted(false); 
		graphButton.setOpaque(false);
		graphButton.setBounds(390, 362, 250, 80);
		graphButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String northWaterConsump, southWaterConsump, eastWaterConsump, westWaterConsump;
				northWaterConsump = sprinklerconfigmgr.getGroupWaterConsumption("North");
				southWaterConsump = sprinklerconfigmgr.getGroupWaterConsumption("South");
				westWaterConsump = sprinklerconfigmgr.getGroupWaterConsumption("West");
				eastWaterConsump = sprinklerconfigmgr.getGroupWaterConsumption("East");
				GraphsDemo frame1 = new GraphsDemo(northWaterConsump, southWaterConsump, westWaterConsump, eastWaterConsump);
				frame1.setPreferredSize(new Dimension(600,600));
				frame1.pack();
				frame1.setVisible(true);
			}
		});
		status.add(graphButton);

		JLabel lblGraph = new JLabel("Click to View Graph");
		lblGraph.setBounds(465, 330, 155, 21);
		status.add(lblGraph);

		Panel infoPanel = new Panel();
		infoPanel.setBounds(23, 294, 350, 232);
		status.add(infoPanel);
		infoPanel.setBackground(Color.LIGHT_GRAY);
		infoPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Information about Status of Sprinkler :");
		lblNewLabel.setBounds(32, 0, 217, 29);
		infoPanel.add(lblNewLabel);

		JButton bluebtn = new JButton("");
		bluebtn.setBackground(new Color(240, 240, 240));
		bluebtn.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\Blue.png"));
		bluebtn.setBorderPainted(false); 
		bluebtn.setContentAreaFilled(false); 
		bluebtn.setFocusPainted(false); 
		bluebtn.setOpaque(false);
		bluebtn.setBounds(32, 40, 18, 23);
		infoPanel.add(bluebtn);

		JLabel lblCurrentlyOn = new JLabel("ON (currently on) ");
		lblCurrentlyOn.setBounds(79, 40, 105, 23);
		infoPanel.add(lblCurrentlyOn);

		JButton orangebtn = new JButton("");
		orangebtn.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\orange.png"));
		orangebtn.setBorderPainted(false); 
		orangebtn.setContentAreaFilled(false); 
		orangebtn.setFocusPainted(false); 
		orangebtn.setOpaque(false);
		orangebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		orangebtn.setBackground(SystemColor.menu);
		orangebtn.setBounds(32, 86, 18, 23);
		infoPanel.add(orangebtn);

		JButton graybtn = new JButton("");
		graybtn.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\Gray.png"));
		graybtn.setBorderPainted(false); 
		graybtn.setContentAreaFilled(false); 
		graybtn.setFocusPainted(false); 
		graybtn.setOpaque(false);
		graybtn.setBounds(32, 131, 18, 23);
		infoPanel.add(graybtn);

		JLabel lblOkfunctional = new JLabel(" OK (functional)");
		lblOkfunctional.setBounds(79, 86, 90, 23);
		infoPanel.add(lblOkfunctional);

		JLabel lblNotoncurrentlyNot = new JLabel("NOTON (currently not on)");
		lblNotoncurrentlyNot.setBounds(79, 131, 170, 23);
		infoPanel.add(lblNotoncurrentlyNot);

		JButton redbtn = new JButton("");
		redbtn.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\Red.png"));
		redbtn.setBorderPainted(false); 
		redbtn.setContentAreaFilled(false); 
		redbtn.setFocusPainted(false); 
		redbtn.setOpaque(false);
		redbtn.setBounds(32, 175, 18, 23);
		infoPanel.add(redbtn);

		JLabel lblNotoknotFunctional = new JLabel("NOTOK (not functional)");
		lblNotoknotFunctional.setBounds(79, 175, 159, 23);
		infoPanel.add(lblNotoknotFunctional);
	}

	/**
	 * All components required for the configuration of the sprinkler system are
	 * added to the configure pane
	 */
	private void configurePage(){
		configure.setLayout(null);

		selGroup1 = new JLabel("Select Group              :");
		selGroup1.setBounds(60, 35, 186, 14);
		configure.add(selGroup1);

		selField1 = new JComboBox();
		selField1.setModel(new DefaultComboBoxModel(new String[] {"North", "South", "East", "West"}));
		selField1.setBounds(205, 35, 75, 20);
		configure.add(selField1);

		selSprinkID1 = new JLabel("Select Sprinkler ID    :");
		selSprinkID1.setBounds(60, 75, 186, 14);
		configure.add(selSprinkID1);
		selSprinkIDField1 = new JComboBox();
		selSprinkIDField1.setModel(new DefaultComboBoxModel(new String[]{"Select"}));
		selSprinkIDField1.setBounds(205, 75, 75, 20);

		selField1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jendTimeHours = (JComboBox) e.getSource();
				cmbType = (String) jendTimeHours.getSelectedItem();
				if (cmbType == "North"){
					selSprinkIDField1.enable();	
					selSprinkIDField1.setModel(new DefaultComboBoxModel(new String[] {"N1", "N2","N3", "ALL"}));
					selSprinkIDField1.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							JComboBox jsprinklerID = (JComboBox) e.getSource();
							sprinklerID = (String) jsprinklerID.getSelectedItem();
							System.out.println("@sprinklerID: "+sprinklerID + "cmbType"+ cmbType);
						}});

				}else if(cmbType == "South"){
					selSprinkIDField1.enable();	
					selSprinkIDField1.setModel(new DefaultComboBoxModel(new String[] {"S1", "S2","S3", "ALL"}));
					selSprinkIDField1.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							JComboBox jsprinklerID = (JComboBox) e.getSource();
							sprinklerID = (String) jsprinklerID.getSelectedItem();
							System.out.println("@sprinklerID: "+sprinklerID + "cmbType"+ cmbType);
						}});

				}else if (cmbType == "East"){
					selSprinkIDField1.enable();	
					selSprinkIDField1.setModel(new DefaultComboBoxModel(new String[] {"E1", "E2","E3", "ALL"}));
					selSprinkIDField1.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							JComboBox jsprinklerID = (JComboBox) e.getSource();
							sprinklerID = (String) jsprinklerID.getSelectedItem();
							System.out.println("@sprinklerID: "+sprinklerID + "cmbType"+ cmbType);
						}});

				}else if (cmbType == "West"){
					selSprinkIDField1.enable();	
					selSprinkIDField1.setModel(new DefaultComboBoxModel(new String[] {"W1", "W2","W3", "ALL"}));
					selSprinkIDField1.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							JComboBox jsprinklerID = (JComboBox) e.getSource();
							sprinklerID = (String) jsprinklerID.getSelectedItem();
							System.out.println("@sprinklerID: "+sprinklerID + "cmbType"+ cmbType);
						}});
				}
				else if (cmbType == "ALL"){	
					selSprinkIDField1.disable();
				}
			}});

		configure.add(selSprinkIDField1);

		lblDate = new JLabel("Choose Date               : ");
		lblDate.setBounds(60, 130, 150, 21);
		configure.add(lblDate);
		lblDate.setVisible(true);

		lblDate = new JLabel("Selected Date            : ");
		lblDate.setBounds(60, 280, 150, 21);
		configure.add(lblDate);
		lblDate.setVisible(true);

		lblDate = new JLabel("Choose Start Time    : ");
		lblDate.setBounds(60, 330, 150, 21);
		configure.add(lblDate);
		lblDate.setVisible(true);

		lblTime = new JLabel("Choose End Time     : ");
		lblTime.setBounds(60, 380, 150, 21);
		configure.add(lblTime);
		lblTime.setVisible(true);

		lblTemp = new JLabel("Temperature Threshold    :");
		lblTemp.setBounds(60, 430, 150, 14);
		configure.add(lblTemp);
		lblTemp.setVisible(true);

		lblSelDate = new JLabel("");
		lblSelDate.setBounds(230, 280, 100, 21);
		configure.add(lblSelDate);		
		lblSelDate.setVisible(true);

		sHr = new JComboBox();
		sHr.setBounds(230, 330, 50, 20);
		configure.add(sHr);
		sHr.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}));
		sHr.setVisible(true);
		sHr.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox guiTime = (JComboBox) e.getSource();
				startTimeHours = (String) guiTime.getSelectedItem();
				System.out.println("@startTimeHours: "+startTimeHours);
			}});

		eHr = new JComboBox();
		eHr.setBounds(230, 380, 50, 20);
		configure.add(eHr);
		eHr.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"}));
		eHr.setVisible(true);
		eHr.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox guiTime = (JComboBox) e.getSource();
				endTimeHours = (String) guiTime.getSelectedItem();
				System.out.println("@endTimeHours: "+endTimeHours);
			}});

		cTemp = new JComboBox();
		cTemp.setBounds(220, 430, 50, 20);
		configure.add(cTemp);
		cTemp.setModel(new DefaultComboBoxModel(new String[] {"55", "56", "57","58", "59","60", "61", "62", "63", "64", "65", "66", "67","68", "69", "70", "71", "72", "73", "74", "75", "76", "77",  "78","79", "80", "81", "82", "83", "84", "85"}));
		cTemp.setVisible(true);
		cTemp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox cmbTemp = (JComboBox) e.getSource();
				temperature = (String) cmbTemp.getSelectedItem();
				System.out.println("@temperature: "+temperature);
			}});

		lbleHr = new JLabel("HR");
		lbleHr.setBounds(192, 383, 28, 14);
		configure.add(lbleHr);
		lbleHr.setVisible(true);

		lblsHr = new JLabel("HR");
		lblsHr.setBounds(192, 333, 28, 14);
		configure.add(lblsHr);
		lblsHr.setVisible(true);

		eMin = new JComboBox();
		eMin.setBounds(315, 380, 50, 20);
		configure.add(eMin);
		eMin.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60"}));
		eMin.setVisible(true);
		eMin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jendTimeMin = (JComboBox) e.getSource();
				endTimeMin = (String) jendTimeMin.getSelectedItem();
				System.out.println("@endTimeMin: "+endTimeMin);
			}});

		sMin = new JComboBox();
		sMin.setBounds(315, 330, 50, 21);
		configure.add(sMin);
		sMin.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60"}));
		sMin.setVisible(true);
		sMin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jstartTimeMin = (JComboBox) e.getSource();
				startTimeMin = (String) jstartTimeMin.getSelectedItem();
				System.out.println("@startTimeMin: "+startTimeMin);
			}});

		lblsMin = new JLabel("MIN");
		lblsMin.setBounds(286, 330, 28, 21);
		configure.add(lblsMin);
		lblsMin.setVisible(true);

		lbleMin = new JLabel("MIN");
		lbleMin.setBounds(286, 383, 28, 14);
		configure.add(lbleMin);
		lbleMin.setVisible(true);

		sSec = new JComboBox();
		sSec.setBounds(419, 330, 50, 21);
		configure.add(sSec);
		sSec.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60"}));
		sSec.setVisible(true);
		sSec.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jstartTimeSec = (JComboBox) e.getSource();
				startTimeSec = (String) jstartTimeSec.getSelectedItem();
				System.out.println("@startTimeSec: "+startTimeSec);
			}});

		lblsSec = new JLabel("SEC");
		lblsSec.setBounds(372, 333, 37, 14);
		configure.add(lblsSec);
		lblsSec.setVisible(true);

		eSec = new JComboBox();
		eSec.setBounds(419, 380, 50, 20);
		configure.add(eSec);
		eSec.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60"}));
		eSec.setVisible(true);
		eSec.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jendTimeSec = (JComboBox) e.getSource();
				endTimeSec = (String) jendTimeSec.getSelectedItem();
				System.out.println("@endTimeSec: "+endTimeSec);
			}});

		lbleSec = new JLabel("SEC");
		lbleSec.setBounds(372, 383, 37, 14);
		configure.add(lbleSec);
		lbleSec.setVisible(true);
		//adding calendar
		JPanel p2 = new JPanel();
		p2.setBounds(200, 128, 450, 21);
		configure.add(p2);
		p2.setLayout(null);

		JButton btnNewButton = new JButton("<<");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				month--;
				displayDate();
			}
		});
		btnNewButton.setBounds(0, 0, 49, 23);
		p2.add(btnNewButton);

		JButton next = new JButton(">>");
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				month++;
				displayDate();
			}
		});
		next.setBounds(250, 0, 49, 23);
		p2.add(next);

		lblLable = new JLabel("LABEL");
		lblLable.setBounds(100, 4, 100, 14);
		p2.add(lblLable);

		JButton applyconfig = new JButton("Apply Configuration");
		applyconfig.setBounds(180, 490, 142, 23);
		configure.add(applyconfig);
		applyconfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//Apply daily config here
				System.out.println("Hr" + sHr.getItemCount());
				if (cTemp.getSelectedIndex() <=0 ) {
					JOptionPane.showMessageDialog(null,"Please select the temperature field");
				}
				else{
					int starttime , endtime;
					System.out.println(startTimeHours + startTimeMin + startTimeSec);
					starttime =SprinklerConfigurationManager.convertTimetoSec(startTimeHours, startTimeMin, startTimeSec);
					endtime = SprinklerConfigurationManager.convertTimetoSec(endTimeHours, endTimeMin, endTimeSec);
					System.out.println("applyconfig.actionPerformed::sprinklerID : "+sprinklerID);
					sprinklerconfigmgr.applyDailyConfig(sprinklerID, cmbType, starttime, endtime, Integer.parseInt(temperature));
				}
			}
		});

		//Calendar
		JPanel calendarPanel = new JPanel();
		calendarPanel.setBounds(200, 149, 295, 120);
		configure.add(calendarPanel);
		calendarPanel.setLayout(new GridLayout(7,7));

		for (int x = 0; x < buttonDate.length; x++) {
			final int selection = x;
			buttonDate[x] = new JButton();
			buttonDate[x].setFocusPainted(false);
			buttonDate[x].setBackground(Color.white);
			if (x > 6)
				buttonDate[x].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						day = buttonDate[selection].getActionCommand();
						lblSelDate.setText(setPickedDate());                                  
					}
				});          
			calendarPanel.add(buttonDate[x]);             
		} displayDate();	         
	}

	/**
	 * To set the selected date
	 * @return : the selected date
	 */
	String setPickedDate() {
		if (day.equals(""))
			return day;
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(year, month, Integer.parseInt(day));
		String time_selected = sdf.format(cal.getTime());
		System.out.println("time_selected : "+ time_selected );
		SimpleDateFormat f = new SimpleDateFormat("EEEE");
		String day=f.format(cal.getTime());
		System.out.println("day :"+ day);
		return sdf.format(cal.getTime());
	}

	/**
	 * To display the selected date on the label field
	 */
	void displayDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd");
		Date date = new Date();
		int current_date = Integer.parseInt(dateFormat.format(date));
		for (int x = 7; x < buttonDate.length; x++)
			buttonDate[x].setText("");
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"MMMM yyyy");
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(year, month, 1);
		int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
		int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
		for (int x = 6 + dayOfWeek, day = 1; day <= daysInMonth; x++, day++){
			buttonDate[x].setText("" + day);
		}
		lblLable.setText(sdf.format(cal.getTime()));
	}

	/**
	 * The activation and the de-activation components are added in this page, 
	 * to allow the user to activate and deactivate a single or group of sprinklers 
	 * in the sprinkler system  
	 */
	private void enablePage(){

		enable.setLayout(null);

		selGroup2 = new JLabel("Select                        :");
		selGroup2.setBounds(60, 35, 186, 14);
		enable.add(selGroup2);

		selField2 = new JComboBox();
		selField2.setModel(new DefaultComboBoxModel(new String[] {"Group", "IndividualSprinkler", "Entire System"}));
		selField2.setBounds(205, 35, 175, 20);
		enable.add(selField2);

		selSprinkID2 = new JLabel("Select Group              :");
		selSprinkID2.setBounds(60, 75, 186, 14);
		enable.add(selSprinkID2);
		selSprinkIDField2 = new JComboBox();
		selSprinkIDField2.setBounds(205, 75, 75, 20);

		selSprinkID3 = new JLabel("Individual Sprinkler   :");
		selSprinkID3.setBounds(60, 115, 186, 14);
		enable.add(selSprinkID3);
		selSprinkIDField3 = new JComboBox();
		selSprinkIDField3.setBounds(205, 115, 75, 20);

		selField2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox jendTimeHours = (JComboBox) e.getSource();
				category = (String) jendTimeHours.getSelectedItem();
				System.out.println(cmbType);

				if (category.equals("IndividualSprinkler")){
					enable.add(selSprinkIDField3);
					selSprinkIDField3.enable();
					selSprinkIDField3.setModel(new DefaultComboBoxModel(new String[] {"N1", "N2", "N3", "S1", "S2","S3", "E1", "E2","E3", "W1", "W2", "W3"}));
					choice = "N1";
					selSprinkIDField3.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							choice = (String) selSprinkIDField3.getSelectedItem();
							System.out.println(choice);
						}
					});

					selSprinkIDField2.disable();
					enable.add(selSprinkIDField2);
				}
				else if (category.equals("Group")){
					enable.add(selSprinkIDField2);
					selSprinkIDField2.enable();		
					selSprinkIDField2.setVisible(true);
					selSprinkIDField2.setModel(new DefaultComboBoxModel(new String[] {"North", "South", "East", "West"}));
					choice = "North";
					selSprinkIDField2.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							choice = (String) selSprinkIDField2.getSelectedItem();
							System.out.println(choice);
						}
					}
							);
					enable.add(selSprinkIDField3);
					selSprinkIDField3.disable();

				}
				else{
					selSprinkIDField3.setModel(new DefaultComboBoxModel(new String[] {"N1", "N2", "N3", "S1", "S2","S3", "E1", "E2","E3", "W1", "W2", "W3"}));
					enable.add(selSprinkIDField3);
					selSprinkIDField3.disable();		
					selSprinkIDField2.setModel(new DefaultComboBoxModel(new String[] {"North", "South", "East", "West"}));
					selSprinkIDField2.disable();
					enable.add(selSprinkIDField2);	
				}
			}
		});

		JButton enableButton = new JButton("Activate");
		enableButton.setBounds(117, 212, 142, 23);
		enable.add(enableButton);
		enableButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//Enable sprinkler / zone / system
				System.out.println("category :"+ category);
				System.out.println("choice :"+ choice);	
				sprinklerconfigmgr.handleEnableDisable(true, category, choice);
				//}
			}
		});

		JButton disableButton = new JButton("Deactivate");
		disableButton.setBounds(257, 212, 142, 23);
		enable.add(disableButton);
		disableButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//Disable sprinkler / zone / system
				sprinklerconfigmgr.handleEnableDisable(false, category, choice);
			}
		});
	}


	/**
	 * Settings tab has components for setting the temperature and break sprinkler
	 */
	private void settings(){

		settings.setLayout(null);

		JPanel tempPanel = new JPanel();
		tempPanel.setBackground(Color.LIGHT_GRAY);
		tempPanel.setBounds(93, 31, 263, 129);
		tempPanel.setBackground(Color.LIGHT_GRAY);
		tempPanel.setLayout(null);
		settings.add(tempPanel);

		JLabel lblAdjustTemperature = new JLabel("Adjust Temperature");
		lblAdjustTemperature.setBounds(44, 29, 119, 14);
		tempPanel.add(lblAdjustTemperature);

		tempComboBox = new JComboBox();
		tempComboBox.setModel(new DefaultComboBoxModel(new String[] {"55", "56", "57","58", "59","60", "61", "62", "63", "64", "65", "66", "67","68", "69", "70", "71", "72", "73", "74", "75", "76", "77",  "78","79", "80", "81", "82", "83", "84", "85"}));
		tempComboBox.setBounds(191, 26, 48, 20);
		tempPanel.add(tempComboBox);

		JButton btnAdjustTemperature = new JButton("Adjust Temperature");
		btnAdjustTemperature.setBounds(80, 82, 139, 23);
		tempPanel.add(btnAdjustTemperature);
		btnAdjustTemperature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// Set the system temperature
				String systemTemperature = null;
				systemTemperature = tempComboBox.getSelectedItem().toString();
				SprinklerConfigurationManager.setCurrentTemperature(systemTemperature);
			}
		});

		JPanel breakPanel = new JPanel();
		breakPanel.setBackground(Color.LIGHT_GRAY);
		breakPanel.setBounds(93, 230, 263, 150);
		breakPanel.setLayout(null);
		breakPanel.setBackground(Color.LIGHT_GRAY);
		settings.add(breakPanel);

		JLabel lblSprinklerId = new JLabel("Sprinkler ID");
		lblSprinklerId.setBounds(61, 31, 119, 14);
		breakPanel.add(lblSprinklerId);

		brknSprinklerComboBox = new JComboBox();
		brknSprinklerComboBox.setModel(new DefaultComboBoxModel(new String[] {"N1", "N2", "N3", "S1", "S2","S3", "E1", "E2","E3", "W1", "W2", "W3"}));
		brknSprinklerComboBox.setBounds(150, 28, 58, 20);
		breakPanel.add(brknSprinklerComboBox);

		JButton btnBreakSprinkler = new JButton("Break Sprinkler");
		btnBreakSprinkler.setBounds(79, 79, 139, 23);
		breakPanel.add(btnBreakSprinkler);

		btnBreakSprinkler.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent ae) {
				// Break the given sprinkler, set its status to "Broken"
				String sprinklerID = null;
				sprinklerID = brknSprinklerComboBox.getSelectedItem().toString();
				sprinklerconfigmgr.breakSprinkler(sprinklerID);
			}
		});
	}

	/**
	 * To display the time on the GUI
	 */
	public void actionPerformed(ActionEvent event)
	{
		Calendar now = Calendar.getInstance();
		int h = now.get(Calendar.HOUR_OF_DAY);
		int m = now.get(Calendar.MINUTE);
		int s = now.get(Calendar.SECOND);
		timeField.setText("" + h + ":" + m + ":" + s);
	}

	/**
	 * Main method has object to the SprinklerGUI class
	 * @param args
	 * @throws URISyntaxException
	 */
	public static void main( String[] args ) throws URISyntaxException{
		//Create and set up the window.
		SprinklerGUI frame = new SprinklerGUI();
		frame.initialize();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Display the window.
		frame.pack();
		frame.setSize(1200, 650);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

