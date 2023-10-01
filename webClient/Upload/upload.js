const dropArea = document.querySelector(".drop_box"),
  button = dropArea.querySelector("button"),
  dragText = dropArea.querySelector("header"),
  input = dropArea.querySelector("input");
let file;
var filename;

button.onclick = () => {
  input.click();
};

input.addEventListener("change", function (e) {
    var fileName = e.target.files[0];
    console.log(fileName);

    const reader = new FileReader();
    reader.addEventListener('load', (event) => {
    let data = event.target.result;
    console.log(data);
    var formData = new FormData();
    formData.append("file_name", fileName.name);
    formData.append("file_size", fileName.size);
    formData.append("file", fileName);

    var xhr = new XMLHttpRequest();

    xhr.open("POST", "http://10.0.0.48:8081/", true);
    xhr.setRequestHeader('Content-Type', 'multipart/form-data');
    xhr.setRequestHeader('code', '301');
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
  });
    reader.readAsDataURL(fileName);
});

function home()
{
  location.href = "http://127.0.0.1:5500/homePage/IndexOwner.html"
}