package com.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "MIXED_FILES")
public class MixedFiles {

	@Id
	@Column(name="MIXED_FILE_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int fileId;

	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private FileInfo channel1;
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private FileInfo channel2;
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private FileInfo channel3;
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private FileInfo channel4;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_SIZE")
	private String fileSize;
	
	@Column(name = "DURATION")
	private String duration;

	@Column(name = "FILE_TYPE")
	private String fileType;

	@Lob
	@Column(name = "FILE_CONTENTS", columnDefinition = "blob")
	private byte[] fileContents;

	@OneToOne(cascade=CascadeType.PERSIST,fetch=FetchType.EAGER)
	@JoinColumn(name = "METADATA_ID")
	private FileMetaData metaData;
	
	@Column(name ="FILE_PATH")
	private String filePath;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATION_DATE")
	private Date creationDate;
	
	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public FileInfo getChannel1() {
		return channel1;
	}

	public void setChannel1(FileInfo channel1) {
		this.channel1 = channel1;
	}

	public FileInfo getChannel2() {
		return channel2;
	}

	public void setChannel2(FileInfo channel2) {
		this.channel2 = channel2;
	}

	public FileInfo getChannel3() {
		return channel3;
	}

	public void setChannel3(FileInfo channel3) {
		this.channel3 = channel3;
	}

	public FileInfo getChannel4() {
		return channel4;
	}

	public void setChannel4(FileInfo channel4) {
		this.channel4 = channel4;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public byte[] getFileContents() {
		return fileContents;
	}

	public void setFileContents(byte[] fileContents) {
		this.fileContents = fileContents;
	}

	public FileMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(FileMetaData metaData) {
		this.metaData = metaData;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	
}
