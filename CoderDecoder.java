
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
					f_xy[y][x] = pix;
					//System.out.print(f_xy[y][x] +  " ");
					img.setRGB(x,y,pix);
					ind++;
				}
				//System.out.println();
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void dct_decode(int[][] f_uv, int[][] f_xy, int quant){
		//dequantize
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				f_uv[i][j] = (int)(f_uv[i][j]* Math.pow(2, quant));
			}
		}
		//initialize C[u][v] matrix
		double[][] c_uv = new double[height][width];
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(i == 0 || j == 0 ){
					c_uv[i][j] = 1.0/Math.sqrt(2);
				}else{
					c_uv[i][j] = 1.0;
				}
			}
		}
		//do inverse function
		int starti = 0;
		while(starti != height){
			int startj =0;
			while(startj != width){
				for(int x = starti; x < starti + 8; x++){
					for(int y = startj; y < startj + 8; y++){
						f_xy[x][y] = (int)Math.round((1.0/4.0)*inverse_cosine(starti, startj, x,y, f_xy, f_uv, c_uv));
					}
				}
				startj += 8;
			}
			starti += 8;
		}
	}
	public double inverse_cosine(int row, int col,int x, int y, int[][]f_xy, int[][] f_uv, double[][] c_uv){
		double sum = 0.0;
		for(int u = row; u < row + 8; u++){
			for(int v = col; v < col + 8; v++){
				sum += c_uv[u][v]*f_uv[u][v]*Math.cos(((2*x + 1)*u*Math.PI)/16.0)*Math.cos(((2*y +1)*v*Math.PI)/16.0);
			}
		}
		return sum;
	}
	public void dct_encode(int[][] f_xy, int[][] f_uv, int quant){
		int q = (int)Math.pow(2, quant);
		//initialize C[u][v] matrix
		double[][] c_uv = new double[height][width];
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				if(i == 0 || j == 0 ){
					c_uv[i][j] = 1.0/Math.sqrt(2);
				}else{
					c_uv[i][j] = 1.0;
				}
			}
		}
		//break into 8x8 
		int starti = 0;
		while(starti != height){ 
			int startj = 0;
			while(startj != width){
				//and feed it to cosine function block by block
				for(int u = starti; u < starti + 8; u++){

						for(int v = startj; v < startj + 8; v++){
							//quantize here as well
							f_uv[u][v] = (int)Math.round(((1.0/4.0)*c_uv[u][v]*cosine(starti, startj, u, v, f_xy, f_uv))/q);
							//System.out.print(f_uv[u][v] +  " ");
						}

				}
				//System.out.println();
				startj += 8;
			}
			starti += 8;
		}
		
	}
	public double cosine(int row, int col,int u, int v, int[][]f_xy, int[][] f_uv){
		double sumCos = 0.0;
		for(int x = row; x < row + 8; x++){
			for(int y = col; y < col + 8; y++){
				sumCos += f_xy[x][y] * Math.cos( ((2*x + 1)*u*Math.PI)/16.0) * Math.cos(((2*y +1)*v*Math.PI)/16.0);
			}
		}
		return sumCos;
	}
	public void display(BufferedImage img, BufferedImage imgMod, int[][] f_xy, int delMode, int latency){
		
	}

	public static void main(String[] args) {
		String imgName = args[0];
		int quant = Integer.parseInt(args[1]);
		int delMode = Integer.parseInt(args[2]);
		int latency = Integer.parseInt(args[3]);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage imgMod = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		//Encoder
		// Create and store original Image
		// 8x8 and dct Input m x n pixel grid => m x n in frequency form
		CoderDecoder cd = new CoderDecoder();
		int[][] f_xy = new int[height][width];
		cd.readOriginal(imgName, img, f_xy);
		int[][] f_uv = new int[height][width];
		// Quantize DC and AC based on quantization table 
		cd.dct_encode(f_xy,f_uv, quant);
		//Decoder
		// Dequantize DC and AC's based on uniform quantization table
		// Inverse DCT
		cd.dct_decode(f_uv, f_xy, quant);
		// display image based on M parameter
		cd.display(img, imgMod, f_xy, delMode, latency);
		

		
		
	}

}