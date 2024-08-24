(function() {
	const LOGIN_URL = "CheckLogin";	
	const errorMessageElement = document.getElementById("errorMessage");
	
	document.getElementById("loginButton").addEventListener("click", (event) => {
		event.preventDefault();
		
		const form = event.target.closest("form");
		console.log(form);
		
		if(form.checkValidity()) {
			makeCall("POST", LOGIN_URL, form, function(request) {
				if(request.readyState === XMLHttpRequest.DONE) {
					const message = request.responseText;
					
					switch(request.status) {
						case 200: // OK
							sessionStorage.setItem("user", message);
							window.location.href = "home.html";
							break;
						
						case 400: // BAD REQUEST
						case 401: // UNAUTHORIZED
						case 500: // SERVER ERROR
							errorMessageElement.textContent = message;
							errorMessageElement.hidden = false;
							break;
					}
				}
			});
		} else {
			errorMessageElement.hidden = true;
			form.reportValidity();
		}
	});
})();