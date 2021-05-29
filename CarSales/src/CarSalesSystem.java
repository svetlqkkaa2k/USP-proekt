//Това е заглавната страница, като се отвори програмата.
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class CarSalesSystem extends JFrame implements ActionListener, ComponentListener
{
	
	public static final int CARS_COUNT = 0;
	public static final int MANUFACTURERS_COUNT = 1;
	public static final int AVERAGE_PRICE = 2;
	public static final int AVERAGE_DISTANCE = 3;
	public static final int AVERAGE_AGE = 4;
	public static final int NAME_COUNT = 5;
	private String file;
	private boolean carsUpdated = false;
	private Vector registeredListeners = new Vector();
	private CarsCollection carCollection;
	private JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));

	private JLabel carCoLabel = new JLabel("Система за продажба на коли", JLabel.CENTER);
	private JLabel pictureLabel = new JLabel();
	private JTabbedPane theTab = new JTabbedPane(JTabbedPane.TOP);
	private JMenuBar menuBar = new JMenuBar();
	private WindowCloser closer = new WindowCloser();
	
	public CarSalesSystem(String f)
	{
		
		super("Система за продажба на коли");

		addWindowListener(closer);
		addComponentListener(this);
		setSize(900, 800);
		
		Container c = getContentPane();
		
		
		carCollection = new CarsCollection();
		file = f;

		try
		{
			carCollection.loadCars(file);
		}
		catch (java.io.FileNotFoundException exp)
		{
			System.out.println("Файлът, 'cars.dat' не съществува. Моля създайте файл с наименование 'cars.dat'");
			System.exit(0);
			
		}
		catch (java.io.EOFException exp){}
		catch (java.io.IOException exp)
		{
			System.out.println("Файлът, 'cars.dat' е повреден. Моля изтрийте го и направете нов файл cars.dat");
			System.exit(0);
		}
		catch (Exception exp)
		{
			System.out.println("Грешка при зареждане на файла 'cars.dat'. Моля изтрийте го и направете нов файл 'cars.dat'");
			System.exit(0);
		}

	
		pictureLabel.setIcon(new ImageIcon("lambo.jpg"));
		topPanel.add(pictureLabel, "West");
		setJMenuBar(menuBar);

		WelcomePanel welcomePanel = new WelcomePanel(this, f);
		AddCarPanel addCarPanel = new AddCarPanel(this);
		ShowAllCarsPanel showAllCarsPanel = new ShowAllCarsPanel(this);
		SearchByAgePanel searchByAgePanel = new SearchByAgePanel(this);
		SearchByOtherPanel searchByOtherPanel = new SearchByOtherPanel(this);

		theTab.add("Начало", welcomePanel);
		theTab.add("Добави кола", addCarPanel);
		theTab.add("Покажи всички марки и модели", showAllCarsPanel);
		theTab.add("Търсене по година", searchByAgePanel);
		theTab.add("Търсене по цена и изминато разстояние", searchByOtherPanel);
		theTab.setBackground(Color.WHITE);
		theTab.addChangeListener(showAllCarsPanel);
		theTab.addChangeListener(welcomePanel);

		theTab.setSelectedIndex(0);
		
		c.setLayout(new BorderLayout());
		c.add(topPanel, "North");
		c.add(theTab, "Center");
		
		
	}


	public void addCarUpdateListener(Object listener)
	{
		if (!(listener == null))
			registeredListeners.add(listener);
	}

	
	public int addNewCar(Car c)
	{
		return carCollection.addCar(c);
	}

	public void closing()
	{
		boolean ok;

		if (carsUpdated)
		{
			do
			{
				try
				{
					carCollection.saveCars(file);
					ok = true;
				}
				catch (java.io.IOException exp)
				{
					int result = JOptionPane.showConfirmDialog(this, "Данните не могат да бъдат запазени,\nАко изберете Не, ще изгубите всички данни.\n\nИскате ли да запишете данните наново?", "Грешка", JOptionPane.YES_NO_OPTION);

					if (result == JOptionPane.YES_OPTION)
						ok = false;
					else
						ok = true;
				}
			}
			while (!ok);
		}

		System.exit(0);
	}

	public void componentHidden(ComponentEvent ev) {}

	public void componentMoved(ComponentEvent ev) {}

	
	public void componentResized(ComponentEvent ev)
	{
		if (this == ev.getComponent())
		{
			Dimension size = getSize();

			if (size.height < 530)
				size.height = 530;
			if (size.width < 775)
				size.width = 675;

			setSize(size);
		}
	}

	public void componentShown(ComponentEvent ev) {}

	
	public static double[] convertToRange(String s)
	{
		String[] parts = s.trim().split("-");
		double[] bounds = new double[2];

		try
		{
			if (parts.length == 1)
			{
				String c = s.substring(s.length() - 1);

				
				if (c.equals("+"))
				{
					
					bounds[0] = Double.parseDouble(s.substring(0, s.length() - 1));
					
					bounds[1] = -1;
				}
				else
				{
					
					bounds[0] = Double.parseDouble(s);
					bounds[1] = bounds[0];
				}
			}
			
			else if(parts.length == 2)
			{
				bounds[0] = Double.parseDouble(parts[0]);
				bounds[1] = Double.parseDouble(parts[1]);
				if (bounds[0] > bounds[1])
				{
					
					bounds[0] = -1;
					bounds[1] = -1;
				}
			}
		}
		catch (NumberFormatException exp)
		{
			
			bounds[0] = -1;
			bounds[1] = -1;
		}

		return bounds;
	}

	
	public Car[] getAllCars()
	{
		return carCollection.getAllCars();
	}


	
	public boolean getCarsUpdated()
	{
		return carsUpdated;
	}

	
	public double getStatistics(int type)
	{
		double result = 0;

		if (type == CARS_COUNT)
		{
			result	= carCollection.carsCount();
		}
		else if (type == MANUFACTURERS_COUNT)
		{
			result = carCollection.manufacturerCount();
		}
		else if (type == AVERAGE_PRICE)
		{
			result = carCollection.getAveragePrice();
		}
		else if (type == AVERAGE_DISTANCE)
		{
			result = carCollection.getAverageDistance();
		}
		else if (type == AVERAGE_AGE)
		{
			result = carCollection.getAverageAge();
		}

		return result;
	}

	
	public static void main(String[] args)
	{
		
		CarSalesSystem carSales = new CarSalesSystem("cars.dat");
		carSales.setVisible(true);
	}

	
	public Car[] search(int minAge, int maxAge)
	{
		return carCollection.search(minAge, maxAge);
	}

	
	public Car[] search(int minPrice, int maxPrice, double minDistance, double maxDistance)
	{
		return carCollection.search(minPrice, maxPrice,  minDistance, maxDistance);
	}

	
	public void setCarsUpdated()
	{
		
		carsUpdated = true;

		for (int i = 0; i < registeredListeners.size(); i++)
		{
			Class[] paramType = {CarUpdateEvent.class};
			Object[] param = {new CarUpdateEvent(this)};

			try
			{
				
				java.lang.reflect.Method callingMethod = registeredListeners.get(i).getClass().getMethod("carsUpdated", paramType);
				
				callingMethod.invoke(registeredListeners.get(i), param);
			}
			catch (Exception exp){}
		}
	}


	public static Car[] vectorToCar(Vector v)
	{
		Car[] ret = new Car[v.size()];

		for (int i = 0; i < v.size(); i++)
		{
			ret[i] = (Car)v.elementAt(i);
		}

		return ret;
	}

	class WindowCloser extends WindowAdapter
	{
		
		public void windowClosing(WindowEvent ev)
		{
			closing();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}