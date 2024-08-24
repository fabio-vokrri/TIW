function makeCall(method, url, formElement, callBack, reset = true) {
    const request = new XMLHttpRequest(); 
    
    request.onreadystatechange = function() {
		callBack(request)
  	};
    
    request.open(method, url);

    if (formElement == null) {
      	request.send();
    } else {
		const formData = new FormData(formElement);
      	request.send(formData);
      
      	if (reset) {
      		formElement.reset();
      	}
    }
}