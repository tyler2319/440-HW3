package DefaultObjects;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import Config.Configuration;
import Interfaces.InputFormat440;
import Interfaces.InputSplit440;
import Interfaces.RecordReader440;

public class ImageInputFormat440 implements InputFormat440 {
	
private Configuration config;
	
	public void configure(Configuration config) {
		this.config = config;
	}

	public RecordReader440<Long, byte[]> getRecordReader440(InputSplit440 split) {
		return new ImageRecordReader440((ImageSplit440) split);
	}

	public InputSplit440[] getSplits(int numSplits) {
		String pathStr = config.getInputFilePath();
		// open image
		File imgPath = new File(pathStr);
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(imgPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		// get DataBufferBytes from Raster
		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
		byte[] bytes = data.getData();
		
		int numPixels = bytes.length / 3;
		int pixelsPerSplit = numPixels / numSplits;
		
		ImageSplit440[] result = new ImageSplit440[numSplits];
		
		String[] pathSplit = pathStr.split("\\.");
		String txtPath = pathSplit[0] + ".txt";
		OutputStream os = null;
		try {
			os = Files.newOutputStream(Paths.get(txtPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < numPixels; i++) {
			byte[] pixel = new byte[3];
			pixel[0] = bytes[3*i];
			pixel[1] = bytes[3*i + 1];
			pixel[2] = bytes[3*i + 2];
			try {
				os.write(pixel);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Done writing to file!");
		
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < numSplits - 1; i++) {
			result[i] = new ImageSplit440(txtPath, i * pixelsPerSplit, pixelsPerSplit);
		}
		int finalStart = (numSplits - 1) * pixelsPerSplit;
		int finalLength = numPixels - (finalStart);
		result[numSplits - 1] = new ImageSplit440(pathStr, finalStart, finalLength);
		
		return result;
	}

	public Class<Long> getKeyClass() {
		return Long.class;
	}
	
	public Class<byte[]> getValueClass() {
		return byte[].class;
	}

}
