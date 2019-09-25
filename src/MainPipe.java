import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class MainPipe {

	public static void main(String[] args) {

		try {
			Pipe pipe = Pipe.open(); // we open the pipe

			Runnable writer = () -> {
				try {
					Pipe.SinkChannel sinkChannel = pipe.sink(); // SinkChannel is FROM through we send data
					ByteBuffer buffer = ByteBuffer.allocate(56); // initializing buffer for 56 bytes

					for(int i=0; i<10; i++) {
						String currentTime = "The time is " + System.currentTimeMillis();
						buffer.put(currentTime.getBytes()); // putting the byte array of the String
						buffer.flip();

						while(buffer.hasRemaining()) { // checks if there is enough space in the buffer
							sinkChannel.write(buffer); // writes the buffer into the sink channel
						}
						buffer.flip();
						Thread.sleep(100); // sleep so the source channel (destination) can access the pipe
					}

				} catch(Exception e) {
					e.printStackTrace();

				}
			};
			
			Runnable reader = () -> {
				try {
					Pipe.SourceChannel sourceChannel = pipe.source(); // SourceChannel is TO where we send the data
					ByteBuffer buffer = ByteBuffer.allocate(56); // same buffer
					
					for(int i=0; i<10; i++) {
						int bytesRead = sourceChannel.read(buffer); // returns how many bytes are in the buffer filled from the source channel
						byte[] timeString = new byte[bytesRead]; // creates the size of byte[] to hold the elements of the buffer
						buffer.flip();
						buffer.get(timeString); // writes the buffer TO timeString byte array
						System.out.println("Reader Thread: "+ new String(timeString)); // prints the byte array
						buffer.flip();
						Thread.sleep(100); // the sink channel has access to the pipe
					}
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			};
			
			// we run the 2 threads
			new Thread(writer).start();
			new Thread(reader).start();

		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
