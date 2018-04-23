A program to display original image as well as image that has gone through some DCT transform used in JPEG. 

To run the code from command line, first compile with:

>> javac CoderDecoder.java

then, you can run the program  for each sample image as:

>> java CoderDecoder image1.rgb 0 3 0

where, the first parameter is the image file name, second is the quantization level from 0 to 7, the third is the delivery mode 1, 2, 3 representing the delivery modes as outlined in the assingment, the fourth is the latency parameter in milliseconds.

---------------------------------------------------------------------------------------------
Assumptions:
- Fixed width x height set to 352 width and  288 height

Implementation notes :
- Progressive delivery using successive bit approximation, mode 3:
	I chose to represent my values as javas int which uses 32 bits. Therefore this mode makes 32 passes. I mask the values of each cell in the image with 1 and shift this mask to the right turning on the specified ith bit until we get to the least significant bit.
	Please Note: Th pixels will start out jumbled but will clear up in time. It could take up to 1-2 full minutes to see the image clear up but it eventually does converge to the original image. This was also consulted with the TA. 
- There is some latency even when the latency parameter is set to 0. This was consulted with the TA and left like this on purpose.