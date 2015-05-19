package com.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

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

	private static final String TEMP_FOLDER = System
			.getProperty("java.io.tmpdir");

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
		try {
			if (channelJson != null) {
				Integer channelId = channelJson.get("channelId").getAsInt();
				FileInfo file = dao.getFile(channelId);
				fileInfo = toJsonElement(file);
			}
		} catch (Exception e) {
			throw e;
		}
		return fileInfo;
	}

	public String getMixedFileContents(String fileId, String fileName) throws Exception {
		File file = null;
		if(fileName != null)
		{
			file = new File(TEMP_FOLDER + fileName);
			if(file.isFile())
			{
				return file.getPath();
			}
		}
		Object[] o = (Object[]) dao.getMixedFileWithContents(Integer
				.valueOf(fileId));
		if (null != o) {
			fileName = o[0].toString();
			file = new File(TEMP_FOLDER + fileName);
			byte[] contents = (byte[]) o[2];
			FileMetaData metaData = (FileMetaData) o[1];
			InputStream b_in = new ByteArrayInputStream(contents);
			AudioFormat format = new AudioFormat(metaData.getSampleRate(),
					metaData.getSampleSizeInBits(), metaData.getChannel(),
					true, false);
			AudioInputStream stream2 = new AudioInputStream(b_in, format,
					contents.length / format.getFrameSize());
			AudioSystem.write(stream2, Type.WAVE, file);

			stream2.close();
			return file.getPath();
		}
		return null;
	}

	public JsonArray getAllFiles() {
		JsonArray files = new JsonArray();
		try {
			List<Object> lst = dao.getAllFiles();
			for (Object o : lst) {
				Object[] obj = (Object[]) o;
				FileInfo file = new FileInfo();
				file.setFileId(Integer.valueOf(obj[0].toString()));
				file.setFileName(obj[1].toString());
				if(obj[2]!=null)
				file.setFileSize(obj[2].toString());
				file.setFileType(obj[3].toString());
				file.setDuration(obj[4].toString());
				Timestamp timestamp = (Timestamp) obj[5];
				long milliseconds = timestamp.getTime()
						+ (timestamp.getNanos() / 1000000);
				Date d = new Date(milliseconds);
				file.setCreationDate(new Date(milliseconds));
				file.setMetaData((FileMetaData) obj[6]);
				files.add(toJsonElement(file));
			}
		} catch (Exception e) {
			throw e;
		}

		return files;
	}

	private static String getDurationString(int seconds) {

		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		seconds = seconds % 60;

		return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : "
				+ twoDigitString(seconds);
	}

	private static String twoDigitString(int number) {

		if (number == 0) {
			return "00";
		}

		if (number / 10 == 0) {
			return "0" + number;
		}

		return String.valueOf(number);
	}

	public JsonArray getAllMixedFiles() {
		JsonArray files = new JsonArray();
		try {
			List<Object> lst = dao.getAllMixedFiles();
			for (Object o : lst) {
				Object[] obj = (Object[]) o;
				MixedFiles file = new MixedFiles();
				file.setFileId(Integer.valueOf(obj[0].toString()));
				file.setFileName(obj[1].toString());
				if(obj[2]!=null)
				file.setFileSize(obj[2].toString());
				file.setDuration(obj[3].toString());
				Timestamp timestamp = (Timestamp) obj[4];
				long milliseconds = timestamp.getTime()
						+ (timestamp.getNanos() / 1000000);
				Date d = new Date(milliseconds);
				file.setCreationDate(new Date(milliseconds));
				file.setMetaData((FileMetaData) obj[5]);
				files.add(toJsonElement(file));
			}
		} catch (Exception e) {
			throw e;
		}

		return files;
	}

	public void deleteMusicFile(String fileId) {
		if (!StringUtils.isEmpty(fileId))
			dao.deleteMixedFile(Integer.valueOf(fileId));
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

	public JsonObject mixSongs(JsonObject files) throws Exception {
		System.out.println("Mix File Started");
		int fileId1 = 0, fileId2 = 0;
		JsonArray fileIds = files.get("fileIds").getAsJsonArray();
		List<FileInfo> sourceFiles = new ArrayList<FileInfo>();
		for (int i = 0; i < fileIds.size(); i++) {
			int fileId = fileIds.get(i).getAsInt();
			FileInfo file = dao.getFileById(fileId);
			File file1 = new File(TEMP_FOLDER + file.getFileName());
			if(file1.isFile())
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				FileChannel fChannel = (new FileInputStream(file1)).getChannel();
			    byte[] barray = new byte[(int) fChannel.size()];
			    ByteBuffer bb = ByteBuffer.wrap(barray);
			    bb.order(ByteOrder.LITTLE_ENDIAN);
			    fChannel.read(bb);
			    file.setFileContents(barray);
			    fChannel.close();
			}
			else
			{
				FileInfo tempFile = dao.getFileContents(fileId);
				file.setFileContents(tempFile.getFileContents());
			}
			sourceFiles.add(file);
		}

		List<short[]> filesShortArray = new ArrayList<short[]>();
		List<short[]> updatedShortArray = new ArrayList<short[]>();
		for (FileInfo f : sourceFiles) {
			byte[] bytes = f.getFileContents();
			short[] shorts = new short[bytes.length / 2];
			// to turn bytes to shorts as either big endian or little endian.
			ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
					.asShortBuffer().get(shorts);
			filesShortArray.add(shorts);
		}
		int mixedFileSize = 0;
		for (int i = 0; i < filesShortArray.size(); i++) {
			short[] arrayMusic1 = filesShortArray.get(i);
			int size_a = arrayMusic1.length;
			if (size_a > mixedFileSize) {
				mixedFileSize = size_a;
			}
		}
		for (int i = 0; i < filesShortArray.size(); i++) {
			short[] arrayMusic1 = filesShortArray.get(i);
			int size_a = arrayMusic1.length;
			if (size_a < mixedFileSize) {
				short[] temp = new short[mixedFileSize];
				// adding series of '0'
				for (int j = size_a + 1; j < mixedFileSize; j++) {
					temp[j] = (short) 0;
				}
				arrayMusic1 = temp;
			}
			updatedShortArray.add(arrayMusic1);
		}
		short[] output = new short[mixedFileSize];
		int nosFiles = updatedShortArray.size();
		for (int i = 0; i < output.length; i++) {
			float samplefile = 0f;
			float mixed = 0f;
			for (int j = 0; j < nosFiles; j++) {
				short[] arrayMusic = updatedShortArray.get(j);
				samplefile = arrayMusic[i] / 32768.0f;
				mixed += samplefile;
			}
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
		ByteBuffer.wrap(end).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
				.put(output);
		InputStream b_in = new ByteArrayInputStream(end);
		try {
			FileInfo sourceFile = sourceFiles.get(0);
			FileMetaData metaData = sourceFile.getMetaData();

			AudioFormat format = new AudioFormat(metaData.getSampleRate(),
					metaData.getSampleSizeInBits(), metaData.getChannel(),
					true, false);

			AudioInputStream stream2 = new AudioInputStream(b_in, format,
					end.length / format.getFrameSize());

			MixedFiles mixedFile = new MixedFiles();
			Set<FileInfo> srcFiles = new HashSet<FileInfo>(sourceFiles);
			String fileName = null;
			for (FileInfo f : srcFiles) {
				if (mixedFile.getChannel1() == null) {
					mixedFile.setChannel1(f);
					fileName = f.getFileName().substring(0,
							f.getFileName().indexOf("."));
				} else if (mixedFile.getChannel2() == null) {
					mixedFile.setChannel2(f);
					fileName += "_"
							+ f.getFileName().substring(0,
									f.getFileName().indexOf("."));
				} else if (mixedFile.getChannel3() == null) {
					mixedFile.setChannel3(f);
					fileName += "_"
							+ f.getFileName().substring(0,
									f.getFileName().indexOf("."));
				} else if (mixedFile.getChannel4() == null) {
					mixedFile.setChannel4(f);
					fileName += "_"
							+ f.getFileName().substring(0,
									f.getFileName().indexOf("."));
				}
			}
			fileName = fileName + ".wav";

			int frameSize = format.getFrameSize();
			float frameRate = format.getFrameRate();

			int durationInSeconds = (int) (end.length / (frameSize * frameRate));
			String formattedDate = getDurationString(durationInSeconds);
			mixedFile.setMetaData(metaData);
			mixedFile.setFileName(fileName);
			mixedFile.setCreationDate(new Date());
			mixedFile.setDuration(formattedDate);
			int id = dao.saveMixedFile(mixedFile);
			mixedFile.setFileId(id);
			mixedFile.setFileContents(end);
			ExecuteSaveThread thread = new ExecuteSaveThread(mixedFile, dao);
			Thread t = new Thread(thread);
			t.start();
			return toJsonElement(mixedFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JsonObject uploadFile(InputStream stream, JsonObject fileDetail,
			JsonObject channelJson) {
		JsonObject fileInfo = new JsonObject();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			FileChannel fChannel = ((FileInputStream)stream).getChannel();
		    byte[] barray = new byte[(int) fChannel.size()];
		    ByteBuffer bb = ByteBuffer.wrap(barray);
		    bb.order(ByteOrder.LITTLE_ENDIAN);
		    fChannel.read(bb);
		    
			/*final FileChannel channel = ((FileInputStream)stream).getChannel();
			MappedByteBuffer buffer1	 = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			// when finished
			channel.close();*/
			/*byte[] buffer = new byte[32000];
			while (true) {
				int r = stream.read(buffer);
				if (r == -1)
					break;
				out.write(buffer, 0, r);
			}*/
			byte[] ret = barray;
			stream.close();
			InputStream bufferedIn = new ByteArrayInputStream(ret);
			AudioInputStream in = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);

			AudioInputStream din = AudioSystem.getAudioInputStream(
					decodedFormat, in);
			out = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[32000];
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
			file.setFileType(fileType);
			file.setCreationDate(new Date());
			FileMetaData metaData = new FileMetaData();
			metaData.setSampleRate(decodedFormat.getSampleRate());
			metaData.setSampleSizeInBits(16);
			metaData.setFrameRate(decodedFormat.getSampleRate());
			metaData.setFrameSize((baseFormat.getChannels() * 2));
			metaData.setChannel(decodedFormat.getChannels());

			int frameSize = decodedFormat.getFrameSize();
			float frameRate = decodedFormat.getFrameRate();

			int durationInSeconds = (int) (ret.length / (frameSize * frameRate));
			String formattedDate = getDurationString(durationInSeconds);

			file.setMetaData(metaData);
			file.setDuration(formattedDate);
			int fileId = dao.saveMusicDetails(file);
			file.setFileId(fileId);
			file.setFileContents(ret);
			ExecuteSaveThread thread = new ExecuteSaveThread(file, dao);
			Thread t = new Thread(thread);
			t.start();
			fileInfo = toJsonElement(file);
			out.close();
			in.close();
			stream.close();
			bufferedIn.close();
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
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fileInfo;
	}

	private JsonObject toJsonElement(Object o) {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

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
			json.addProperty("duration", file.getDuration());
			String dateFormatted = formatter.format(file.getCreationDate());
			json.addProperty("creationDate", dateFormatted);
			/*
			 * byte[] bytesEncoded =
			 * Base64.encodeBase64(file.getFileContents());
			 * json.addProperty("fileContent", new String(bytesEncoded));
			 */
			if (file.getChannel() != null) {
				Channel ch = file.getChannel();
				JsonObject channel = toJsonElement(ch);
				json.add("channel", channel);
			}
		} else if (o instanceof MixedFiles) {
			MixedFiles file = (MixedFiles) o;
			json.addProperty("fileId", file.getFileId());
			json.addProperty("fileName", file.getFileName());
			json.addProperty("fileSize", file.getFileSize());
			json.addProperty("filePath", file.getFilePath());
			json.addProperty("duration", file.getDuration());
			String dateFormatted = formatter.format(file.getCreationDate());
			json.addProperty("creationDate", dateFormatted);
			/*
			 * byte[] bytesEncoded =
			 * Base64.encodeBase64(file.getFileContents());
			 * json.addProperty("fileContent", new String(bytesEncoded));
			 */
			JsonArray sourceFiles = new JsonArray();
			if (file.getChannel1() != null) {
				JsonObject srcFile = toJsonElement(file.getChannel1());
				sourceFiles.add(srcFile);
			}
			if (file.getChannel2() != null) {
				JsonObject srcFile = toJsonElement(file.getChannel2());
				sourceFiles.add(srcFile);
			}
			if (file.getChannel3() != null) {
				JsonObject srcFile = toJsonElement(file.getChannel3());
				sourceFiles.add(srcFile);
			}
			if (file.getChannel4() != null) {
				JsonObject srcFile = toJsonElement(file.getChannel4());
				sourceFiles.add(srcFile);
			}
			json.add("sourceFiles", sourceFiles);
		}
		return json;
	}
}
