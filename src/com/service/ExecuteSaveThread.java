package com.service;

import java.util.concurrent.Callable;

import com.dao.MusicDaoImpl;
import com.entity.FileInfo;
import com.entity.MixedFiles;

public class ExecuteSaveThread implements Callable<Integer>{

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
	public Integer call() throws Exception {
		Integer idx = 0;
		if(file !=null)
		{
			dao.saveMusicDetails(file);
			file = null;
		}
		if(mixedFile !=null)
		{
			idx = dao.saveMixedFile(mixedFile);
			mixedFile = null;
		}
		return idx;
	}

}
