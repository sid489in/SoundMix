define(
		[ 'angular', 'datatables', 'dataTablesBootstrap', 'app',
				'services/MusicApplicationService' ],
		function(angular, app, musicApplicationService) {

			var musicApp = angular.module('soundMix');

			var MusicApplicationController = function($scope,
					musicApplicationService, $location) {
				this.scope = $scope;
				this.location = $location;
				this.musicApplicationService = musicApplicationService;
				this.scope.showDialog = "false";
				var self = this;
				this.title = " Upload Music";
				this.mixedFiles = [];
				this.files = [];
				this.addedFiles = [];
				var self = this;
				this.fileIds = [];
				this.musicApplicationService.getAllChannels().done(
						function(data) {
							self.channels = data;
							self.scope.$apply();
						}).fail(function(e) {
							
						});

				if (!window.AudioContext) {
					if (!window.webkitAudioContext) {
						alert("Your browser does not support any AudioContext and cannot play back this audio.");
						return;
					}
					window.AudioContext = window.webkitAudioContext;
				}

				this.context = new AudioContext();
				var options = {
						"bStateSave" : true,
						"iCookieDuration" : 2419200, /* 1 month */
						"bJQueryUI" : true,
						"bPaginate" : true,
						"bLengthChange" : false,
						"bFilter" : true,
						"bInfo" : true,
						"bDestroy" : true
					};
				this.musicInfoTable = $('#example').DataTable({
				        "columnDefs": [
				            {
				                "targets": [ 0 ],
				                "visible": false,
				                "searchable": false
				            }],
				            aaSorting : [[4, 'desc']]
				});
				
				var self = this;
				$('#example tbody').on( 'click', 'tr', function () {
			    	var data = self.musicInfoTable.row( this ).data();
			    	
			    	var idx = jQuery.inArray(data[0], self.fileIds);
			        if ( $(this).hasClass('highlight') ) {
			        	self.fileIds.splice(idx,1);
			            $(this).removeClass('highlight');
			        }
			        else {
			          //  table.$('tr.highlight').removeClass('selected');
			        	self.fileIds.push(data[0]);
			            $(this).addClass('highlight');
			        }
			        self.scope.$apply();
			    } );
			
				this.mixedFileTable = $('#example1').DataTable({
					"columnDefs": [
						            {
						                "targets": [ 0 ],
						                "visible": false,
						                "searchable": false
						            },
						            {
						                "targets": -1,
						                "data": null,
						                "defaultContent": "<center><button>Download</button><center>"
						            }],
					aaSorting : [[3, 'desc']]
				});
				
				$('#example1 tbody').on( 'click', 'button', function () {
			        var data = self.mixedFileTable.row( $(this).parents('tr') ).data();
			        self.downloadFile(data[4]);
			    } );

			};

			MusicApplicationController.prototype = {
				selectMusicFile : function(nRow, aData, iDisplayIndex,
						iDisplayIndexFull) {
					var self = this;
					$('td:eq(4)', nRow).bind('click', function() {
		                $scope.$apply(function() {
		                    this.someClickHandler(aData);
		                });
		            });
		            return nRow;
				},
				
				someClickHandler : function(info) {
		            $scope.message = 'clicked: '+ info.id;
		            alert(info)
		        },

				navigateToSection : function(sectionId, channel) {
					this.fileIds = [];
					var url = window.location.href.substr(0,
							window.location.href.lastIndexOf("#"))
					if (url) {
						url = url + "#" + sectionId;
					} else {
						url = window.location.href + "#" + sectionId;
					}
					var self = this;
					if (sectionId == 'section1') {
						this.musicApplicationService.getFileInfo(channel).done(
								function(data) {
									self.fileInfo = data;
									self.scope.$apply();
								})
						window.location.href = url;
					} else if (sectionId == 'section2') {
						$(".modal").fadeIn();
						this.musicApplicationService.getAllFiles().done(
								function(data) {
									self.files = data;
									$('#example').dataTable().fnClearTable();
									   $.each(data, function(index, data) {     
			                                  //!!!--Here is the main catch------>fnAddData
			                                  $('#example').dataTable().fnAddData( [  data.fileId,	
			                                                                          data.fileName,
			                                                                          "mp3/wav",
			                                                                          data.duration,
			                                                                          data.fileSize ,
			                                                                          data.creationDate]
			                                                                        );      

			                                });
									self.scope.$apply();
									$(".modal").fadeOut();
								}).fail(function(e) {
									$(".modal").fadeOut();
								});
						// window.location.href = url;
						var self = this;
					     
					} else if (sectionId == 'collection') {
						$(".modal").fadeIn();
						$('#example1').dataTable().fnClearTable();
						this.musicApplicationService.getAllMixedFiles().done(
								function(data) {
									self.mixedFiles = data;
									$.each(data, function(index, data) {     
		                                  //!!!--Here is the main catch------>fnAddData
		                                  $('#example1').dataTable().fnAddData( [  data.fileId,	
		                                                                          data.fileName,
		                                                                          data.duration,
		                                                                          data.fileSize,
		                                                                          data.creationDate, data ]
		                                                                        );      

		                                });
   
									self.scope.$apply();
									$(".modal").fadeOut();
								}).fail(function(e) {
									$(".modal").fadeOut();
								});;
					}
				},

				addSelected : function(file) {
					file.added = true;
					this.fileIds.push(file.fileId);
					self.scope.$apply();
				},

				setLoading : function(loading) {
					this.isLoading = true;
				},

				removeSelected : function(file) {
					file.added = false;
					for (var i = 0; i < fileIds.length; i++) {
						if (file.fileId == filesIds[i]) {
							fileIds.splice(i, 1);
						}
					}
					self.scope.$apply();
				},

				mixFiles : function() {
					var self = this;
					this.mixedFile = null;
					$(".modal").fadeIn();
					this.musicApplicationService.mixSongs(this.fileIds).done(
							function(result) {
								var file = result;
								self.mixedFiles.push(file);
								self.mixedFile = file;
								$(".modal").fadeOut();
								self.scope.$apply();

							}).fail(function(e) {
								$(".modal").fadeOut();
							});
				},

				updateDataTable : function() {
					this.scope.options = {
						aoColumns : [ {
							"sTitle" : "File Name"
						}, {
							"sTitle" : "Source File"
						}, {
							"sTitle" : "Target File"
						}, {
							"sTitle" : "Duration File"
						}, {
							"sTitle" : "Action"
						} ],

						aoColumnDefs : [ {
							"bSortable" : false,
							"aTargets" : [ 0, 1, 2, 3 ]
						} ],
						bJQueryUI : true,
						bDestroy : true,
						aaData : this.mixedFiles
					};
				},
				downloadFile : function(file) {
					var self = this;
					if (file) {
						this.musicApplicationService.downloadFile(file);
					} else {
						this.musicApplicationService
								.downloadFile(this.mixedFile);
					}

				},

				deleteFile : function(file) {
					var self = this;
					if (file) {
						$(".modal").fadeIn();
						this.musicApplicationService
								.deleteFile(file)
								.done(
										function() {
											$
													.each(
															self.mixedFiles,
															function(i) {
																if (self.mixedFiles[i].fileId === file.fileId) {
																	self.mixedFiles
																			.splice(
																					i,
																					1);
																	return false;
																}
															});
											self.scope.$apply();
											$(".modal").fadeOut();
										});
					}

				},

				uploadFile : function() {
					var self = this;
					var file = this.scope.musicCtrl.myFile;
					var channel = this.channel;
					self.closeMusicDialog();
					$(".modal").fadeIn();
					this.musicApplicationService.uploadFile(file, channel)
							.done(function(data) {
								self.fileInfo = JSON.parse(data);
								self.addedFiles.push(self.fileInfo);
								self.scope.musicCtrl.myFile = null;
								$(".modal").fadeOut();
								self.scope.$apply()
							}).fail(function(e) {
								$(".modal").fadeOut();
							});;
				},

				playByteArray : function(file) {
					var self = this;
					var byteArr = self.base64DecToArr(file.fileContent);
					var arrayBuffer = new ArrayBuffer(byteArr.length);
					var bufferView = new Uint8Array(arrayBuffer);
					for (i = 0; i < byteArr.length; i++) {
						bufferView[i] = byteArr[i];
					}

					self.context.decodeAudioData(arrayBuffer, function(buffer) {
						self.buf = buffer;
						self.play();
					});
				},

				// Play the loaded file
				play : function() {
					// Create a source node from the buffer
					this.source = this.context.createBufferSource();
					this.source.buffer = this.buf;
					// Connect to the final output node (the speakers)
					this.source.connect(this.context.destination);
					// Play immediately
					this.source.start(0);
				},

				changeVolume : function(element) {
					var volume = element.value;
					var fraction = parseInt(element.value)
							/ parseInt(element.max);
					// Let's use an x*x curve (x-squared) since simple linear
					// (x) does not
					// sound as good.
					this.gainNode.gain.value = fraction * fraction;
				},

				stop : function() {
					this.source.stop(0);
				},

				toggle : function(file) {
					if (!this.playing) {
						this.playByteArray(file);
					} else {
						this.stop();
					}
					this.playing = !this.playing;

				},
				// from mozilla
				b64ToUint6 : function(nChr) {

					return nChr > 64 && nChr < 91 ? nChr - 65 : nChr > 96
							&& nChr < 123 ? nChr - 71
							: nChr > 47 && nChr < 58 ? nChr + 4
									: nChr === 43 ? 62 : nChr === 47 ? 63 : 0;

				},

				base64DecToArr : function(sBase64, nBlocksSize) {

					var sB64Enc = sBase64.replace(/[^A-Za-z0-9\+\/]/g, ""), nInLen = sB64Enc.length, nOutLen = nBlocksSize ? Math
							.ceil((nInLen * 3 + 1 >> 2) / nBlocksSize)
							* nBlocksSize
							: nInLen * 3 + 1 >> 2, taBytes = new Uint8Array(
							nOutLen);

					for (var nMod3, nMod4, nUint24 = 0, nOutIdx = 0, nInIdx = 0; nInIdx < nInLen; nInIdx++) {
						nMod4 = nInIdx & 3;
						nUint24 |= this.b64ToUint6(sB64Enc.charCodeAt(nInIdx)) << 18 - 6 * nMod4;
						if (nMod4 === 3 || nInLen - nInIdx === 1) {
							for (nMod3 = 0; nMod3 < 3 && nOutIdx < nOutLen; nMod3++, nOutIdx++) {
								taBytes[nOutIdx] = nUint24 >>> (16 >>> nMod3 & 24) & 255;
							}
							nUint24 = 0;

						}
					}

					return taBytes;
				},

				closeMusicDialog : function() {
					this.showDialog = false;
				},

				openMusicDialog : function() {
					this.title = "Upload Music"
					this.showDialog = true;
				},

			};

			musicApp.controller('MusicApplicationController',
					MusicApplicationController);
		});