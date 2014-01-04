package userInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

/**
 * Draw bar chart to display the water consumption
 * @author Devi
 *
 */
class BarChart extends JPanel
{
	JPanel panelgraph = new JPanel();
	private Map<Color, Integer> bars =
			new LinkedHashMap<Color, Integer>();

	public BarChart()
	{	}

	/**
	 * Add new bar to chart
	 * @param color color to display bar
	 * @param value size of bar
	 */
	public void addBar(Color color, int value)
	{
		bars.put(color, value);
	}

	/**
	 *  Calls the UI delegate's paint method, if the UI delegate is non-null.
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		// determine longest bar
		int max = Integer.MIN_VALUE;
		for (Integer value : bars.values())
		{
			max = Math.max(max, value);
		}
		// paint bars
		int width = (getWidth() / bars.size()) - 100;
		int x = 1;
		for (Color color : bars.keySet())
		{
			int value = bars.get(color);
			int height = (int)
					((getHeight()-5) * ((double)value / max));
			g.setColor(color);
			g.fillRect(x, getHeight() - height, width, height);
			g.setColor(Color.black);
			g.drawRect(x, getHeight() - height, width, height);
			x += (width + 2);
		}
	}

	@Override
	/**
	 * Override the preferred dimensions
	 */
	public Dimension getPreferredSize() {
		return new Dimension(bars.size() * 10 + 2, 50);
	}
}