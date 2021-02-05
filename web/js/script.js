function saveResume(uuid) {
    let form = document.getElementById('edit_form');
    let inp = form.querySelector('input[name="edit_uuid"]');
    inp.removeAttribute('disabled');
    if (uuid) inp.value = uuid;
    console.log(inp);
    form.submit();
}

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

document.querySelectorAll('.resume-edit__input')
    .forEach(item => {
        setInputWidth(item);
        item.addEventListener('input', event => setInputWidth(event.target));
    })

document.querySelector('.resume-edit__input').focus();

document.querySelectorAll('.resume-edit__delete-icon')
    .forEach(item => item
        .addEventListener('click',
            event => {
                deleteElement(event.target);
            })
    )


document.querySelectorAll('.resume-edit__add-icon')
    .forEach(item => item
        .addEventListener('click',
            event => {
                addElement(event.target);
            })
    )

function deleteElement(elem) {
    let parentLi = elem.closest('li');
    let parentUl = parentLi.closest('ul');
    parentLi.remove();
    if (parentUl.classList.contains('resume-edit__org-list')) {
        reindexPosition(parentUl);
    }
}

function reindexPosition(orgList) {
    orgList.querySelectorAll('.resume-edit__org')
        .forEach((orgLI, orgIndex)=> {
            orgLI.querySelectorAll('input')
                .forEach(item => {
                    let itemNameAttr = item.getAttribute('name');
                    if (itemNameAttr.search('^\\w+_\\d+_\\w+$') >= 0){
                        let nameArr = itemNameAttr.split('_');
                        nameArr[1] = orgIndex;
                        item.setAttribute('name', nameArr.join('_'));
                    }
                });
        });
}

function addElement(target) {
    let parentLi = target.closest('li');
    //check if organization/text-list was chosen to add
    if (parentLi.classList.contains('resume-edit__sections-item')) {
        let sectionTypeName = target.closest('.resume-edit__section_title')
            .dataset.sectionType;
        //check if it was a text-list
        if (parentLi.querySelector('.resume-edit__text-list')){
            addText(parentLi.querySelector('.resume-edit__text-list'),
                sectionTypeName);
        } //or it was an organization
        else {
            addOrganization(parentLi.querySelector('.resume-edit__org-list'),
                sectionTypeName);
        }
    }
    // check if a position in the organization was chosen to add
    if (parentLi.classList.contains('resume-edit__org')) {
        let closestOrgList = target.closest('.resume-edit__org-list');
        let sectionTypeName = closestOrgList
            .closest('.resume-edit__sections-item')
            .querySelector('.resume-edit__section_title')
            .dataset.sectionType;
        // May be orgIndex should be calculated here to avoid reindexing later
        addPosition(0,
            sectionTypeName,
            parentLi.querySelector('.resume-edit__positions-list'));
        reindexPosition(closestOrgList);
    }

}

function addText(parent, sectionType) {
    let textHtml =
`<span class="resume-edit__delete-icon">
    <img src="${pageContext}/img/delete.svg" alt="">
</span><label><input type="text" class="resume-edit__input"
         name="${sectionType.toString()}" value=""></label>`;
    let newTextElem = document.createElement('li');
    newTextElem.classList.add('resume-edit__text');
    newTextElem.insertAdjacentHTML('afterbegin', textHtml);
    insertAndSetListener(parent, newTextElem);
}

function addOrganization(orgList, sectionType) {
    let newOrgHtml =
`<span class="resume-edit__add-icon">
    <img src="${pageContext}/img/add.svg" alt="">
</span>
<span class="resume-edit__delete-icon">
    <img src="${pageContext}/img/delete.svg" alt="">
</span>
<label class="resume-edit__org-name">Organization: <input 
    type="text" class="resume-edit__input"
           name="${sectionType}" value="empty">
</label>
<label class="resume-edit__org-link">URL: <input 
    type="text" class="resume-edit__input"
           name="${sectionType}_url"
           value="">
</label>
<ul class="resume-edit__positions-list"></ul>`

    let newOrgElem = document.createElement('li');
    newOrgElem.classList.add('resume-edit__org');
    newOrgElem.insertAdjacentHTML('afterbegin', newOrgHtml);
    insertAndSetListener(orgList, newOrgElem);
    reindexPosition(orgList);
}

function addPosition(orgIndex, sectionType, positionsList) {
    let newPosHtml =
`<span class="resume-edit__delete-icon"> <img src="${pageContext}/img/delete.svg" alt=""> </span>
<label>From: <input 
    type="text" class="resume-edit__input" name="${sectionType}_${orgIndex}_posstart" value="01/2000"> </label>
<label>to: <input type="text" class="resume-edit__input" name="${sectionType}_${orgIndex}_posend" value="01/2000"> </label>
<label>Title: <input type="text" class="resume-edit__input" name="${sectionType}_${orgIndex}_postitle" value="empty"> </label>
<label>Description: <input type="text" class="resume-edit__input" name="${sectionType}_${orgIndex}_posdescr" value="empty"> </label>`

    let newPosElem = document.createElement('li');
    newPosElem.classList.add('resume-edit__position');
    newPosElem.insertAdjacentHTML('afterbegin', newPosHtml);
    insertAndSetListener(positionsList, newPosElem);
}

function insertAndSetListener(parent, elem){
    parent.insertBefore(elem, parent.firstChild);
    elem.querySelectorAll('.resume-edit__add-icon')
        .forEach(item => item
                .addEventListener('click',
                    event => addElement(event.target))
        );
    elem.querySelectorAll('.resume-edit__delete-icon')
        .forEach(item => item
                .addEventListener('click',
                    event => deleteElement(event.target))
        );
    elem.querySelectorAll('input')
        .forEach(item => setInputWidth(item))
}