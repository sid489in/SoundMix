package com.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CHANNEL")
public class Channel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int channelId;

	@Column(name = "CHANNEL_NAME")
	private String channelName;

	@Column(name = "CHANNEL_DESCRIPTION")
	private String channelDescription;

	public Channel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelDescription() {
		return channelDescription;
	}

	public void setChannelDescription(String channelDescription) {
		this.channelDescription = channelDescription;
	}

	@Override
	public String toString() {
		return "Channel [channelId=" + channelId + ", channelName="
				+ channelName + ", channelDescription=" + channelDescription
				+ "]";
	}

}
