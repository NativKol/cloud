//gets elements
const nameEl = document.getElementById("groupName");
const parEl = document.querySelector(".participants");

//gets star
let root = document.documentElement;
var style = getComputedStyle(document.body)

let pars = "";

//get param from url
var url_string = window.location.href; 
var url = new URL(url_string);
var c = url.searchParams.get("group_id");
console.log(c);

load_data(c)
    
function load_data(id)
{
    var formData = new FormData();
    formData.append("group_id", id);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '212');
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
                    nameEl.innerHTML = obj["groupName"];
                    for (let i = 0; i <obj["participants"].length; i++) 
                    {
                        console.log(obj["participants"][i]);
                        console.log(`<button id="` + obj["participants"][i] + `" onclick="kickPar()">` + obj["participants"][i] + `</button>`);
                        pars += `<button id="` + obj["participants"][i] + `" onclick="kickPar()">` + obj["participants"][i] + `</button>`;
                    }
                    let buttonList = document.querySelectorAll(".participants");
                    parEl.innerHTML = "<a>Participants:</a>" + pars;
                    buttonList.forEach(function(i){

                    i.addEventListener("click", kickPar)
                    })
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

function back()
{
  location.href = "http://127.0.0.1:5500/groupPage/indexGroup.html"
}

function leave()
{
    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '206');
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
          back(); 
        }
      }
    }
}
