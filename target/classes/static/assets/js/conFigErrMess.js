
var inputElement = document.querySelectorAll(".input")
var errorElement = document.querySelectorAll(".errorStr")
inputElement.forEach(myInputElement)

function myInputElement(item) {
	item.oninput = function() {
		var errorElement = document.querySelectorAll(".errorStr")
		errorElement.forEach(function(err) {
			err.innerText = "";
		})
	}
}