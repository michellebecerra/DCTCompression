import java.io.*;
import java.util.*;

public class Test{
	static int height = 16;
	static int width = 16;
	static FileWriter file;
	static HashMap<Integer, ZigIndex> map = new HashMap<Integer, ZigIndex>();
	static class ZigIndex{
		int x;
		int y;
		public ZigIndex(int x, int y){
			this.x = x;
			this.y = y;
		}

	}

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


		try{
			file = new FileWriter(new File("result.txt"));
		}catch(IOException e){
			System.out.println(e);
		}
		

		int[][] test = new int[][]{
		{139, 145, 149, 153, 155, 155, 155, 155,139, 145, 149, 153, 155, 155, 155, 155},
		{144, 151, 153, 156, 159, 156, 156, 156,144, 151, 153, 156, 159, 156, 156, 156},
		{150, 155, 160, 163, 158, 156, 156, 156,150, 155, 160, 163, 158, 156, 156, 156},
		{159, 161, 162, 160, 160, 159, 159, 159,159, 161, 162, 160, 160, 159, 159, 159},
		{159, 160, 161, 162, 162, 155, 155, 155,159, 160, 161, 162, 162, 155, 155, 155},
		{161, 161, 161, 161, 160, 157, 157, 157,161, 161, 161, 161, 160, 157, 157, 157},
		{162, 162, 161, 163, 162, 157, 157, 157,162, 162, 161, 163, 162, 157, 157, 157},
		{162, 162, 161, 161, 163, 158, 158, 158,162, 162, 161, 161, 163, 158, 158, 158},
		{139, 145, 149, 153, 155, 155, 155, 155,139, 145, 149, 153, 155, 155, 155, 155},
		{144, 151, 153, 156, 159, 156, 156, 156,144, 151, 153, 156, 159, 156, 156, 156},
		{150, 155, 160, 163, 158, 156, 156, 156,150, 155, 160, 163, 158, 156, 156, 156},
		{159, 161, 162, 160, 160, 159, 159, 159,159, 161, 162, 160, 160, 159, 159, 159},
		{159, 160, 161, 162, 162, 155, 155, 155,159, 160, 161, 162, 162, 155, 155, 155},
		{161, 161, 161, 161, 160, 157, 157, 157,161, 161, 161, 161, 160, 157, 157, 157},
		{162, 162, 161, 163, 162, 157, 157, 157,162, 162, 161, 163, 162, 157, 157, 157},
		{162, 162, 161, 161, 163, 158, 158, 158,162, 162, 161, 161, 163, 158, 158, 158}};

	 // int[][] f_uvR = new int[height][width];
	 //    dct(test, f_uvR);
	 //    dct_decode(f_uvR, test, 0);

		int[][] zig_copy = new int[height][width];

		zigzag();
		for(int i = 1; i < 64; i++){
			ZigIndex zi = map.get(i);
			for(int starti = 0; starti < height; starti= starti + 8){
				for(int startj = 0; startj < width; startj = startj + 8){
					zig_copy[starti + zi.x][startj + zi.y] = test[starti + zi.x][startj + zi.y];
					
				}
			}
		
			printArray(zig_copy);
			System.out.println("New AC coefficient");

			//System.out.println("(" + zi.x + " " + zi.y + ")");
		}
	}

	public static void printArray(int[][] arr){
		for(int i = 0; i < arr.length; i++){
			for(int j = 0; j < arr[0].length; j++){
				System.out.print(arr[i][j] + " ");
			}
			System.out.println();
		}
	}
		
	public static void dct(int[][] f_xyR, int[][] f_uvR){
		//break into 8x8 and feed it to cosine function
		// for(int i = 0; i < height; i++){
		// 	for(int j = 0; j < width; j++){
		// 		f_xyR[i][j] = f_xyR[i][j];
		// 	}
		// }

		int ui = 0;
		int vi = 0;

		double cu,cv;
		int starti = 0;
		try{
		while(starti != height){
			int startj = 0;
			while(startj != width){
				ui = 0;
				for(int u = starti; u < starti + 8; u++){
						vi = 0;
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
                    		//System.out.println("D ui: " + ui + " vi: " + vi);
                    		
                    		//file.write("DCT ui: " + ui + " vi: " + vi + " u " + u + " v " + v + "\n");


							f_uvR[u][v] = (int)Math.round((0.25)*cu*cv*cosine(starti, startj, ui, vi, f_xyR));
							vi++;
							//f_uv[u][v] = (1.0/4.0)*cosine(starti, startj, u, v, f_xy, f_uv)
							//System.out.print("f(u,v) " +  f_uvR[u][v] + " ");
							file.write(f_uvR[u][v] + "        ");
						}
						ui++;
						file.write("\n");
						//System.out.println();

				}
				//System.out.println("End of block ");
				startj += 8;
			}
			starti += 8;
		}
		file.close();
		}catch(IOException e){
			System.out.println(e);
		}
	}

	public static double cosine(int row, int col,int u, int v, int[][] f_xy){
		int xi = 0;
		int yi = 0;
		double sumCos = 0.0;
		//try{
		for(int x = row; x < row + 8; x++){
			yi=0;
			for(int y = col; y < col + 8; y++){
				//System.out.println("Cos x: " + xi + " y: "  + yi + " i: " + x  + " j: " + y);
				
				//file.write("Cos x: " + xi + " y: "  + yi + " i: " + x  + " j: " + y + "\n");

				sumCos =  sumCos + (f_xy[x][y] * Math.cos( ((2*xi + 1)*u*Math.PI)/16) * Math.cos(((2*yi +1)*v*Math.PI)/16));
				yi++;
			}
			//file.write(" new row\n");
			xi++;
		}
		//}catch(IOException e){
			//System.out.println(e);
		//}
		return sumCos;
	}

	public static void dct_decode(int[][] f_uvR,int[][] f_xyR, int quant){
		//dequantize
 	
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
						//System.out.println("I x: " + xi + " y: "  + yi);
						f_xyR[x][y] = (int)Math.round((0.25)*inverse_cosine(starti, startj, xi,yi,f_uvR));
						//System.out.print( "f(x,y) " + ((f_xyR[x][y])) + " ");
						yi++;
					}
					//System.out.println();
					xi++;
				}
				startj += 8;
			}
			starti += 8;
		}
	}
	public static double inverse_cosine(int row, int col,int x, int y, int[][] f_uv){
		int ui = 0;
		int vi = 0;

		double sum = 0.0;
		double cu, cv;
		for(int u = row; u < row + 8; u++){
			vi = 0;
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
            	//System.out.println("ID ui: " + ui + " vi: " + vi);
				sum =  sum + (cu*cv*f_uv[u][v]*Math.cos( ((2*x + 1)*ui*Math.PI)/16) * Math.cos(((2*y +1)*vi*Math.PI)/16));
				vi++;
			}
			ui++;
		}
		return sum;
	}
	//=> dequantize [1 2] [1 2] => first value of every square is "on" => [1 0] [1 0] => IDCT => display  second value on [1 2] [1 2]  => IDCT => display
	//				[3 4] [3 4]											  [0 0] [0 0]									  [0 0] [0 0]
	//				[1 2] [1 2]	=> 	third value of every sqaure is "on" => [1 2]									   										   
	// 				[3 4] [3 4]											   [3 0]
	public static void zigzag(){
		int left = 0;
		int right = (0 + 8) - 1; 
		int top = 0;
		int bottom = (0 + 8) - 1;

		int key = 1;
		//go southwest and down and
		//northeast and east until we get to top right corner which will be starti, (startj + 8) - 1 => right
		int i = 0;
		int j = 0;

		j = j+1;
		map.put(key++, new ZigIndex(i,j));
		//while we're not at the top right corner
		while( j != right){
			//southwest
			while(j > left){
				i++;
				j--;
				map.put(key++, new ZigIndex(i,j));

			}
			
			//down
			i++;
			map.put(key++, new ZigIndex(i,j));
			//northeast
			while(i > top){
				i--;
				j++;
				map.put(key++, new ZigIndex(i,j));

			}
			
			//right
			j++;
			map.put(key++, new ZigIndex(i,j));

		}
		//while we're not at the bottom right corner
		while(i != bottom || j != right){


			//Southwest
			while( i < bottom){
				i++;
				j--;
				map.put(key++, new ZigIndex(i,j));

			}
			//right
			j++;
			map.put(key++, new ZigIndex(i,j));

			if(i == bottom && j == right){
				break;
			}
			
			//northeast
			while( j < right){
				i--;
				j++;
				map.put(key++, new ZigIndex(i,j));

			}
			//down
			i++;
			map.put(key++, new ZigIndex(i,j));

		}

	}	
}
