
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
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 


					//System.out.println(r);
					//Save to f(x,y) 2D matrix range -127 to 128 for DCT
					f_xyR[y][x] = (r);
					f_xyG[y][x] = (g);
					f_xyB[y][x] = (b);

					// if( 0 <= x && x <= 7 && 0 <= y && y <= 7){
					// 	System.out.print(f_xyR[y][x] + " ");
					// }
					//System.out.println("byte " + (r & 0xff) + " " + (g & 0xff) + " " + (b & 0xff));

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);


					//System.out.print(f_xy[y][x] +  " ");
					img.setRGB(x,y,pix);
					ind++;
				}
				// if(0 <= y && y <= 7){
				// 	System.out.println();
				// }
				
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void dct_decode(int[][] f_uvR, int[][] f_uvG, int[][] f_uvB, int[][] f_xyR, int[][] f_xyG, int[][] f_xyB, int quant){
		//dequantize
 		int xi = 0;
 		int yi = 0;
		//do inverse function
		int starti = 0;
		while(starti != height){
			int startj =0;
			while(startj != width){
				for(int x = starti; x < starti + 8; x++){
					for(int y = startj; y < startj + 8; y++){
						f_xyR[x][y] = (int)Math.round((0.25)*inverse_cosine(starti, startj, xi,yi,f_uvR));
						f_xyG[x][y] = (int)Math.round((0.25)*inverse_cosine(starti, startj, xi,yi,f_uvG));
						f_xyB[x][y] = (int)Math.round((0.25)*inverse_cosine(starti, startj, xi,yi,f_uvB));
						yi++;
					}
					yi = 0;
					xi++;
				}
				xi = 0;
				startj += 8;
			}
			starti += 8;
		}
	}
	public double inverse_cosine(int row, int col,int x, int y, int[][] f_uv){
		double sum = 0.0;
		double cu, cv;
		int ui = -1;
		int vi = -1;
		for(int u = row; u < row + 8; u++){
			ui++;
			for(int v = col; v < col + 8; v++){
				vi++;
					if (u == 0){
            			cu = 1.0/ Math.sqrt(2);
            		}
        			else{
            			cu = 1.0;
             		}
        			if (v == 0){
        				//System.out.println("v:  " + u + " " + v);
            			cv = 1.0/ Math.sqrt(2);
            		}
        			else{
            			cv = 1.0;
            		}
				sum =  sum + (cu*cv*f_uv[u][v]*Math.cos( ((2*x + 1)*ui*Math.PI)/16) * Math.cos(((2*y +1)*vi*Math.PI)/16));
			}
		}
		return sum;
	}
	public void dct_encode(int[][] f_xyR, int[][] f_xyG, int[][] f_xyB, int[][] f_uvR, int[][] f_uvG, int[][] f_uvB, int quant){
		int q = (int)Math.pow(2, quant);

		//break into 8x8 
		double cu,cv;
		int ui = 0;
		int vi = 0;
		int starti = 0;
		//boolean i0 = true;
		while(starti != height){ 
			int startj = 0;
			//boolean j0 = true;
			while(startj != width){
				//and feed it to cosine function block by block
				for(int u = starti; u < starti + 8; u++){
					
						for(int v = startj; v < startj + 8; v++){

							//System.out.print( u + "," + v + "  ");
							if (u == 0){
                    			cu = 1.0/ Math.sqrt(2);
                    		}
                			else{
                    			cu = 1.0;
                     		}
                			if (v == 0){
                				//System.out.println("v:  " + u + " " + v);
                    			cv = 1.0/ Math.sqrt(2);
                    		}
                			else{
                    			cv = 1.0;
                    		}
							//quantize here as well?
							f_uvR[u][v] = (int)Math.round((0.25)*cu*cv*cosine(starti, startj, ui, vi, f_xyR));
							f_uvG[u][v] = (int)Math.round((0.25)*cu*cv*cosine(starti, startj, ui, vi, f_xyG));
							f_uvB[u][v] = (int)Math.round((0.25)*cu*cv*cosine(starti, startj, ui, vi, f_xyB));
							vi++;
							//System.out.print(f_uvR[u][v] +  " ");
						    //j0 = false;
						}
						vi = 0;
						ui++;
					//System.out.println();
					//i0 = false;
				}
				ui = 0;
				//System.out.println("End of block");
				//System.out.println();
				startj += 8;
				
			}
			starti += 8;
			
		}
		
	}
	public double cosine(int row, int col,int u, int v, int[][] f_xy){
		double sumCos = 0.0;
		int xi = -1;
		int yi = -1;
		for(int x = row; x < row + 8; x++){
			xi++;
			for(int y = col; y < col + 8; y++){
				yi++;
				sumCos =  sumCos + (f_xy[x][y] * Math.cos( ((2*xi + 1)*u*Math.PI)/16) * Math.cos(((2*yi +1)*v*Math.PI)/16));
			}
		}
		return sumCos;
	}
	public void display(BufferedImage img, BufferedImage imgMod, int[][] f_xyR, int[][] f_xyG, int[][] f_xyB, int delMode, int latency){
		int ind = 0;
		for(int y = 0; y < height; y++){

			for(int x = 0; x < width; x++){

				int r = (f_xyR[y][x]);
				// if(r < -127){
				// 	r = -127;
				// }
				// else if(r > 128){
				// 	r = 128;
				// }
				int g = (f_xyG[y][x]);
				// if(g < -127){
				// 	g = -127;
				// }
				// else if(g > 128){
				// 	g = 128;
				// }
				int b = (f_xyB[y][x]);
				// if(b < -127){
				// 	b= -127;
				// }
				// else if(b > 128){
				// 	b = 128;
				// }
				
				System.out.println(r + " " + g + " " + b);
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				imgMod.setRGB(x,y, pix);
				ind++;
			}
		}
		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(imgMod));

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

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

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