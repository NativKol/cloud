//gets elements
const ownerEl = document.getElementById("groupsOwner");
const inviteEl = document.getElementById("groupsInvited");
const allEl = document.getElementById("groupsAll");
console.log(ownerEl);
console.log(inviteEl);
console.log(allEl);

let owners = "";
var ownerList = new Map()

let alls = "";
var allList = new Map()

let invites = "";
var inviteList = new Map()


load_owner();
    
function load_owner()
{
    // CHECK IF EXISTS (by get file)
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '209');
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
                    ownerList.set(obj["_id"], obj["groupName"]);
                }
                for (let [key, value] of ownerList) 
                {
                    owners += `<button id="` + key + `" onclick="openOwner()">` + value + `</button>`;
                }

                if (owners != "")
                {
                    let buttonList = document.querySelectorAll(".groupsOwner");
                    console.log(owners);
                    ownerEl.innerHTML = "<a>My Groups</a>" + owners;
                    buttonList.forEach(function(i){

                        i.addEventListener("click", openOwner)
                    })
                }
            
                load_all();
            }
        }
    }
    
}


function load_all()
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
                    allList.set(obj["_id"], obj["groupName"]);
                }
                for (let [key, value] of allList) 
                {
                    alls += `<button id="` + key + `" onclick="openAll()">` + value + `</button>`;
                }
            
                let buttonList = document.querySelectorAll(".groupsAll");
                console.log(alls);
                if (alls != "")
                {
                    allEl.innerHTML = "<a>All Groups</a>" + alls;
                    buttonList.forEach(function(i){
    
                        i.addEventListener("click", openAll)
                    })
                }

                load_invite();
            }
        }
    }
    
}

function load_invite()
{
    // CHECK IF EXISTS (by get file)
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '211');
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
                if (xhr.responseText != null)
                {
                    let json = JSON.parse(xhr.responseText);
                    console.log(JSON.stringify(json, null, 4));
                    for (var key in json)
                    {
                        var obj = json[key];
                        //console.log(obj["groupName"]);
                        inviteList.set(obj["_id"], obj["groupName"]);
                    }
                    for (let [key, value] of inviteList) 
                    {
                        invites += `<button id="` + key + `" onclick="openInvite()">` + value + `</button>`;
                    }

                    if (invites != "")
                    {
                        let buttonList = document.querySelectorAll(".groupsInvited");
                        console.log(invites);
    
                        inviteEl.innerHTML = "<a>Invites</a>" + invites;
                        buttonList.forEach(function(i){
        
                            i.addEventListener("click", openInvite)
                        })
                    }
                
                }
            }
        }
        document.getElementById("loading").remove();
    }
    
}


async function openOwner(e) 
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
            location.href = 'groupOwner.html?group_id=' + group_id;
        }

    }
    
}


async function openAll(e) 
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
            location.href = 'groupAll.html?group_id=' + group_id;
        }

    }
    
}

async function openInvite(e) 
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
            location.href = 'groupInvite.html?group_id=' + group_id;
        }

    }
    
}

function addConfirm()
{
  submitEl = document.getElementById("add");
  console.log(submitEl.value)
  if (submitEl.value == "")
  {
    alert("field is empty");
  }
  else
  {
    var formData = new FormData();
    formData.append("groupName", submitEl.value);

    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '201');
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
          back(); 
        }
      }
    }
  }
}

function back()
{
  location.href = "http://127.0.0.1:5500/groupPage/indexGroup.html"
}

function home()
{
  location.href = "http://127.0.0.1:5500/homePage/IndexOwner.html"
}
