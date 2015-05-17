define(
		[ 'app' ],
		function(musicApp) {
			return musicApp
					.factory(
							'musicApplicationService',
							function() {
								var baseUrl = location.href;
								return {
									
									getAllChannels : function() {
										var deferred = jQuery.Deferred();
										var self = this;
										jQuery
												.ajax({
													url : baseUrl + "services/allChannels",
													type : "GET",
													contentType : "application/json",
													success : function(result) {
														return deferred
																.resolve(result);
													},
													fail : function(result) {
														return deferred
														.reject(result);
													}
												});
										return deferred.promise();
									},

									getAllMixedFiles : function() {
										var deferred = jQuery.Deferred();
										jQuery
												.ajax({
													url : baseUrl + "services/allMixedFiles",
													type : "GET",
													contentType : "application/json",
													success : function(result) {
														return deferred
																.resolve(result);
													},
													fail : function(result) {
														alert(e);
														return deferred
														.reject(result);
													}
												});
										return deferred.promise();
									},
									
									deleteFile : function(file) {
										var deferred = jQuery.Deferred();
										jQuery
												.ajax({
													url : baseUrl + "services/deleteFile?fileId="+file.fileId,
													type : "DELETE",
													contentType : "application/json",
													success : function(result) {
														return deferred
																.resolve(result);
													},
													fail : function(result) {
														return deferred
														.reject(result);
													}
												});
										return deferred.promise();
									},
									
									getAllFiles : function() {
										var deferred = jQuery.Deferred();
										jQuery
												.ajax({
													url : baseUrl + "services/allFiles",
													type : "GET",
													contentType : "application/json",
													success : function(result) {
														return deferred
																.resolve(result);
													},
													fail : function(result) {
														alert(e);
														return deferred
														.reject(result);
													}
												});
										return deferred.promise();
									},

									getFileInfo : function(channel) {
										var deferred = jQuery.Deferred();
										jQuery
												.ajax({
													url : baseUrl + "services/file",
													type : "GET",
													contentType : "application/json",
													data : JSON
															.stringify(channel),
													success : function(result) {
														return deferred
																.resolve(result);
													},
													fail : function(result) {
														alert(e);
														return deferred
														.reject(result);
													}
												});
										return deferred.promise();
									},

									downloadFile : function(file) {
										window.location.href = baseUrl + "services/download?fileId="
												+ file.fileId+ "&fileName="+file.fileName;
									},

									mixSongs : function(fileIds) {
										var deferred = jQuery.Deferred();
										var changes = {
											"fileIds" : fileIds
										}
										jQuery
												.ajax({
													url : baseUrl + "services/mixSongs",
													type : "POST",
													contentType : "application/json",
													data : JSON
															.stringify(changes),
													success : function(result) {
														return deferred
																.resolve(result);
													},
													fail : function(result) {
														return deferred
														.reject(result);
													}
												});
										return deferred.promise();
									},
									uploadFile : function(file, channel) {
										var formData = new FormData();
										var fileInfo = {
											"fileName" : file.name,
											"fileSize" : file.size,
											"fileType" : file.type
										}
										formData.append('fileInfo', JSON
												.stringify(fileInfo));
										formData.append('fileData', file);
										formData.append('channel', JSON
												.stringify(channel));
										var deferred = jQuery.Deferred();
										$
												.ajax({
													url : baseUrl + 'services/uploadMusic', // Server
													// script
													// to
													// process
													// data
													type : 'POST',
													data : formData,
													cache : false,
													contentType : 'multipart/form-data',
													processData : false,
													success : function(data,
															textStatus, jqXHR) {
														deferred.resolve(data);
														alert(textStatus);
													},
													error : function(jqXHR,
															textStatus,
															errorThrown) {
														alert(textStatus);
													}
												});
										return deferred.promise();
									}
								}
							})
		});