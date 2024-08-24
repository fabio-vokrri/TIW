(function() {
	
	let homePage, documentPage, addFolderDialog, addDocumentDialog, deleteDialog, errorDialog;
	
	const GET_FOLDER_AND_DOCUMENTS_URL 	= "GetFoldersAndDocuments";
	const CREATE_FOLDER_URL 			= "CreateFolder";
	const CREATE_DOCUMENT_URL 			= "CreateDocument";
	const DELETE_FOLDER_URL 			= "DeleteFolder";
	const DELETE_DOCUMENT_URL 			= "DeleteDocument";
	const MOVE_FOLDER_URL 				= "MoveFolder";
	const MOVE_DOCUMENT_URL 			= "MoveDocument";
	const GET_DOCUMENT_DETAILS_URL 		= "GetDocumentDetails"
	
	window.addEventListener("load", () => {
		const userJson = sessionStorage.getItem("user");
		
		if(userJson == null) {
			window.location.href = "login.html";
		} else {
			const headerTitle = document.getElementById("headerTitle");			
			const rootFolder = document.getElementById("rootFolder");

			const user = JSON.parse(userJson);
			homePage = new HomePage(
				document.getElementById("homePage"), 
				headerTitle, 
				rootFolder, 
				user.userName
			);
			homePage.reset();
			homePage.show();
			
			documentPage = new DocumentPage(
				document.getElementById("documentPage"), 
				headerTitle
			);
			documentPage.reset();
			
			addFolderDialog = new AddFolderDialog(
				document.getElementById("addFolderDialog")
			).init();
			
			addDocumentDialog = new AddDocumentDialog(
				document.getElementById("addDocumentDialog")
			).init();
			
			deleteDialog = new DeleteDialog(
				document.getElementById("deleteDialog")
			);	
			
			document.getElementById("logoutButton").addEventListener("click", () => logOut());
			document.getElementById("addToRoot").addEventListener("click", () => addFolderDialog.show());
			
			const deleteZone = document.getElementById("deleteZone");
			deleteZone.addEventListener("drop", (event) => dropDeleteHandler(event));
			deleteZone.addEventListener("dragover", (event) => dragOverHandler(event));
			
			errorDialog = document.getElementById("errorDialog");		
		}
	}, false);
	
	/********************** HOME PAGE **********************/
	function HomePage(homePageBody, headerTitle, rootFolder, userName) {
		this.userName = userName
		
		this.homePageBody = homePageBody;
		this.headerTitle = headerTitle;
		this.rootFolder = rootFolder;
		
		this.show = function() {
			const self = this;			
			this.headerTitle.textContent = `Welcome ${userName}!`;
			makeCall("GET", GET_FOLDER_AND_DOCUMENTS_URL, null, function(request) {
				if(request.readyState === XMLHttpRequest.DONE) {
					const message = request.responseText;
					
					if(request.status == 200) { // OK
						const folders = JSON.parse(message);
						self.update(folders);
					} else if(request.status == 403) { // FORBIDDEN
						logOut();
					} else {
						showErrorDialog(message);
					}
				}
			});
		}
		
		this.update = function(folders) {
			const title = document.getElementById("homeTitle");
			
			this.rootFolder.innerHTML = "";
			
			if(folders.length != 0) {
				title.textContent = "Your documents and folders";
				
				for(const folder of folders) {
					this.renderFolder(folder, rootFolder);
				}
			} else {
				title.textContent = "No Folders or Documents found!"
			}
			
			this.homePageBody.classList.remove("hidden");
		}
		
		this.renderFolder = function(folder, parentFolder) {
			const folderLi = document.createElement("li");
			folderLi.setAttribute("id", `folder?id=${folder.id}`);
			folderLi.setAttribute("draggable", true);
			folderLi.textContent = folder.name;
			
			// adds drag and drop functionality
			folderLi.addEventListener("dragstart", (event) => dragStartHandler(event));
			folderLi.addEventListener("dragover",  (event) => dragOverHandler(event));
			folderLi.addEventListener("drop",      (event) => dropHandler(event));
			
			const addFolderLink = document.createElement("a");
			addFolderLink.textContent = ">add Folder";
			addFolderLink.addEventListener("click", () => addFolderDialog.show(folder));
			folderLi.appendChild(addFolderLink)
			
			const addDocumentLink = document.createElement("a");
			addDocumentLink.textContent = ">add Document";
			addDocumentLink.addEventListener("click", () => addDocumentDialog.show(folder));
			folderLi.appendChild(addDocumentLink);
			
			parentFolder.appendChild(folderLi);
			
			// creates the documents tree
			if(folder.documents.length != 0) {
				const documentUl = document.createElement("ul");
				documentUl.setAttribute("id", `documents?id=${folder.id}`)
				folderLi.appendChild(documentUl);
				
				for(const subDocument of folder.documents) {
					const documentLi = document.createElement("li");
					documentLi.setAttribute("id", `document?id=${subDocument.id}`);
					documentLi.setAttribute("draggable", "true");
					documentLi.textContent = subDocument.name;
					documentLi.addEventListener("dragstart", (event) => dragStartHandler(event));
					
					const documentDetailsLink = document.createElement("a");
					documentDetailsLink.textContent = ">view";
					documentDetailsLink.addEventListener("click", () => {
						this.reset();
						documentPage.reset();
						documentPage.show(subDocument.id);
					});
					
					documentLi.appendChild(documentDetailsLink);					
					documentUl.appendChild(documentLi);
				}
			}
			
			// creates the subfolders tree (recursively)
			if(folder.subFolders.length != 0) {
				const subFolderUl = document.createElement("ul");
				subFolderUl.setAttribute("id", `subfolders?id${folder.id}`);
				folderLi.appendChild(subFolderUl);
				
				for(const subFolder of folder.subFolders) {
					this.renderFolder(subFolder, subFolderUl);
				}
			}
		}
		
		this.reset = function() {
			this.homePageBody.classList.add("hidden");
		}
	}
	
	/********************** DOCUMENT PAGE **********************/
	function DocumentPage(documentPageBody, headerTitle, errorMessage) {
		this.documentPageBody = documentPageBody;
		this.headerTitle = headerTitle; 
		this.errorMessage = errorMessage;
		
		this.show = function(documentId) {
			const self = this;
			
			makeCall("GET", `${GET_DOCUMENT_DETAILS_URL}?documentId=${documentId}`, null, function(request) {
				if(request.readyState === XMLHttpRequest.DONE) {
					const message = request.responseText;
					
					if(request.status == 200) { // OK
						const documentInfo = JSON.parse(message);
						self.update(documentInfo);
					} else if(request.status == 403) { // FORBIDDEN
						logOut();
					} else {
						showErrorDialog(message);
					}
				}
			});			
		}
		
		this.update = function(documentInfo) {
			const backButton = document.getElementById("backButton");
			
			this.headerTitle.textContent = `${documentInfo.name}.${documentInfo.type}`;
			backButton.classList.remove("hidden");
			backButton.addEventListener("click", () => {
				this.reset();
				homePage.show();
				backButton.classList.add("hidden");
			});
			
			const documentDate = document.getElementById("documentDate");
			documentDate.textContent = `Created on ${documentInfo.date}`;
			
			const documentContent = document.getElementById("documentContent");
			documentContent.textContent = documentInfo.summary;
			
			this.documentPageBody.classList.remove("hidden");
		}
		
		this.reset = function() {
			this.documentPageBody.classList.add("hidden");
		}
	}
	
	/********************** FOLDER DIALOG **********************/
	function AddFolderDialog(addFolderDialog) {
		this.addFolderDialog = addFolderDialog;
		
		this.init = function() {
			document.getElementById("addFolderButton").addEventListener("click", (event) => {
				event.preventDefault();
				const self = this;
				
				const form = event.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", CREATE_FOLDER_URL, form, function(request) {
						if (request.readyState === XMLHttpRequest.DONE) {
							const message = request.responseText;
							
							if(request.status != 200) {
								showErrorDialog(message);
							}
							
							homePage.show();
							self.addFolderDialog.close();
						} else {
							form.reportValidity();
						}
					});
				}
			});
			
			return this;
		}
		
		this.show = function(parentFolder = null) {
			const parentIdInput = document.getElementById("parentIdInput-fold");
			const dialogTitle = document.getElementById("dialogTitle-fold");
			
			if(parentFolder == null) {
				parentIdInput.value = -1;
				dialogTitle.textContent = "Add to root";
			} else {
				parentIdInput.value = parentFolder.id;
				dialogTitle.textContent = "Add to " + parentFolder.name;
			}
			this.addFolderDialog.showModal();
		}
	}
	
	/********************** DOCUMENT DIALOG **********************/
	function AddDocumentDialog(addDocumentDialog) {
		this.addDocumentDialog = addDocumentDialog;
		
		this.init = function() {
			document.getElementById("addDocumentButton").addEventListener("click", (event) => {
				event.preventDefault();
				const self = this;
				
				const form = event.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", CREATE_DOCUMENT_URL, form, function(request) {
						if (request.readyState === XMLHttpRequest.DONE) {
							const message = request.responseText;
							
							if(request.status != 200) {
								showErrorDialog(message);
							}
							
							homePage.show();
							self.addDocumentDialog.close();
						} else {
							form.reportValidity();
						}
					});
				}
			});
			
			return this;
		}
		
		this.show = function(parentFolder) {
			document.getElementById("parentIdInput-doc").value = parentFolder.id;
			document.getElementById("dialogTitle-doc").textContent = "Add to " + parentFolder.name;
			this.addDocumentDialog.showModal();			
		}
	}
	
	/********************** DELETE FOLDER DIALOG **********************/
	function DeleteDialog(deleteDialog) {
		this.deleteDialog = deleteDialog;
		
		this.show = function({folderId = null, documentId = null}) {
			if (folderId != null && documentId != null) {
				showErrorDialog("Something is not right: both folder id and document id were specified");
				return;
			}
			
			if(folderId == null && documentId == null) {
				showErrorDialog("Something is not right: neither folder id nor document id were specified");
				return;
			}
			
			const deleteDialogTitle = document.getElementById("deleteDialogTitle");
			deleteDialogTitle.textContent = "Are you sure you want to delete this "
			folderId == null ? deleteDialogTitle.textContent += "document?" : deleteDialogTitle.textContent += "folder?";
		
			
			const self = this;
			
			document.getElementById("cancelButton").addEventListener("click", () => this.deleteDialog.close());;
			document.getElementById("deleteButton").addEventListener("click", () => {
				let url = folderId != null ? `${DELETE_FOLDER_URL}?folderId=${folderId}` : `${DELETE_DOCUMENT_URL}?documentId=${documentId}`
				
				makeCall("POST", url, null, function(request) {
					if (request.readyState === XMLHttpRequest.DONE) {
						const message = request.responseText;
						
						if(request.status != 200) {
							showErrorDialog(message);
						}
						
						homePage.show();
						self.deleteDialog.close();
					}
				});
				
			});
			
			this.deleteDialog.showModal();
		}
	}
	
	function logOut() {
		sessionStorage.removeItem("user");
		window.location.href = "login.html";
	}
	
	function dragStartHandler(event) {
		event.dataTransfer.setData("text/plain", event.target.id);
		event.dataTransfer.effectAllowed = "move";
	}
	
	function dragOverHandler(event) {
		event.preventDefault();
		event.dataTransfer.dropEffect  = "move";
	}
	
	function dropDeleteHandler(event) {
		event.preventDefault();
		
		const {isDocument, id} = getIdFrom(event.dataTransfer.getData("text/plain"));		
		isDocument ? deleteDialog.show({documentId : id}) : deleteDialog.show({folderId : id});
	}
	
	function dropHandler(event) {
		event.preventDefault();
		
		const {isDocument: isSourceDocument, id: sourceId} = getIdFrom(event.dataTransfer.getData("text/plain"));
		const {isDocument: isDestinationDocument, id: destinationId} = getIdFrom(event.target.id);
		
		if (isDestinationDocument) {
			showErrorDialog("Cannot move a folder or document into another document");
			return;
		}
		
		const url = isSourceDocument ? `${MOVE_DOCUMENT_URL}?documentId=${sourceId}&folderId=${destinationId}` : `${MOVE_FOLDER_URL}?sourceFolderId=${sourceId}&destinationFolderId=${destinationId}`
		
		makeCall("POST", url, null, function(request) {
			if (request.readyState === XMLHttpRequest.DONE) {
				const message = request.responseText;
				
				if(request.status != 200) {
					showErrorDialog(message);
				}
				
				homePage.show();
			}
		});
	}
	
	function getIdFrom(data) {
		// the id of li elements are as follows:
		// document?id=## or folder?id=##
		const destination = data.split("?");
		const id = destination[1].split("=")[1];
		
		return {
			isDocument : destination[0] === "document",
			id : id,
		}
	}
	
	
	function showErrorDialog(message) {
		document.getElementById("errorMessage").textContent = message;
		document.getElementById("errorDialogButton").addEventListener("click", () => errorDialog.close());
		errorDialog.showModal();
	}
})();
