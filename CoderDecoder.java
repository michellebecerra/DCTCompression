
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
	public static void readOriginal(String fileName, BufferedImage img, int[][] f_xyR, int[][] f_xyG, int[][] f_xyB){

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
					//byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					f_xyR[y][x] = (r & 0xff);
					f_xyG[y][x] = (g & 0xff);
					f_xyB[y][x] = (b & 0xff);

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
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
	public void dct_decode(int[][] f_uvR, int[][] f_uvG, int[][] f_uvB, int[][] f_xyR, int[][] f_xyG, int[][] f_xyB, int quant){
		//dequantize
 		for(int i = 0; i < height; i++){
 			for(int j = 0; j < width; j++){
 				f_uvR[i][j] = f_uvR[i][j]* (int)Math.pow(2,quant);
 				f_uvG[i][j] = f_uvG[i][j]* (int)Math.pow(2,quant);
 				f_uvB[i][j] = f_uvB[i][j]* (int)Math.pow(2,quant);
 			}
 		}

 		int xi = 0;
 		int yi = 0;
		//do inverse function
		int starti = 0;
		while(starti != height){
			int startj =0;
			while(startj != width){
				xi = 0;
				for(int x = starti; x < starti + 8; x++){
					yi = 0;
					for(int y = startj; y < startj + 8; y++){
						f_xyR[x][y] = (int)Math.round((0.25)*inverse_cosine(starti, startj, xi,yi,f_uvR));
						f_xyG[x][y] = (int)Math.round((0.25)*inverse_cosine(starti, startj, xi,yi,f_uvG));
						f_xyB[x][y] = (int)Math.round((0.25)*inverse_cosine(starti, startj, xi,yi,f_uvB));
						yi++;
					}
					xi++;
				}
				startj += 8;
			}
			starti += 8;
		}
	}
	public double inverse_cosine(int row, int col,int x, int y, int[][] f_uv){
		
		int ui = 0;
		int vi = 0;

		double sum = 0.0;
		double cu, cv;
		for(int u = row; u < row + 8; u++){
			vi = 0;
			for(int v = col; v < col + 8; v++){
					if (ui == 0){
            			cu = 1.0/ Math.sqrt(2);
            		}
        			else{
            			cu = 1.0;
             		}
        			if (vi == 0){
            			cv = 1.0/ Math.sqrt(2);
            		}
        			else{
            			cv = 1.0;
            		}
				sum =  sum + (cu*cv*f_uv[u][v]*Math.cos( ((2*x + 1)*ui*Math.PI)/16) * Math.cos(((2*y +1)*vi*Math.PI)/16));
				vi++;
			}
			ui++;
		}
		return sum;
	}
	public void dct_encode(int[][] f_xyR, int[][] f_xyG, int[][] f_xyB, int[][] f_uvR, int[][] f_uvG, int[][] f_uvB, int quant){
		int q = (int)Math.pow(2, quant);

		int ui = 0;
		int vi = 0;
		//break into 8x8 
		double cu,cv;
		int starti = 0;
		while(starti != height){ 
			int startj = 0;
			while(startj != width){
				ui = 0;
				//and feed it to cosine function block by block
				for(int u = starti; u < starti + 8; u++){
						vi = 0;
						for(int v = startj; v < startj + 8; v++){

							//System.out.print( u + "," + v + "  ");
							if (ui == 0){
                    			cu = 1.0/ Math.sqrt(2);
                    		}
                			else{
                    			cu = 1.0;
                     		}
                			if (vi == 0){
                				//System.out.println("v:  " + u + " " + v);
                    			cv = 1.0/ Math.sqrt(2);
                    		}
                			else{
                    			cv = 1.0;
                    		}
							//quantize here
							f_uvR[u][v] = (int)Math.round( ((0.25)*cu*cv*cosine(starti, startj, ui, vi, f_xyR))/q);
							f_uvG[u][v] = (int)Math.round( ((0.25)*cu*cv*cosine(starti, startj, ui, vi, f_xyG))/q);
							f_uvB[u][v] = (int)Math.round( ((0.25)*cu*cv*cosine(starti, startj, ui, vi, f_xyB))/q);
							vi++;
						}
					
					ui++;
				}
				//System.out.println("End of block");
				startj += 8;
			}
			starti += 8;
			
		}
		
	}
	public double cosine(int row, int col,int u, int v, int[][] f_xy){
		int xi = 0;
		int yi = 0;

		double sumCos = 0.0;
		for(int x = row; x < row + 8; x++){
			yi = 0;
			for(int y = col; y < col + 8; y++){
				
				sumCos =  sumCos + (f_xy[x][y] * Math.cos( ((2*xi + 1)*u*Math.PI)/16) * Math.cos(((2*yi +1)*v*Math.PI)/16));
				yi++;
			}
			xi++;
		}
		return sumCos;
	}
	public void clampValues(int[][] f_xyR, int[][] f_xyG, int[][] f_xyB){
		for(int y = 0; y < height; y++){

			for(int x = 0; x < width; x++){

				//Clamp values < 0 and > 255
				int r = (f_xyR[y][x]);
				if(r < 0){
					f_xyR[y][x] = 0;
				}
				else if(r > 255){
					f_xyR[y][x] = 255;
				}
				int g = (f_xyG[y][x]);
				if(g < 0){
					f_xyG[y][x] = 0;
				}
				else if(g > 255){
					f_xyG[y][x] = 255;
				}
				int b = (f_xyB[y][x]);
				if(b < 0){
					f_xyB[y][x] = 0;
				}
				else if(b > 255){
					f_xyB[y][x] = 255;
				}
			}
		}
	}
	public void display(BufferedImage img, BufferedImage imgMod, int[][] f_xyR, int[][] f_xyG, int[][] f_xyB, int delMode, int latency){
		//clamp values less than 0 and greater than 255
		clampValues(f_xyR, f_xyG, f_xyB);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);



		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		lbIm1 = new JLabel(new ImageIcon(img));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		//Baseline set all to black first
		for(int y = 0; y < height; y++){
			for(int x = 8; x < width; x++){
				int r = 0;
				int g = 0;
				int b = 0;
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				imgMod.setRGB(x,y, pix);
			}
		}

		int starti = 0;
		while(starti != height){ 
			int startj = 0;
			while(startj != width){
				for(int x = starti; x < starti + 8; x++){
					for(int y = startj; y < startj + 8; y++){
						int r = f_xyR[x][y];
						int g = f_xyG[x][y];
						int b = f_xyB[x][y];

				//System.out.println(r + " " + g + " " + b);
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						imgMod.setRGB(y,x, pix);
						//ind++;
					}
				}
				//create new lib
				lbIm2 = new JLabel(new ImageIcon(imgMod));
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 1;
				c.gridy = 1;
				frame.getContentPane().add(lbIm2, c);
				frame.pack();
				frame.setVisible(true);
				//sleep
				try{
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startj += 8;
			}
			starti += 8;
		}
		frame.pack();
		frame.setVisible(true);
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
		int[][] f_xyR = new int[height][width];
		int[][] f_xyG = new int[height][width];
		int[][] f_xyB = new int[height][width];

		cd.readOriginal(imgName, img, f_xyR, f_xyG, f_xyB);
		int[][] f_uvR = new int[height][width];
		int[][] f_uvG = new int[height][width];
		int[][] f_uvB = new int[height][width];
		// Quantize DC and AC based on quantization table 
		cd.dct_encode(f_xyR,f_xyG, f_xyB, f_uvR,f_uvG,f_uvB, quant);
		//Decoder
		// Dequantize DC and AC's based on uniform quantization table
		// Inverse DCT
		cd.dct_decode(f_uvR,f_uvG,f_uvB, f_xyR,f_xyG, f_xyB, quant);
		// display image based on M parameter
		cd.display(img, imgMod, f_xyR, f_xyG, f_xyB, delMode, latency);
		

		
		
	}

}