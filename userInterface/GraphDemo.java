package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Panel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Chart display for displaying the amount of water consumption in each group
 * @author Devi
 *
 */
class GraphsDemo extends JFrame {
	BarChart chart;
	int northWaterVol, southWaterVol, eastWaterVol, westWaterVol;	
	/**
	 * Initialize the value of each group with the volume of water consumed
	 * @param northWater
	 * @param southWater
	 * @param westWater
	 * @param eastWater
	 */
	public GraphsDemo(String northWater, String southWater, String westWater, String eastWater){
		super ("Bar Chart");
		northWaterVol = Integer.parseInt(northWater);
		southWaterVol = Integer.parseInt(southWater);
		westWaterVol = Integer.parseInt(westWater);
		eastWaterVol = Integer.parseInt(eastWater);
		createChart();
	}

	/**
	 * Draw char with the volume of water consumed in each group
	 */
	void createChart(){
		chart = new BarChart();

		//The order of bars in the graph is North, South, West and East.
		chart.addBar(Color.red, northWaterVol);
		chart.addBar(Color.gray, southWaterVol);
		chart.addBar(Color.LIGHT_GRAY, westWaterVol);
		chart.addBar(Color.DARK_GRAY, eastWaterVol);
		this.setContentPane(chart);
		Panel watervolPanel = new Panel();
		watervolPanel.setLayout(null);
		watervolPanel.setBounds(200, 60, 260, 172);
		this.add(watervolPanel);		
		JButton redBtn = new JButton("");
		redBtn.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\Red.png"));
		redBtn.setBounds(32, 40, 18, 23);
		redBtn.setBorderPainted(false); 
		redBtn.setContentAreaFilled(false); 
		redBtn.setFocusPainted(false); 
		redBtn.setOpaque(false);
		watervolPanel.add(redBtn);
		JLabel lblNorth = new JLabel("North"+" : " + northWaterVol);
		lblNorth.setBounds(79, 40, 105, 23);
		watervolPanel.add(lblNorth);
		JButton grayBtn = new JButton("");
		grayBtn.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\Gray.png"));
		grayBtn.setBounds(32, 74, 18, 23);
		grayBtn.setBorderPainted(false); 
		grayBtn.setContentAreaFilled(false); 
		grayBtn.setFocusPainted(false); 
		grayBtn.setOpaque(false);
		watervolPanel.add(grayBtn);
		JButton lightgrayBtn = new JButton("");
		lightgrayBtn.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\lightGray.png"));
		lightgrayBtn.setBounds(32, 108, 18, 23);
		lightgrayBtn.setBorderPainted(false); 
		lightgrayBtn.setContentAreaFilled(false); 
		lightgrayBtn.setFocusPainted(false); 
		lightgrayBtn.setOpaque(false);
		watervolPanel.add(lightgrayBtn);
		JLabel lblSouth = new JLabel("South"+" : " + southWaterVol);
		lblSouth.setBounds(79, 74, 90, 23);
		watervolPanel.add(lblSouth);
		JLabel lblWest = new JLabel("West"+" : " + westWaterVol);
		lblWest.setBounds(79, 108, 170, 23);
		watervolPanel.add(lblWest);
		JButton darkgrayBtn = new JButton("");
		darkgrayBtn.setIcon(new ImageIcon("C:\\Users\\Devi\\Desktop\\image\\darkGray.png"));
		darkgrayBtn.setBounds(32, 142, 18, 23);
		darkgrayBtn.setBorderPainted(false); 
		darkgrayBtn.setContentAreaFilled(false); 
		darkgrayBtn.setFocusPainted(false); 
		darkgrayBtn.setOpaque(false);
		watervolPanel.add(darkgrayBtn);		
		JLabel lblEast = new JLabel(" East"+" : " + eastWaterVol);
		lblEast.setBounds(75, 142, 159, 23);
		watervolPanel.add(lblEast);
		JLabel lblInformation = new JLabel("Information"+" : " + "Group : WaterVolume");
		lblInformation.setBounds(32, 0, 260, 29);
		watervolPanel.add(lblInformation);
	}
}
