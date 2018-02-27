import java.io.*;

public class Test{
	static int height = 8;
	static int width = 8;
	public static void main(String[] args){
// 		double[][] test = new double[][]{{3.0, 34.0, 212.0, 250.0, 232.0, 232.0, 237.0, 237.0},
// {3.0, 33.0, 212.0, 250.0, 232.0, 232.0, 237.0, 237.0},
// {9.0, 36.0, 181.0, 213.0, 69.0, 31.0, 92.0, 88.0}, 
// {9.0, 34.0, 185.0, 213.0, 18.0, 73.0, 207.0, 207.0},
// {6.0, 30.0, 201.0, 228.0, 30.0, 38.0, 47.0, 89.0},
// {6.0, 36.0, 187.0, 229.0, 152.0, 79.0, 47.0, 42.0}, 
// {3.0, 31.0, 202.0, 240.0, 181.0, 181.0, 143.0, 28.0},
// {3.0, 22.0, 209.0, 240.0, 59.0, 62.0, 51.0, 28.0}};
		// int[][] test = new int[][]{{178, 187, 183, 175, 178, 177, 150, 183},
		// {191, 174,171, 182,176, 171, 170, 188},
		// {199, 153, 128, 177, 171, 167, 173, 183},
		// {95,  178, 158, 167, 167, 165, 166, 177},
		// {190, 186, 158, 155, 159, 164, 158, 178},
		// {194, 184, 137, 148, 157, 158, 150, 173},
		// {200, 194, 148, 151, 161, 155, 148, 167},
		// {200, 195, 172, 159, 159, 152, 156, 154}};

		int[][] test = new int[][]{
		{139, 144, 149, 153, 155, 155, 155, 155},
		{144, 151, 153, 156, 159, 156, 156, 156},
		{150, 155, 160, 163, 158, 156, 156, 156},
		{159, 161, 162, 160, 160, 159, 159, 159},
		{159, 160, 161, 162, 162, 155, 155, 155},
		{161, 161, 161, 161, 160, 157, 157, 157},
		{162, 162, 161, 163, 162, 157, 157, 157},
		{162, 162, 161, 161, 163, 158, 158, 158}};

		int[][] f_uvR = new int[height][width];
	    dct(test, f_uvR);
	    dct_decode(f_uvR, test, 0);

	}
		
	public static void dct(int[][] f_xyR, int[][] f_uvR){
		//break into 8x8 and feed it to cosine function
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				f_xyR[i][j] = f_xyR[i][j] - 128;
			}
		}
		double cu,cv;
		int starti = 0;
		
		while(starti != height){
			int startj = 0;
			while(startj != width){

				for(int u = starti; u < starti + 8; u++){
					
						for(int v = startj; v < startj + 8; v++){

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
							
							f_uvR[u][v] = (int)Math.round((0.25)*cu*cv*cosine(starti, startj, u, v, f_xyR));
							//f_uv[u][v] = (1.0/4.0)*cosine(starti, startj, u, v, f_xy, f_uv)
							System.out.print( f_uvR[u][v] + " ");
						}
						System.out.println();

				}
				System.out.println("End of block ");
				startj += 8;
			}
			starti += 8;
		}
	}

	public static double cosine(int row, int col,int u, int v, int[][] f_xy){
		double sumCos = 0.0;
		for(int x = row; x < row + 8; x++){
			for(int y = col; y < col + 8; y++){
				sumCos =  sumCos + (f_xy[x][y] * Math.cos( ((2*x + 1)*u*Math.PI)/16) * Math.cos(((2*y +1)*v*Math.PI)/16));
			}
		}
		return sumCos;
	}

	public static void dct_decode(int[][] f_uvR,int[][] f_xyR, int quant){
		//dequantize
 
		//do inverse function
		int starti = 0;
		while(starti != height){
			int startj =0;
			while(startj != width){
				for(int x = starti; x < starti + 8; x++){
					for(int y = startj; y < startj + 8; y++){
						f_xyR[x][y] = (int)Math.round((0.25)*inverse_cosine(starti, startj, x,y,f_uvR));
						System.out.print( ((f_xyR[x][y] + 128)) + " ");
					}
					System.out.println();
				}
				startj += 8;
			}
			starti += 8;
		}
	}
	public static double inverse_cosine(int row, int col,int x, int y, int[][] f_uv){
		double sum = 0.0;
		double cu, cv;
		for(int u = row; u < row + 8; u++){
			for(int v = col; v < col + 8; v++){
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
				sum =  sum + (cu*cv*f_uv[u][v]*Math.cos( ((2*x + 1)*u*Math.PI)/16) * Math.cos(((2*y +1)*v*Math.PI)/16));
			}
		}
		return sum;
	}

	// public static void zigzag(double[][] mat){
	// 	while(j != 0){
	// 		i++;
	// 		j--;

	// 	}
	// }	
}
