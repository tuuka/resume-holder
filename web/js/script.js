function setInputWidth(inputElement) {
    let MAX_WIDTH = 320,
        MAX_COLS = 40,
        canvas = document.createElement("canvas"),
        context = canvas.getContext("2d");
    context.font = window.getComputedStyle(inputElement).font;
    let textLen = inputElement.value.length,
        rows = Math.ceil((textLen + 1) / MAX_COLS),
        name = inputElement.getAttribute("name"),
        width = context.measureText(inputElement.value).width;
    if (width > MAX_WIDTH) {
        if (inputElement.tagName.toLowerCase() === "input") {
            let newNode = document.createElement('textarea');
            newNode.className = 'resume-edit__input';
            newNode.value = inputElement.value;
            newNode.setAttribute("cols", `${MAX_COLS}`);
            newNode.setAttribute("name", name);
            newNode.style.resize = "none";
            newNode.addEventListener('input', event => setInputWidth(event.target));
            inputElement.parentNode.insertBefore(newNode, inputElement);
            inputElement.remove();
            inputElement = newNode;
            inputElement.focus();
        }
    } else {
        inputElement.style.width = Math.min(Math.max(100, Math.ceil(width) + 5), MAX_WIDTH) + 'px';
    }
    if (inputElement.tagName.toLowerCase() === "textarea") {
        inputElement.setAttribute("rows", `${rows}`);
    }
}

document.querySelectorAll('.resume-edit__input').forEach(item => {
    setInputWidth(item);
    item.addEventListener('input', event => setInputWidth(event.target));
})

document.querySelector('.resume-edit__input').focus();