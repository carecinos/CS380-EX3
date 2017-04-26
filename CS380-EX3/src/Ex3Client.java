import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 
 */

/**
 * This program creates a Socket connection to codebank.xyz port number
 * 38103. It then reads a byte of data that represents the number of bytes
 * that will follow. Once all bytes of data are read, the checksum is calculated
 * and sent to the server. 
 * 
 * @author cesar
 *
 */
public class Ex3Client {

	/**
	 * This main method saves the bytes of data into an array and
	 * passes that array into the checkSum method. Then sends the 
	 * value to the server in two byte sequences.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws  IOException {
		// TODO Auto-generated method stub
		try(Socket socket = new Socket("Codebank.xyz", 38103)){	//Create Socket Connection
			
			System.out.println("Connected to server.");
			
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			int numOfBytes = is.read();	//First byte is the number of bytes to be received
			
			System.out.println("Reading "+ numOfBytes+ " bytes.");
			byte[] byteArray = new byte[numOfBytes];
			
			for(int i = 0; i<byteArray.length; i++){	//Save data in an array
				byteArray[i]= (byte) is.read();

			}
			
			System.out.print("Data Received: "); //Display bytes received 
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
			//Display checksum
			System.out.println("Checksum Calculated: 0x" + String.format("%04X", checksum & 0xFFFF));
			
			byte[] outArray = new byte[2];	//Send checksum in two byte sequences
			
			outArray[0] = (byte)((checksum & 0xFF00)>>>8);
			outArray[1] = (byte)(checksum & 0x00FF);
			
			os.write(outArray);	//Send checksum
			
			if(is.read() == 1)	//Server Response
				System.out.println("Response good.");			
		}
		System.out.println("Disconnected from Server.");

	}
	
	/**
	 * This method calculates the checksum of the bytes of
	 * data received from the server.
	 * @param bytes This is the array of bytes of data
	 * @return short This is the checksum value.
	 */
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
