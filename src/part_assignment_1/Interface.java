package part_assignment_1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import javax.swing.border.Border;

import part_assignment_2.DiscreetCosineTransformation;
import part_assignment_2.FourierTransformation;

public class Interface extends JFrame{
	
	private String path = null;
	private JLabel image;
	private int width = 800;
	private int height = 600;
	
	public int[] pixels;
	public int imageWidth;
	public int imageHeight;
	public int minL;
	public int maxL;
	
	private ArrayList<int[]> previous = new ArrayList<int[]>();
	private ArrayList<int[]> next = new ArrayList<int[]>();
	
	public Interface(){
		super("Digital Image Processing");
		setSize(width, height);
		
		image = new JLabel();
		JScrollPane pane = new JScrollPane(image);
		getContentPane().add(pane, BorderLayout.CENTER);
		
		JButton previous = new JButton("Previous");
		JButton next = new JButton("Next");
		JPanel panel = new JPanel();
		panel.add(previous);
		panel.add(next);
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		previous.addActionListener(new PreviousActionListener());
		next.addActionListener(new NextActionListener());
		
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		JMenu menu_file = new JMenu("File");
		bar.add(menu_file);
		JMenu menu_operations = new JMenu("Intensity Operations");
		bar.add(menu_operations);
		
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new OpenImageActionListener());
		menu_file.add(open);
		
		JMenuItem toGrey = new JMenuItem("Intensity Picture");
		toGrey.addActionListener(new ToGreyActionListener());
		menu_operations.add(toGrey);
		
		JMenuItem balance = new JMenuItem("Intensity Balance");
		balance.addActionListener(new BalanceActionListener());
		menu_operations.add(balance);
		
		JMenuItem transformation = new JMenuItem("Transformation");
		transformation.setActionCommand("transformation");
		transformation.addActionListener(new TransformationActionListener());
		menu_operations.add(transformation);
		
		JMenuItem segmentedTransformation =  new JMenuItem("Segmented Transformation(Stretch)");
		segmentedTransformation.setActionCommand("segmentedTransformation");
		segmentedTransformation.addActionListener(new TransformationActionListener());
		menu_operations.add(segmentedTransformation);
		
		JMenu menu_fourier = new JMenu("Fourier Transformation");
		bar.add(menu_fourier);
		
		JMenuItem fastFourierTransformation = new JMenuItem("Fast Fourier Transformation");
		fastFourierTransformation.addActionListener(new FourierTransformation(this));
		menu_fourier.add(fastFourierTransformation);
		
		JMenuItem  discreetCosineTransformation = new JMenuItem("Discreet Cosine Transformation");
		discreetCosineTransformation.addActionListener(new DiscreetCosineTransformation(this));
		menu_fourier.add(discreetCosineTransformation);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	
	private class OpenImageActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			BufferedImage buffer = null;
			path = getImagePath();
			if (path == null)
				return;
			try {
				buffer = ImageIO.read(new File(path));
				//一开始就直接转换成灰度图像的方式
				ColorConvertOp cco=new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null);
		        cco.filter(buffer,buffer);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog((Component)e.getSource(), "Error loading image");
				e1.printStackTrace();
			}
			
			imageWidth = buffer.getWidth();
			imageHeight = buffer.getHeight();
			pixels = new int[imageWidth*imageHeight];
			PixelGrabber grabber = new PixelGrabber(buffer, 0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);
			try {
				if (grabber.grabPixels() == false){
					JOptionPane.showMessageDialog(null, "Image processing failure!");
					return;
				}
			} catch (HeadlessException | InterruptedException e1) {
				e1.printStackTrace();
			}
			
			image.setIcon(new ImageIcon(buffer));
		}
	}
	
	private class ToGreyActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (pixels == null)
				return;
			ColorModel model = ColorModel.getRGBdefault();
			
			saveHistory();
			
			for (int j = 0 ; j < imageHeight; j ++ ){
				for (int i =  0 ; i < imageWidth ; i ++){
					int R = model.getRed(pixels[i + j*imageWidth]);
					int G = model.getGreen(pixels[i + j*imageWidth]);
					int B = model.getBlue(pixels[i + j*imageWidth]);
					
					int y = (int)(0.3 * R + 0.59 * G + 0.11 * B);
					int u = R - y;
					int v = B - y;
					
					Color c = new Color(y,y,y);
					pixels[i + j*imageWidth] = c.getRGB();
				}
			}
			overrideImage();
		}
		
	}
	
	private class BalanceActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (pixels == null)
				return;
			int[] histogram = new int[256];
			findMinMaxL();
			JOptionPane.showMessageDialog(null, "MinL: " + minL + ", MaxL: " + maxL);
			double constant = (double)(maxL-minL)/(imageHeight*imageWidth);
			ColorModel model = ColorModel.getRGBdefault();
			
			saveHistory();
			
			for (int j = 0 ; j < imageHeight; j ++ )
				for (int i =  0 ; i < imageWidth ; i ++)
					histogram[model.getRed(pixels[i + imageWidth*j])]++;
			
			for (int j = 0 ; j < imageHeight; j ++ )
				for (int i =  0 ; i < imageWidth ; i ++){
					int grey =model.getRed(pixels[i + imageWidth*j]);
					int new_grey = 0;
					for (int k = 0 ; k <= grey ; k++)
						new_grey += histogram[k];
					new_grey *= constant;
					
					Color c = new Color(new_grey,new_grey,new_grey);
					pixels[i + j*imageWidth] = c.getRGB();
				}
			
			overrideImage();
			
		}
		
	}
	
	private class TransformationActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (pixels == null)
				return;
			
			saveHistory();
			
			findMinMaxL();
			
			if (e.getActionCommand().equals("transformation")){
				int newMin, newMax;
				ValueInputDialog dlg = new ValueInputDialog(minL, maxL, false);
				if (!dlg.status)
					return;
				newMin = dlg.c;	
				newMax = dlg.d;
				
				for (int j = 0 ; j < imageHeight; j ++ )
					for (int i =  0 ; i < imageWidth ; i ++){
						int grey = pixels[i + imageWidth*j]&0xff;
						int tmp = (int) ((double)(newMax - newMin)/(maxL - minL)*(grey - minL) + newMin);
						if (tmp > 255)
							tmp = 255;
						if (tmp < 0)
							tmp = 0;
						Color c = new Color(tmp,tmp,tmp);
						pixels[i + j*imageWidth] = c.getRGB();
					}
			}
			else{
				int a,b,c,d;
				ValueInputDialog dlg = new ValueInputDialog(minL, maxL, true);
				if (!dlg.status)
					return;
				a = dlg.a;
				b = dlg.b;
				c = dlg.c;
				d = dlg.d;
				
				System.out.println(a + " " + b + " " + c + " " + d);
				
				for (int j = 0 ; j < imageHeight; j ++ )
					for (int i =  0 ; i < imageWidth ; i ++){
						int grey = pixels[i + imageWidth*j]&0xff;
						int tmp;
						if (grey<a)
							tmp = (int) ((double)c*grey/a);
						else if (grey >= a && grey<= b)
							tmp = (int) ((double)(d - c)/(b - a)*(grey - a) + c);
						else
							tmp = (int) ((double)(255 - b)/(255 - d)*(grey - b) + d);
						tmp = (tmp > 255) ? 255 : tmp;
						tmp = (tmp < 0 ) ? 0 : tmp;
						Color color = new Color(tmp,tmp,tmp);
						pixels[i + j*imageWidth] = color.getRGB();
					}
			}
			overrideImage();
		}
		
		
	}
	
	private class PreviousActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (previous.size() <= 0){
				JOptionPane.showMessageDialog(null, "This is the first one");
				return;
			}
			
			next.add(pixels.clone());
			pixels = previous.get(previous.size() - 1).clone();
			previous.remove(previous.size() - 1);
			
			overrideImage();
		}
	}
	
	private class NextActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (next.size()<=0){
				JOptionPane.showMessageDialog(null, "This is the last one");
				return;
			}
		
			previous.add(pixels.clone());
			pixels = next.get(0).clone();
			next.remove(0);
			
			overrideImage();
		}
	}
	
	public static void main(String[] args){
		new Interface();
	}
	
	public void saveHistory(){
		//save history
		next = new ArrayList<int[]>();
		previous.add(pixels.clone());
	}
	
	public void overrideImage() {
        image.setIcon(new ImageIcon(
        		createImage(new MemoryImageSource(imageWidth, imageHeight, pixels, 0, imageWidth))));
        image.repaint();
	}
	
	private String getImagePath(){
		
		JFileChooser chooser = new JFileChooser("resources/");
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
		
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			
			@Override
			public String getDescription() {
				return "*.jpg,*.jpeg,*.png";
			}
			
			@Override
			public boolean accept(File f) {
				String name = f.getName().toLowerCase();
				if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith("png"))
					return true;
				else
					return false;
			}
		});
        chooser.showDialog(this, "Confirm");
        File file=chooser.getSelectedFile(); 
        String name = file.getName();
        if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith("png"))){
        	JOptionPane.showMessageDialog(null, "Can't read unspecified image file");
        	return null;
        }
        return file.getPath();
		
	}
	
	//assumption: has been "greyed"
	private void findMinMaxL(){
		minL = 255; 
		maxL = 0;
		for (int j = 0 ; j < imageHeight; j ++ )
			for (int i =  0 ; i < imageWidth ; i ++){
				int grey = pixels[i + imageWidth*j]&0xff;
				if (grey > maxL)
					maxL = grey;
				if (grey < minL)
					minL = grey;
			}
	}
	
}
