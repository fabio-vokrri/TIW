(function() {
	const REGISTRATION_URL = "CheckRegistration";
	
	document.getElementById("registerButton").addEventListener('click', (event) => {
		event.preventDefault();
		
		const form = event.target.closest("form");
		const errorMessageElement = document.getElementById("errorMessage");
		
		if(form.checkValidity()) {
			if(!checkMatchingPasswords()) {				
				errorMessageElement.textContent = "Inserted passwords do not match";
				errorMessageElement.hidden = false
				
				return false;
			}
			
			makeCall("POST", REGISTRATION_URL, form, function(request) {
				if(request.readyState === XMLHttpRequest.DONE) {
					const message = request.responseText;
					
					switch(request.status) {
						case 200:
							window.location.href = "login.html";
							break;
						case 400: // BAD REQUEST
						case 401: // UNAUTHORIZED
						case 409: // CONFLICT
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
	
	
	function checkMatchingPasswords() {
		let password1 = document.getElementById("password1").value;
		let password2 = document.getElementById("password2").value;
		
		if(password1 != null && password2 != null && password1 !== "" && password2 !== "") {
			return password1.match(password2);
		}
		
		return false;
	}
})();