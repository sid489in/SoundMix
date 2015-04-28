package com.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.dao.MusicDaoImpl;
import com.entity.Channel;
import com.entity.FileInfo;
import com.entity.FileMetaData;
import com.entity.MixedFiles;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Component
public class MusicServiceImpl {

	@Autowired
	private MusicDaoImpl dao;

	private static final String TEMP_FOLDER ="c:\\mixedFiles\\";
	public void createChannel() {
		for (int i = 0; i < 4; i++) {
			Channel c = new Channel();
			int channelId = i + 1;
			c.setChannelName("Channel " + channelId);
			dao.saveChannelDetails(c);
		}
	}

	public JsonArray getChannels() {
		JsonArray channelArray = new JsonArray();
		List<Channel> channels = dao.getAllChannels();
		for (Channel c : channels) {
			channelArray.add(toJsonElement(c));
		}
		return channelArray;
	}

	public JsonObject getFile(JsonObject channelJson) {
		JsonObject fileInfo = new JsonObject();
		if (channelJson != null) {
			Integer channelId = channelJson.get("channelId").getAsInt();
			FileInfo file = dao.getFile(channelId);
			fileInfo = toJsonElement(file);
		}
		return fileInfo;
	}
	
	public String getMixedFilePath(String fileId)
	{
		Object o = dao.getMixedFilePath(Integer.valueOf(fileId));
		return (String) o;
	}

	public JsonArray getAllFiles() {
		JsonArray files = new JsonArray();
		List<Object> lst = dao.getAllFiles();
		for (Object o : lst) {
			Object[] obj = (Object[]) o;
			FileInfo file = new FileInfo();
			file.setFileId(Integer.valueOf(obj[0].toString()));	
			file.setFileName(obj[1].toString());	
			file.setFileSize(obj[2].toString());	
			file.setFileType(obj[3].toString());	
			file.setMetaData((FileMetaData) obj[4]);	
			files.add(toJsonElement(file));
		}
		return files;
	}
	
	public JsonArray getAllMixedFiles() {
		JsonArray files = new JsonArray();
		List<Object> lst = dao.getAllMixedFiles();
		for (Object o : lst) {
			MixedFiles obj = (MixedFiles) o;
			files.add(toJsonElement(obj));
		}
		return files;
	}
	
	public void deleteMusicFile(String fileId)
	{
		if(!StringUtils.isEmpty(fileId))
		dao.deleteMixedFile(Integer.valueOf(fileId));
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	private short[] buildShortArray(List<Short> music) {
		short[] shortData = new short[music.size()];
		for (int j = 0; j < music.size(); j++) {
			shortData[j] = music.get(j);
		}
		return shortData;
	}

	public List<Short> createMusicArray(byte[] bytes) throws IOException {
		List<Short> musicList = new ArrayList<Short>();

		ByteBuffer bb = ByteBuffer.wrap(bytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		ShortBuffer sb = bb.asShortBuffer();

		for (int j = 0; j < sb.capacity(); j++) {
			musicList.add(sb.get(j));
		}

		return musicList;
	}

	private byte[] convertToByteArray(short[] content) {
		byte[] end = new byte[content.length * 2];
		ByteBuffer.wrap(end).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
				.put(content);
		return end;
	}
	public JsonObject mixSongs(JsonObject files) throws Exception{
		System.out.println("Mix File Started");
		int fileId1 = 0, fileId2 = 0;
		fileId1 = files.get("sourceFileId").getAsInt();
		fileId2 = files.get("targetFileId").getAsInt();
		FileInfo sourceFile = dao.getFileById(fileId1);
		FileInfo targetFile = dao.getFileById(fileId2);

		List<Short> file1MusicList = createMusicArray(sourceFile
				.getFileContents());
		List<Short> file2MusicList = createMusicArray(targetFile
				.getFileContents());

		short[] arrayMusic1 = new short[sourceFile.getFileContents().length/2];
		// to turn bytes to shorts as either big endian or little endian. 
		ByteBuffer.wrap(sourceFile.getFileContents()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(arrayMusic1);
		
		short[] arrayMusic2 = new short[targetFile.getFileContents().length/2];
		// to turn bytes to shorts as either big endian or little endian. 
		ByteBuffer.wrap(targetFile.getFileContents()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(arrayMusic2);
		
		int size_a = arrayMusic1.length;
		int size_b = arrayMusic2.length;

		if (size_a > size_b) {
			short[] temp = new short[size_a];
			System.arraycopy(arrayMusic2, 0, temp, 0, arrayMusic2.length);
			// adding series of '0'
			for (int i = size_b + 1; i < size_a; i++) {
				temp[i] = (short) 0;
			}
			arrayMusic2 = temp;
		} else if (size_a < size_b) {
			short[] temp = new short[size_b];
			System.arraycopy(arrayMusic1, 0, temp, 0, arrayMusic1.length);
			for (int i = size_a + 1; i < size_b; i++) {
				temp[i] = (short) 0;
			}
			arrayMusic1 = temp;
		} else {
			// do nothing
		}

		short[] output = new short[arrayMusic1.length];
		for (int i = 0; i < output.length; i++) {

			float samplef1 = arrayMusic1[i] / 32768.0f;
			float samplef2 = arrayMusic2[i] / 32768.0f;

			float mixed = samplef1 + samplef2;
			// reduce the volume a bit:
			mixed *= 0.8;
			// hard clipping
			if (mixed > 1.0f)
				mixed = 1.0f;
			if (mixed < -1.0f)
				mixed = -1.0f;
			short outputSample = (short) (mixed * 32768.0f);

			output[i] = outputSample;
		}
	    
		 // to turn shorts back to bytes.
        byte[] end = new byte[output.length * 2];
        ByteBuffer.wrap(end).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(output);
        InputStream b_in = new ByteArrayInputStream(end);
		try {
			
			FileMetaData metaData = sourceFile.getMetaData();
			
			
			AudioFormat format = new AudioFormat(metaData.getSampleRate(),
					metaData.getSampleSizeInBits(),
					metaData.getChannel(), true, false);
				AudioInputStream stream2 = new AudioInputStream(b_in, format,
						end.length);
				String fileName = "SampleMusicFile"+Math.random()+".wav";
				File file = new File(TEMP_FOLDER + fileName);
				file.getParentFile().mkdirs();    
				AudioSystem.write(stream2, Type.WAVE, file);
				b_in.close();
			
			System.out.println("FILEEEEEEEEEEE CREATEDDDDD");
			
			long audioFileLength = file.length();
			int frameSize = format.getFrameSize();
			float frameRate = format.getFrameRate();
			float durationInSeconds = (audioFileLength / (frameSize * frameRate));
			
			System.out.println("DURRRRRRRRR"+ durationInSeconds);
			MixedFiles mixedFile = new MixedFiles();
			mixedFile.setSourceFile(sourceFile);
			mixedFile.setTargetFile(targetFile);
			//mixedFile.setFileContents(end);
			mixedFile.setMetaData(metaData);
			String fileSize = humanReadableByteCount((output.length * 2), false);
			mixedFile.setFileName(fileName);
			mixedFile.setFileSize(fileSize);
			mixedFile.setFilePath(file.getPath());
			int id = dao.saveMixedFile(mixedFile);
			mixedFile.setFileId(id);
			return toJsonElement(mixedFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public JsonObject uploadFile(InputStream stream, JsonObject fileDetail, JsonObject channelJson) {
		JsonObject fileInfo = new JsonObject();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			while (true) {
				int r = stream.read(buffer);
				if (r == -1)
					break;
				out.write(buffer, 0, r);
			}
			byte[] ret = out.toByteArray();
			stream.close();
			InputStream bufferedIn = new ByteArrayInputStream(ret);
			AudioInputStream in = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
			out = new ByteArrayOutputStream();
			buffer = new byte[1024];
			while (true) {
				int r = din.read(buffer);
				if (r == -1)
					break;
				out.write(buffer, 0, r);
			}
			ret = out.toByteArray();
			FileInfo file = new FileInfo();
			String fileName = fileDetail.get("fileName").getAsString();
			String fileType = fileDetail.get("fileType").getAsString();
			file.setFileName(fileName);
			file.setFileContents(ret);
			file.setFileType(fileType);
			String fileSize = humanReadableByteCount(ret.length, false);
			if (channelJson != null) {
				Channel c = new Channel();
				c.setChannelId(channelJson.get("channelId").getAsInt());
				c.setChannelName(channelJson.get("channelName").getAsString());
				file.setChannel(c);
			}
			file.setFileSize(fileSize);
			FileMetaData metaData = new FileMetaData();
			metaData.setSampleRate(decodedFormat.getSampleRate());
			metaData.setSampleSizeInBits(16);
			metaData.setFrameRate(decodedFormat.getSampleRate());
			metaData.setFrameSize((baseFormat.getChannels() * 2));
			metaData.setChannel(decodedFormat.getChannels());
			file.setMetaData(metaData);
			dao.saveMusicDetails(file);
			fileInfo = toJsonElement(file);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileInfo;
	}

	private JsonObject toJsonElement(Object o) {
		JsonObject json = new JsonObject();
		if (o instanceof Channel) {
			Channel channel = (Channel) o;
			json.addProperty("channelId", channel.getChannelId());
			json.addProperty("channelName", channel.getChannelName());
			json.addProperty("channelDescription",
					channel.getChannelDescription());
		} else if (o instanceof FileInfo) {
			FileInfo file = (FileInfo) o;
			json.addProperty("fileId", file.getFileId());
			json.addProperty("fileName", file.getFileName());
			json.addProperty("fileSize", file.getFileSize());
			/*byte[] bytesEncoded = Base64.encodeBase64(file.getFileContents());
			json.addProperty("fileContent", new String(bytesEncoded));*/
			if (file.getChannel() != null) {
				Channel ch = file.getChannel();
				JsonObject channel = toJsonElement(ch);
				json.add("channel", channel);
			}
		}
		else if (o instanceof MixedFiles) {
			MixedFiles file = (MixedFiles) o;
			json.addProperty("fileId", file.getFileId());
			json.addProperty("fileName", file.getFileName());
			json.addProperty("fileSize", file.getFileSize());
			json.addProperty("filePath", file.getFilePath());
			/*byte[] bytesEncoded = Base64.encodeBase64(file.getFileContents());
			json.addProperty("fileContent", new String(bytesEncoded));*/
			if (file.getSourceFile() != null) {
				FileInfo sourceFile = file.getSourceFile();
				JsonObject sourceFileJson = toJsonElement(sourceFile);
				json.add("sourceFile", sourceFileJson);
			}
			if (file.getTargetFile() != null) {
				FileInfo targetFile = file.getTargetFile();
				JsonObject targetFileJson = toJsonElement(targetFile);
				json.add("targetFile", targetFileJson);
			}
		}
		return json;
	}
}
