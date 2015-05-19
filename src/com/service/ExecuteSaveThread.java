package com.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat.Type;

import com.dao.MusicDaoImpl;
import com.entity.FileInfo;
import com.entity.FileMetaData;
import com.entity.MixedFiles;

public class ExecuteSaveThread implements Runnable{

	private static final String TEMP_FOLDER = System
			.getProperty("java.io.tmpdir");
	
	public ExecuteSaveThread(FileInfo file, MusicDaoImpl dao)
	{
		this.file = file;
		this.dao = dao;
	}
	
	public ExecuteSaveThread(MixedFiles file,MusicDaoImpl dao)
	{
		this.mixedFile = file;
		this.dao= dao;
	}
	private byte[] contents;
	
	private MusicDaoImpl dao;
	
	private FileInfo file;
	
	private MixedFiles mixedFile;
	
	@Override
	public void run() {
		Integer idx = 0;
		File createdFile =null;
		byte[] contents = null;
		FileMetaData metaData =null;
		if(file!=null)
		{
			createdFile = new File(TEMP_FOLDER + file.getFileName());
			contents = file.getFileContents();
			metaData = file.getMetaData();	
		}
		else
		{
			createdFile = new File(TEMP_FOLDER + mixedFile.getFileName());
			contents = mixedFile.getFileContents();
			metaData = mixedFile.getMetaData();
		}
		InputStream b_in = new ByteArrayInputStream(contents);
		AudioFormat format = new AudioFormat(metaData.getSampleRate(),
				metaData.getSampleSizeInBits(), metaData.getChannel(),
				true, false);
		AudioInputStream stream2 = new AudioInputStream(b_in, format,
				contents.length / format.getFrameSize());
		try {
			AudioSystem.write(stream2, Type.WAVE, createdFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				stream2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		

		if(file !=null)
		{
			dao.updateMusicDetails(file);
			file = null;
		}
		if(mixedFile !=null)
		{
			dao.updateMixedFile(mixedFile);
			mixedFile = null;
		}
		System.out.println("@@@Uplode complete");
	}

}
