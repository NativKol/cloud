const wrongEl = document.querySelector(".content");


async function confirm() {
    const pasEl = document.querySelector(".pass").value;
    const emailEl = document.querySelector(".email").value;
    const userEl = document.querySelector(".username").value;

    if (pasEl.length > 30 || userEl.length > 30 || emailEl.length > 30)
    {
      wrongEl.innerHTML = `Not valid input..`;   
    }
    else
    {
      //pass id
      var formData = new FormData();
      formData.append("email", emailEl);
      formData.append("username", userEl);
      formData.append("password", pasEl);
      formData.append("storageSize", "1000000");
      

      var xhr = new XMLHttpRequest();

      xhr.open("POST", "http://10.0.0.48:8081/", true);
      xhr.setRequestHeader('Content-Type', 'multipart/form-data');
      xhr.setRequestHeader('code', '101');
      xhr.send(formData);
      xhr.onreadystatechange = function() { 
        // If the request completed, close the extension popup
        if (xhr.readyState == 4)
        {
          console.log(xhr.status); 
          if (xhr.status != 200)
          {
            wrongEl.innerHTML = `Error`;   
          }
          else
          {
            location.href = 'http://127.0.0.1:5500/homePage/indexOwner.html';
          }
        }
      }

    document.querySelector(".email").value = "";
    document.querySelector(".pass").value = "";
    document.querySelector(".username").value = "";

    return false;
  }
}