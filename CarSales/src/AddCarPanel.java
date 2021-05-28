import java.util.*;

import java.awt.*;

import java.awt.event.*;

import javax.swing.*;

public class AddCarPanel extends JPanel implements ActionListener
{
	
	private CarSalesSystem carSystem;
	private JLabel headingLabel = new JLabel("Добави кола");
	private JButton resetButton = new JButton("Изчисти");
	private JButton saveButton = new JButton("Добави");
	private JPanel buttonPanel = new JPanel();
	private CarDetailsComponents carComponents = new CarDetailsComponents();

	public AddCarPanel(CarSalesSystem carSys)
	{
		carSystem = carSys;

		resetButton.addActionListener(this);
		saveButton.addActionListener(this);
		headingLabel.setAlignmentX(0.5f);

		buttonPanel.add(resetButton);
		buttonPanel.add(saveButton);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(Box.createVerticalStrut(10));
		add(headingLabel);
		add(Box.createVerticalStrut(15));
		carComponents.add(buttonPanel, "Center");
		add(carComponents);
	}


	public void actionPerformed(ActionEvent ev)
	{
		if (ev.getSource() == resetButton)
			resetButtonClicked();
		else if (ev.getSource() == saveButton)
			saveButtonClicked();
	}

	private void resetButtonClicked()
	{
		carComponents.clearTextFields();
	}

	private void saveButtonClicked()
	{
		String manufacturer = "";
		String model = "";
		String info = "";
		double kilometers = 0;
		int price = 0;
		int year = 0;
		boolean valid = false;
		try
		{
			manufacturer = carComponents.getManufacturerText().trim();
			model = carComponents.getModelText().trim();
			info = carComponents.getInfoText().trim();
			kilometers = Double.parseDouble(carComponents.getKmText().trim());
			price = Integer.parseInt(carComponents.getPriceText().trim());
			year = Integer.parseInt(carComponents.getYearText().trim());

			
			if (validateString(manufacturer))
			{
				if (year >= 1000 && year <= 9999)
				{
					if (validateString(model))
					{
						if (validateKilometers(carComponents.getKmText().trim()))
						{
							valid = true;
						}
						else
							JOptionPane.showMessageDialog(carSystem, "Грешка при \"Изминати километри\" текстово поле.", "Невалиден запис", JOptionPane.ERROR_MESSAGE);
					}
					else
						JOptionPane.showMessageDialog(carSystem, "Грешка при \"Модел\" текстово поле.", "Невалиден запис", JOptionPane.ERROR_MESSAGE);
				}
				else
					JOptionPane.showMessageDialog(carSystem, "Грешка при \"Година\" текстово поле.", "Невалиден запис", JOptionPane.ERROR_MESSAGE);
			}
			else
				JOptionPane.showMessageDialog(carSystem, "Грешка при \"Марка\" текстово поле.", "Невалиден запис", JOptionPane.ERROR_MESSAGE);

		}
		catch (NumberFormatException exp)
		{
			JOptionPane.showMessageDialog(carSystem, "Грешка.:\n" +
			"\"Година\" Трябва да съдържа само цифри\n \"Цена\" полето трябва да съдържа валидно цяло число.\n \"Изминато Разстояние\" полето трябва да съдържа число, което може да има максимум един десетичен знак", "Невалиден зпаис", JOptionPane.ERROR_MESSAGE);
		}

		if (valid)
		{
			
			Car myCar = new Car(manufacturer, model, info);
			myCar.setKilometers(kilometers);
			myCar.setPrice(price);
			myCar.setYear(year);

			
			int result = carSystem.addNewCar(myCar);

			if (result == CarsCollection.NO_ERROR)
			{
				carSystem.setCarsUpdated();
				JOptionPane.showMessageDialog(carSystem, "Записът е добавен.", "Потвърждение", JOptionPane.INFORMATION_MESSAGE);
				resetButtonClicked();
				carComponents.setFocusManufacturerTextField();
			}
			else if (result == CarsCollection.CARS_MAXIMUM_REACHED)
				JOptionPane.showMessageDialog(carSystem, "Максималният лимит е достигнат.\nНе можете да добавяте коли за тази марка", "Грешка", JOptionPane.WARNING_MESSAGE);
			else if (result == CarsCollection.MANUFACTURERS_MAXIMUM_REACHED)
				JOptionPane.showMessageDialog(carSystem, "Максималният допустим лимит за добавяне на коли е достигнат.\nНе можете да добавяте повече коли", "Грешка", JOptionPane.WARNING_MESSAGE);
		}
	}

	private boolean validateString(String arg)
	{
		boolean valid = false;
		String[] splitted = arg.split(" "); 

		for (int i = 0; i < splitted.length; i++)
		{
			valid = (splitted[i].length() > 2);
			if (valid)
				break;
		}

		return valid;
	}

	private boolean validateKilometers(String distance)
	{
		boolean valid = false;
		String rem;
		StringTokenizer tokens = new StringTokenizer(distance, ".");

		tokens.nextToken();

		if (tokens.hasMoreTokens())
		{
			
			rem = tokens.nextToken();
			
			if (rem.length() == 1)
				valid = true;
			else
			{
				if ((Integer.parseInt(rem)) % (Math.pow(10, rem.length() - 1)) == 0)
					valid = true;
				else
					valid=false;
			}
		}
		else 
			valid = true;

		return valid;
	}
}