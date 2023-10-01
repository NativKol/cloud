//gets elements
const emailEl = document.getElementById("email");
const usernameEl = document.getElementById("username");
const storageEl = document.getElementById("storage");

load_data()

function load_data()
{

    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '102');
    xhr.send();
    xhr.onreadystatechange = function() { 
      // If the request completed, close the extension popup
      if (xhr.readyState == 4)
      {
        console.log(xhr.status); 
        if (xhr.status != 200)
        {
          alert("Error");   
        }
        else
        {
          console.log(xhr.responseText);
          let json = JSON.parse(xhr.responseText);
          console.log(JSON.stringify(json, null, 4));
          
          for (var key in json)
          {
              var obj = json[key];
              emailEl.innerHTML =   "Email: "+ obj["email"] ;
              usernameEl.innerHTML =  "Username: " + obj["username"] ;
              let st = (parseInt(obj["StorageSize"]) / 1000).toString();
              storageEl.innerHTML =  "Max Storage Size: " + st + "MB" ;
          }
          
        }
      }
      document.getElementById("loading").remove();
    }
}

function home()
{
  location.href = "http://127.0.0.1:5500/homePage/IndexOwner.html"
}

function renameConfirm()
{
  submitEl = document.getElementById("userChange");
  console.log(submitEl.value)
  if (submitEl.value == "")
  {
    alert("field is empty");
  }
  else
  {
    var formData = new FormData();
    formData.append("username", submitEl.value);

    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '103');
    xhr.send(formData);
    xhr.onreadystatechange = function() { 
      // If the request completed, close the extension popup
      if (xhr.readyState == 4)
      {
        console.log(xhr.status); 
        if (xhr.status != 200)
        {
          alert("Error");   
        }
        else
        {
          home(); 
        }
      }
    }
  }
  
}

function signOut()
{
    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '107');
    xhr.send();
    xhr.onreadystatechange = function() { 
        // If the request completed, close the extension popup
        if (xhr.readyState == 4)
        {
            console.log(xhr.status); 
            if (xhr.status != 200)
            {
                alert("Error");   
            }
            else
            {
                location.href = "http://127.0.0.1:5500/loginPage/logIn.html"
            }
        }
    }
 
}