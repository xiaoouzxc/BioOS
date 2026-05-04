function createParagraph() {
  const para = document.createElement("p");
  para.textContent = "你点击了按钮！";
  document.body.appendChild(para);
}

const buttons = document.querySelector("#getData");


  buttons.addEventListener("click", createParagraph);




