package com.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.entity.Channel;
import com.entity.FileInfo;
import com.entity.FileMetaData;
import com.entity.MixedFiles;

@Component
public class MusicDaoImpl {

	private HibernateTemplate hibernateTemplate;

	public MusicDaoImpl() {

	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		System.out.println("SESSION FACTORY CREATED");
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public Integer saveMusicDetails(FileInfo file) {
		try {
			return (Integer) getHibernateTemplate().save(file);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateMusicDetails(FileInfo file) {
		try {
			getHibernateTemplate().update(file);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	public Integer saveMixedFile(MixedFiles file) {
		try {
			Serializable id = getHibernateTemplate().save(file);
			return (Integer) id;
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void updateMixedFile(MixedFiles file) {
		try {
			getHibernateTemplate().update(file);
			System.out.println("Music file update complete");
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	public Integer deleteMixedFile(int fileId) {
		try {
			getHibernateTemplate().bulkUpdate(
					"delete from " + MixedFiles.class.getName()
							+ " where fileId=" + fileId);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveChannelDetails(Channel ch) {
		try {
			getHibernateTemplate().saveOrUpdate(ch);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	public List<Object> getAllFiles() {
		return getHibernateTemplate()
				.find("select f.fileId, f.fileName, f.fileSize, f.fileType, f.duration, f.creationDate, f.metaData from FileInfo f");
		// return
		// getHibernateTemplate().find("select f.fileName, f.fileSize, f.fileType, f.metaData from FileInfo f");
	}

	public List<Object> getAllMixedFiles() {
		// return getHibernateTemplate().find("from MixedFiles");
		return getHibernateTemplate()
				.find("select f.fileId, f.fileName, f.fileSize, f.duration, f.creationDate, f.metaData from MixedFiles f");
	}

	public FileInfo getFile(int channelId) {
		return (FileInfo) getHibernateTemplate().find(
				"from FileInfo where channel.channelId =" + channelId).get(0);
	}

	public Object getMixedFileWithContents(int fileId) {
		List list = getHibernateTemplate().find(
				"select mf.fileName, mf.metaData, mf.fileContents from MixedFiles mf where mf.id =" + fileId);
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	public FileInfo getFileById(int fileId) {
		Object obj = getHibernateTemplate().find("select f.fileId, f.fileName, f.fileSize, f.fileType, f.duration, f.creationDate, f.metaData from FileInfo f where f.fileId ="+fileId).get(0);
		FileInfo f = new FileInfo();
		if(null != obj)
		{
			Object o[] = (Object[]) obj;
			f.setFileId(Integer.valueOf(o[0].toString()));
			f.setFileName(o[1].toString());
			f.setFileType(o[3].toString());
			f.setDuration(o[4].toString());
			f.setCreationDate((Date) o[5]);
			f.setMetaData((FileMetaData) o[6]);
		}
		return f;
	}
	
	public FileInfo getFileContents(int fileId) {
		Object obj = getHibernateTemplate().find("select f.fileId, f.fileName, f.fileContents from FileInfo f where f.fileId ="+fileId).get(0);
		FileInfo f = new FileInfo();
		if(null != obj)
		{
			Object o[] = (Object[]) obj;
			f.setFileId(Integer.valueOf(o[0].toString()));
			f.setFileName(o[1].toString());
			f.setFileContents((byte[]) o[2]);
		}
		return f;
	}

	public List<Channel> getAllChannels() {
		List<Channel> channels = new ArrayList<Channel>();
		try {
			List<Object> objs = getHibernateTemplate().find("from Channel");
			for (Object o : objs) {
				Channel c = (Channel) o;
				channels.add(c);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return channels;
	}

}
