package userInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Display the garden layout on the right side of the pane
 * @author Devi
 *
 */
class ImagePanel extends JPanel {
	private Image img;
	static String image = "C:\\Users\\Devi\\Desktop\\image\\Garden2.png";
	int[] statusOfSprinkler= new int[16];
	Color colorOfSprinkler;
	int sprinklerID;
	int id;

	/**
	 * Constructor to set the image as the argument
	 */
	public ImagePanel(){
		this(new ImageIcon(image).getImage());
	}

	/**
	 * Set the color of the sprinkler - to update status
	 * @param sprinklerStatus
	 */
	public void setColor(int[] sprinklerStatus){
		for(id=0; id<12; id++){
			statusOfSprinkler[id] = sprinklerStatus[id];
		}
	}

	/**
	 * Set the image on the panel
	 * @param img
	 */
	public ImagePanel(Image img) {
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}

	/**
	 * Get the sprinkler ID to update the status of the sprinkler 
	 * @param id
	 * @return color to show the sprinkler status
	 */
	Color getColor(int id) {
		Color newColor;
		switch(id) {
		case 0 : newColor = Color.red; break;
		case 1: newColor = Color.gray;break;
		case 3:
		case 2: newColor = Color.orange;break;
		case 4: newColor = Color.blue;break;
		default: newColor = Color.white;break;
		}
		return newColor;
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);		
		//North1
		g.setColor(getColor(statusOfSprinkler[0]));
		g.fillOval( 160,60, 15, 15 );		
		//North2
		g.setColor(getColor(statusOfSprinkler[1]));
		g.fillOval( 270,120,15, 15 );		
		//North3
		g.setColor(getColor(statusOfSprinkler[2]));
		g.fillOval( 380,60, 15, 15 );		
		//South1
		g.setColor(getColor(statusOfSprinkler[3]));
		g.fillOval( 160,470, 15, 15 );		
		//South2
		g.setColor(getColor(statusOfSprinkler[4]));
		g.fillOval( 270,410,15, 15 );		
		//South3
		g.setColor(getColor(statusOfSprinkler[5]));
		g.fillOval( 380,470, 15, 15 );		
		//West1
		g.setColor(getColor(statusOfSprinkler[6]));
		g.fillOval( 60,200, 15, 15 );		
		//West2
		g.setColor(getColor(statusOfSprinkler[7]));
		g.fillOval( 120,270,15, 15 );		
		//West3
		g.setColor(getColor(statusOfSprinkler[8]));
		g.fillOval( 60,340, 15, 15 );		
		//East1
		g.setColor(getColor(statusOfSprinkler[9]));
		g.fillOval( 480,200, 15, 15 );		
		//East2
		g.setColor(getColor(statusOfSprinkler[10]));
		g.fillOval( 400,270,15, 15 );		
		//East3
		g.setColor(getColor(statusOfSprinkler[11]));
		g.fillOval( 480,380, 15, 15 );		
	}
}
