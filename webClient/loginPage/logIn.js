const wrongEl = document.querySelector(".content");

async function confirm() {
    const pasEl = document.querySelector(".pass").value;
    const emailEl = document.querySelector(".email").value;


    var formData = new FormData();
    //formData.append("code", "102");
    formData.append("email", emailEl);
    formData.append("password", pasEl);
  
    // CHECK IF EXISTS (by get file)
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '102');
    xhr.send(formData);

    xhr.onreadystatechange = function() { 
      // If the request completed, close the extension popup
      if (xhr.readyState == 4)
      {
        console.log(xhr.status); 
        if (xhr.status != 200)
        {
          wrongEl.innerHTML = `<a>Wrong password!</a>`; 
        }
        else
        {
          location.href = 'http://127.0.0.1:5500/homePage/indexOwner.html';
        }
      }
    }
    

    document.querySelector(".email").value = "";
    document.querySelector(".pass").value = "";

    return false;
}