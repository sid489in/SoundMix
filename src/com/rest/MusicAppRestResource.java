package com.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.service.MusicServiceImpl;
import com.util.JsonMarshaller;

@Path("/")
public class MusicAppRestResource {

	private static int i = 0;
	@Autowired
	private MusicServiceImpl musicServiceImpl;

	JsonMarshaller marshaller = new JsonMarshaller();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("allChannels")
	public Response getAllChannels() {
		try {
			JsonArray channels = musicServiceImpl.getChannels();
			return Response.ok(channels.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("allFiles")
	public Response getAllFiles() {
		try {
			JsonArray files = musicServiceImpl.getAllFiles();
			return Response.ok(files.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("allMixedFiles")
	public Response getAllMixedFiles() {
		try {
			JsonArray files = musicServiceImpl.getAllMixedFiles();
			return Response.ok(files.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("file")
	public Response getFile(String postedData) {
		try {
			JsonObject channelJson = marshaller.parseJson(postedData);
			JsonObject fileJson = musicServiceImpl.getFile(channelJson);
			return Response.ok(fileJson.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("mixSongs")
	public Response mixSongs(String postedData) {
		try {
			JsonObject fileIds = marshaller.parseJson(postedData);
			JsonObject fileJson = musicServiceImpl.mixSongs(fileIds);
			return Response.ok(fileJson.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@QueryParam("fileId") String fileId) {
	    String filePath = musicServiceImpl.getMixedFilePath(fileId);
	    File file = new File(filePath);
	    ResponseBuilder response = Response.ok((Object) file);
	    response.header("Content-Disposition",
	        "attachment; filename="+ file.getName());
	    return response.build();

	}
	
	@POST
	@Path("uploadMusic")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@Multipart("fileInfo") String file,
			@Multipart(value = "fileData") InputStream stream,
			@Multipart("channel") String channel) {
		JsonObject fileInfo = new JsonObject();
		JsonObject fileDetails = marshaller.parseJson(file);
		System.out.println("FILE iS");
		if (channel != null) {
			JsonObject channelJson = marshaller.parseJson(channel);
			System.out.println("Channel is" + channelJson);
			System.out.println("Upload data" + stream);
			fileInfo = musicServiceImpl.uploadFile(stream, fileDetails,
					channelJson);
		}
		return Response.status(200).entity(fileInfo.toString()).build();
	}
}
