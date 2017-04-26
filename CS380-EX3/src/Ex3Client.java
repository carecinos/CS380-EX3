import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 
 */

/**
 * @author cesar
 *
 */
public class Ex3Client {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws  IOException {
		// TODO Auto-generated method stub
		try(Socket socket = new Socket("Codebank.xyz", 38103)){
			
			System.out.println("Connected to server.");
			
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			int numOfBytes = is.read();
			
			System.out.println("Reading "+ numOfBytes+ " bytes.");
			byte[] byteArray = new byte[numOfBytes];
			
			for(int i = 0; i<byteArray.length; i++){
				byteArray[i]= (byte) is.read();

			}
			
			System.out.print("Data Received: ");
			String byteString;
			for(int i = 0; i<byteArray.length; i++){
				byteString = "";
                if(i%10==0){
                    System.out.print("\n    ");
                }
                byteString += String.format("%02X",byteArray[i]);                
                System.out.print(byteString);
			}
			
			System.out.println();
			
			short checksum = checksum(byteArray);//Calculates Checksum
			System.out.println("Checksum Calculated: 0x" + String.format("%04X", checksum & 0xFFFF));
			
			byte[] outArray = new byte[2];
			
			outArray[0] = (byte)((checksum & 0xFF00)>>>8);
			outArray[1] = (byte)(checksum & 0x00FF);
			
			os.write(outArray);
			
			if(is.read() == 1)
				System.out.println("Response good.");			
		}
		System.out.println("Disconnected from Server.");

	}
	
	public static short checksum(byte[] bytes){
		long sum = 0;
		
		for(int i = 0; i < bytes.length; i+=2){
			int thisInt = bytes[i] & 0xFF;
			thisInt <<= 8;
			
			if(i != bytes.length-1)
				thisInt |= (bytes[i+1]&0x00FF);
			
			sum += thisInt;

			if((sum & 0xFFFF0000)>0){
				sum &= 0xFFFF;
				sum++;
			}
		}
		return (short)((~((sum & 0xFFFF) + (sum >> 16))) & 0xFFFF);
	}
	

}
