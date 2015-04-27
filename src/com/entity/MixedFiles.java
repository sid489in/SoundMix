package com.entity;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MIXED_FILES")
public class MixedFiles {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int fileId;

	@ManyToOne
	@JoinColumn(name = "SOURCE_FILE_ID")
	private FileInfo sourceFile;

	@ManyToOne
	@JoinColumn(name = "TARGET_FILE_ID")
	private FileInfo targetFile;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_SIZE")
	private String fileSize;

	@Column(name = "FILE_TYPE")
	private String fileType;

	@Lob
	@Column(name = "FILE_CONTENTS", columnDefinition = "blob")
	private byte[] fileContents;

	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name = "ID")
	private FileMetaData metaData;
	
	@Column(name ="FILE_PATH")
	private String filePath;

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public FileInfo getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(FileInfo sourceFile) {
		this.sourceFile = sourceFile;
	}

	public FileInfo getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(FileInfo targetFile) {
		this.targetFile = targetFile;
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

}
