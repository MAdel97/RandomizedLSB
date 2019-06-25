package stegano;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.bouncycastle.pqc.jcajce.provider.test.QTESLATest;



public class main
{
	
	
	public main()
	{
	}

	public boolean encode(String path, String original, String ext1, String stegan, String message)
	{
		String			file_name 	= image_path(path,original,ext1);
		BufferedImage 	image_orig	= getImage(file_name);
		
			BufferedImage image = user_space(image_orig);
		image = add_text(image,message);
		
		return(setImage(image,new File(image_path(path,stegan,"png")),"png"));
	}
	

	public byte[] decode(String path, String name)
	{
		byte[] decode;
		try
		{
			
			BufferedImage image  = user_space(getImage(image_path(path,name,"png")));
			decode = decode_text(get_byte_data(image));
			
			return( (decode));
		}
        catch(Exception e)
        {
			JOptionPane.showMessageDialog(null, 
				"There is no hidden message in this image!","Error",
				JOptionPane.ERROR_MESSAGE);
			return null;
        }
    }
    

	private String image_path(String path, String name, String ext)
	{
		return path + "/" + name + "." + ext;
	}
	
	
	private BufferedImage getImage(String f)
	{
		BufferedImage 	image	= null;
		File 		file 	= new File(f);
		
		try
		{
			image = ImageIO.read(file);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, 
				"Image could not be read!","Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	

	
	private boolean setImage(BufferedImage image, File file, String ext)
	{
		try
		{
			file.delete(); //delete resources used by the File
			ImageIO.write(image,ext,file);
			return true;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
				"File could not be saved!","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	
	private BufferedImage user_space(BufferedImage image)
	{
		//create new_img with the attributes of image
		BufferedImage new_img  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D    graphics = new_img.createGraphics();
		graphics.drawRenderedImage(image, null);
		graphics.dispose(); //release all allocated memory for this image
		return new_img;
	}
	

	private byte[] get_byte_data(BufferedImage image)
	{
		WritableRaster raster   = image.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	
	
	private byte[] bit_conversion(int i)
	{

		byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
		byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
		byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
		byte byte0 = (byte)((i & 0x000000FF)       );
		
		return(new byte[]{byte3,byte2,byte1,byte0});
	}
	
	
		private BufferedImage add_text(BufferedImage image, String text)
		{
			//convert all items to byte arrays: image, message, message length
			byte img[]  = get_byte_data(image);
			byte msg[] = text.getBytes();
			byte len[]   = bit_conversion(msg.length);
			encode_text(encode_msgLen(img, len,  0), msg,  32);
	
			return image;
		}

	private byte[] encode_msgLen(byte[] image, byte[] addition, int offset)
	{
		//check that the data + offset will fit in the image
		if(addition.length + offset > image.length)
		{
			throw new IllegalArgumentException("File not long enough!");
		}
		long seed = 10000000; 
		 byte key[]= {13,2,4,6,7}; 
		 byte [] rPos =generate_randomPos(seed, key, 10000);

		for(int i=0; i<addition.length; ++i)
		{
			//loop through the 8 bits of each byte
			int add = addition[i];
			
			
			for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
			{

				 
			int j=0;
				int imOffset=image[offset];
				int position=Byte.toUnsignedInt(rPos[j]);
			j++;
				int b = (add >>> bit) & 1;
				int c= imOffset & 1;
				int d = (imOffset>>position) &1;
				
				if((d ^ c) != b){
					image[offset]=   (byte)((image[offset]  ^1));
					
			}
					
			}
		}
		return image;
	}

	
	private byte[] encode_text(byte[] image, byte[] addition, int offset)
	{
	
		if(addition.length + offset > image.length)
		{
			throw new IllegalArgumentException("File not long enough!");
		}
		long seed = 10000000; 
		 byte key[]= {13,2,4,6,7}; 
		 byte [] rPos =generate_randomPos(seed, key, 10000);

		for(int i=0; i<addition.length; ++i)
		{
			//loop through the 8 bits of each byte
			int add = addition[i];
			
			
			for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
			{

				 
			int j=32;
				int imOffset=image[offset];
				int position=Byte.toUnsignedInt(rPos[j]);
			j++;
				int b = (add >>> bit) & 1;
				int c= imOffset & 1;
				int d = (imOffset>>position) &1;
				
		
				if((d ^ c) != b){
					image[offset]=   (byte)((image[offset]  ^1));
					
			}
					
			}
		}
		return image;
	}
    

	private byte[] decode_text(byte[] image)
	{
		int length = 0;
		int offset  = 32;
		long seed = 10000000; 
		 byte key[]= {13,2,4,6,7}; 
		 byte [] rPos =generate_randomPos(seed, key, 10000);

	  
	    
	        
	    	
		for(int i=0; i<32; ++i) //i=24 will also work, as only the 4th byte contains real data
		{
			int j=0;
			int position=Byte.toUnsignedInt(rPos[j]);
			j++;
			int imBit= image[i];
			int b= imBit & 1;
			int c = (imBit>>position) &1;
			int d= b^c;
		
			length = (length << 1) | d;
		}
	
		byte[] result = new byte[length];
		
		//loop through each byte of text
		for(int b=0; b<result.length; ++b )
		{
			//loop through each bit within a byte of text
			for(int i=0; i<8; ++i, ++offset)
			{
				int j=32;
				int position=Byte.toUnsignedInt(rPos[j]);
				j++;
				int imBit= image[offset];
				int b1= imBit & 1;
				int c = (imBit>>position) &1;
				
				int d= b1^c;
				
				result[b] = (byte) ((result[b] << 1) | d);
			}
			
		}
		
		return result;
	}
	public byte [] generate_randomPos(long seed, byte[] key, int len)
	{
		 
		 Random random = new Random(); 
	      random.setSeed(seed); 
	      byte[] bytes = new byte[len]; 
	    
	     
	      random.nextBytes(bytes);
	      for(int i=0;i<bytes.length;i++) {
	    	 
	    	 bytes[i]=(byte) (Byte.toUnsignedInt(bytes[i])%8);
	    	  
	      }
	      List<byte[]> rbytes  = Arrays.asList(bytes);

	      List<Integer> rints  ;
	     
	      SecureRandom random1 = new SecureRandom(key); 


	      Collections.shuffle(rbytes,random1);
	      byte[] bytes1 = rbytes.get(0);
		    
	return bytes1;
	}
	public double measure_MSE (String path, String original,String stego, String ext, String message) {
		String			file_name 	= image_path(path,original,ext);
		String			file_name2 	= image_path(path,stego,ext);
	
		BufferedImage 	image_orig	= getImage(file_name);
		BufferedImage 	stego_img	= getImage(file_name2);
		
		byte [] origi =get_byte_data(image_orig);
		byte [] stegano =get_byte_data(stego_img);
		long sum = 0;
		int diff;
		
	               for (int i = 0; i < stegano.length; i++) {
			              int ioriginal = origi[i] & 0xFF;
		                   	int iresult = stegano[i] & 0xFF;
		                  	diff = ioriginal - iresult;
			                sum += diff * diff;
			                //System.out.println(ioriginal+" "+ iresult);
		}
		double MSE = (double) sum / origi.length;
		return MSE;
	}
	public  double measure_PSNR(String path, String original,String stego, String ext, String message) {
		
		double MSE = measure_MSE( path, original, stego,  ext,  message);
		double PSNR = MSEtoPSNR(MSE, 255.0);
		return PSNR;
	}
	public static double MSEtoPSNR(double MSE, double max) {
		return 10 * Math.log10((max * max) / MSE);
	}
	
	public  static void main(String[] arr) throws Exception {
		main magic2 = new main();
		 QTESLATest magic= new QTESLATest();
		 System.out.println("before encoding");
		 String message ="hello";
		 magic.setUp();
	    	magic.testGenerateKeyPairSigningVerifyingPI(message);
	    
	    	byte [] sig= (magic.sigValue);
	    	System.out.println(sig);
	    	System.out.println("signature length in bits "+sig.length);
	    	String encoded = Base64.getEncoder().encodeToString(sig);
	    	//String encoded ="Su2YCXEyHAcoYh+G6d2OVLDWiE/xhKwPbkj1tAoQax5XrlVdiYwtvKvHrPWM+JrAzvq08IVe+8r7J/nJMrqnpvL5+bX/aexdFu++fRV7Q6CUHypauLsCDo057iuUfiRhWFqIWSsrzTZT5NILiWojL99ClTjfxAUTfkp/0b0r3L4omcA0/AX8vk+b/mqdLRS/M7Tq5iG9LtdvDEjjLdkfSOfHmoJ0pGJLe2y88zyPrCk1Jbkr+QjAjcLZi9HYkfM1fNjqhycTU4NNofkVBJGtMC5Knhq4hO9cH4pQi6jOZem0xa7pwP8xrr0QuvzIDUdv";
	    	
	    System.out.println("Signature length in base64 "+encoded .length());
	    System.out.println("base 64 signature String : "+encoded);
		 String encode_path  =        "c:\\Users\\Mohammed146876\\Desktop\\test stego class1";;
		 String original =   "lena";
		 String ext1 =         "png";
		 String stegan =    "result";
		

		
		magic2. encode (encode_path,  original,  ext1, stegan,  encoded);
		 String decode_path =  "c:\\Users\\Mohammed146876\\Desktop\\test stego class1";
		 String name= "result";
		 byte [] decoded= (magic2.decode( decode_path,  name));
		 if (encoded.equals(new String(magic2.decode( decode_path,  name))))
			 System.out.println("yes, extracted base64 string  from image matched with initial signature");
			 
		 else 
			 System.out.println("no, extracted base64 string  from image matched with initial signature");
		
			 System.out.println("after decoding");
			 System.out.println("signature length in base64 "+decoded.length);
			 System.out.println("Signature in base64 : " +new String(decoded));
			 byte[] afterencode =(Base64.getDecoder().decode(decoded));
			 System.out.println("signature length in bits "+ afterencode.length);
			 
			 if(Arrays.equals(afterencode, sig))
				 System.out.println("yes, extracted byte array from image matched with initial signature");
			 else
				 System.out.println("No, extracted byte array from image did not match with initial signature");
			
		// System.out.print(new String(afterencode));
		 
		System.out.println("MSE value is " +magic2.measure_MSE(encode_path, original, stegan, ext1, encoded));
		System.out.println("PSNR value is " +magic2.measure_PSNR(encode_path, original, stegan, ext1, encoded));
	   }
}

