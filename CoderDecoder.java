
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;



public class CoderDecoder{
	//====== Fixed constants =====//
	static int width = 352;
	static int height = 288;
	
	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;

	/**
	* Converts RGB to YUV and stores the values in double precision. 
	* Processes original image and stores it in img.
	**/
	public static void readOriginal(String fileName, BufferedImage img, int[][] f_xy){

		try {
			File file = new File(fileName);
			InputStream is = new FileInputStream(file);

			long len = file.length();
			byte[] bytes = new byte[(int)len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}


			int ind = 0;
			for(int y = 0; y < height; y++){

				for(int x = 0; x < width; x++){

					//Get bytes per pixel and per each channel
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					//System.out.println("byte " + r + " " + g + " " + b);

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);

					//Save to f(x,y) 2D matrix
					f_xy[y][y] = pix;

					img.setRGB(x,y,pix);
					ind++;
				}
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		String imgName = args[0];
		int quant = Integer.parseInt(args[1]);
		int delMode = Integer.parseInt(args[2]);
		int latency = Integer.parseInt(args[3]);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		//Encoder
		// Create and store original Image
		// 8x8 and dct Input m x n pixel grid => m x n in frequency form
		CoderDecoder cd = new CoderDecoder();
		int[][] f_xy = new int[height][width];
		cd.readOriginal(imgName, img, f_xy);
		//dct(f_xy);

		// Quantize DC and AC based on quantization table 
		//Decoder
		// Dequantize DC and AC's based on uniform quantization table
		// Inverse DCT and display image based on M parameter
		
	}

}