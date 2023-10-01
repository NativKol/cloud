//gets elements
const fileEl = document.getElementById("file");
const dateEl = document.getElementById("date");
const storageEl = document.getElementById("storage");
const groupsEl = document.querySelector(".groups");

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
          load_groups();
        }
      }
    }
}
    
function load_groups()
{
    // CHECK IF EXISTS (by get file)
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '210');
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
                    //console.log(obj["groupName"]);
                    groupList.set(obj["_id"], obj["groupName"]);
                }
                for (let [key, value] of groupList) 
                {
                    groups += `<button id="` + key + `" onclick="shareGroup()">` + value + `</button>`;
                }
            
                let buttonList = document.querySelectorAll(".groups");
                console.log(groups);
                groupsEl.innerHTML = "<a>Share To Group</a>" + groups;
                buttonList.forEach(function(i){

                    i.addEventListener("click", shareGroup)
                })

            }
        }
        document.getElementById("loading").remove();
    }
    
}

function home()
{
  location.href = "http://127.0.0.1:5500/homePage/IndexOwner.html"
}

function star()
{
  var xhr = new XMLHttpRequest();

  xhr.open("POST", "http://10.0.0.48:8081/", true);
  xhr.setRequestHeader('Content-Type', 'multipart/form-data');
  xhr.setRequestHeader('code', '302');
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
        home(); 
      }
    }
  }
}

function renameConfirm()
{
  submitEl = document.getElementById("fileChange");
  console.log(submitEl.value)
  if (submitEl.value == "")
  {
    alert("field is empty");
  }
  else
  {
    var formData = new FormData();
    formData.append("file_name", submitEl.value);

    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '303');
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

function shareConfirm()
{
  submitEl = document.getElementById("fileShare");
  console.log(submitEl.value)
  if (submitEl.value == "")
  {
    alert("field is empty");
  }
  else
  {
    var formData = new FormData();
    formData.append("share_email", submitEl.value);

    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '304');
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

async function shareGroup(e) 
{
  let text = String(e.target.innerHTML);
  if (!(text.includes("<button")))
  {
    console.log(text);
    console.log(e.target.id);

    let group_id = e.target.id

    if (group_id == "")
    {
      alert("field is empty");
    }
    else
    {
      var formData = new FormData();
      formData.append("group_id", group_id);

      var xhr = new XMLHttpRequest();

      xhr.open("POST", "http://10.0.0.48:8081/", true);
      xhr.setRequestHeader('Content-Type', 'multipart/form-data');
      xhr.setRequestHeader('code', '305');
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

    //share file to group

    //location.href = 'fileOwner.html?file_name=' + text;
  }
}