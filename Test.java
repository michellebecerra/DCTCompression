import java.io.*;

public class Test{
	static int height = 16;
	static int width = 24;
	public static void main(String[] args){
		int[][] test = new int[16][24];
	    dct(test);

	}
		
	public static void dct(int[][] t){
		//break into 8x8 and feed it to cosine function
		int starti = 0;
		while(starti != height){
			int startj = 0;
			while(startj != width){

				for(int u = starti; u < starti + 8; u++){
					
						for(int v = startj; v < startj + 8; v++){
							//f_uv[u][v] = (1.0/4.0)*cosine(starti, startj, u, v, f_xy, f_uv)
							System.out.print( u + "," + v + "  ");
						}
						System.out.println();

				}
				startj += 8;
			}
			starti += 8;
		}
		

	}	
}
