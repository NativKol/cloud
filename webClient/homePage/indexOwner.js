const fileEl = document.querySelector(".files");
const storageEl = document.getElementById("Storage");

let files = "";

const filesList = [];

// CHECK IF EXISTS (by get file)
var xhr = new XMLHttpRequest();
xhr.open("POST", "http://10.0.0.48:8081/", true);
xhr.setRequestHeader('Content-Type', 'multipart/form-data');
xhr.setRequestHeader('code', '104');
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
        console.log(obj["storage"]);
        let st = (parseInt(obj["storage"]) / 1000).toString();
        storageEl.innerHTML =  "<pre>" + st + "MB" + "\n" + "Free To Use" + "</pre>";;
      }
      
      sendFileReq();
    }
  }

}

function sendFileReq()
{
  // CHECK IF EXISTS (by get file)
  var xhr = new XMLHttpRequest();
  xhr.open("POST", "http://10.0.0.48:8081/", true);
  xhr.setRequestHeader('Content-Type', 'multipart/form-data');
  xhr.setRequestHeader('code', '306');
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
          //console.log(obj["file_name"]);
          filesList.push(obj["file_name"]);
        }
        for (let i = 0; i <filesList.length; i++) 
        {
          console.log(`<button id="` + filesList[i] + `" onclick="getFile()" class="files button1">` + filesList[i] + `</button>`);
          files += `<button id="` + filesList[i] + `" onclick="getFile()" class="files button1">` + filesList[i] + `</button>`;
        }
    
        let buttonList = document.querySelectorAll(".files");
        fileEl.innerHTML = files;
        buttonList.forEach(function(i){

          i.addEventListener("click", getFile)
        })
      }
    }
    document.getElementById("loading").remove();

  }
}



async function getFile(e) 
{
  let text = String(e.target.innerHTML);
  if (!(text.includes("<button")))
  {
    console.log(text);

    location.href = 'fileOwner.html?file_name=' + text;
  }
}