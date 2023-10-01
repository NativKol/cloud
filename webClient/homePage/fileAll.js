//gets elements
const fileEl = document.getElementById("file");
const dateEl = document.getElementById("date");
const storageEl = document.getElementById("storage");

//gets star
let root = document.documentElement;
var style = getComputedStyle(document.body)

let groups = "";
var groupList = new Map()

//get param from url
var url_string = window.location.href; 
var url = new URL(url_string);
var c = url.searchParams.get("file_name");
console.log(c);

load_data(c)

function load_data(file)
{
  var formData = new FormData();
    formData.append("file_name", file);

    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '307');
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
          console.log(xhr.responseText);
          let json = JSON.parse(xhr.responseText);
          console.log(JSON.stringify(json, null, 4));
          for (var key in json)
          {
              var obj = json[key];
              let st = (parseInt(obj["sizeFile"]) / 1000).toString();
              storageEl.innerHTML =  st  + "MB" ;
              dateEl.innerHTML =  obj["date"] ;
              fileEl.innerHTML =  obj["file_name"] + "." + obj["type"] ;
              if (obj["star"] == "false")
              {
                  root.style.setProperty('--white', '#FFFFFF');
              }
              else
              {
                  root.style.setProperty('--white', '#FFBF00');
              }
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