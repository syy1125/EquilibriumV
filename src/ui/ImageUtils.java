package ui;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtils
{
	/**
	 * Loads an image from the assets folder.
	 */
	public static ImageIcon loadImage(String fileName)
	{
		try
		{
			return new ImageIcon(ImageIO.read(ImageUtils.class.getResourceAsStream(Config.ASSETS_FOLDER + fileName)));
		}
		catch (IOException e)
		{
			System.err.println("Failed to load image: " + fileName);
			e.printStackTrace();
			return null;
		}
	}
}
